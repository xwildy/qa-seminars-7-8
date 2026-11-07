package ru.qa.course.core;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
/**
 * Подбор chromedriver под установленную версию Chrome.
 *
 * Порядок работы (выполняется один раз за прогон тестов):
 * 1. Определить версию Chrome: реестр Windows (BLBeacon), exe на macOS,
 *    либо переменная окружения CHROME_VERSION (можно подставить явно).
 * 2. Если вложенный drivers/chromedriver(.exe) совпадает по мажорной версии —
 *    использовать его.
 * 3. Иначе скачать подходящий драйвер через зеркало npmmirror
 *    (доступно из корпоративных сетей, где googlechromelabs заблокирован)
 *    во временную папку и подменить вложенный драйвер.
 * 4. Найденный путь выставляется в системное свойство
 *    webdriver.chrome.driver — Selenium Manager не обращается к сети.
 *
 * Если ничего не удалось — оставляем вложенный драйвер: Selenium выдаст
 * понятную ошибку несовпадения версий.
 */
public final class ChromeDriverResolver {

    private static final Path DRIVER_DIR;
    private static final Path BUNDLED_DRIVER;
    private static final String NPMMIRROR =
            "https://registry.npmmirror.com/-/binary/chrome-for-testing";

    static {
        // drivers/ лежит в корне проекта (pom.xml передаёт путь через свойство)
        String configured = System.getProperty("project.basedir", ".");
        DRIVER_DIR = Paths.get(configured, "drivers");
        BUNDLED_DRIVER = DRIVER_DIR.resolve(isWindows()
                ? "chromedriver.exe" : "chromedriver");
    }

    private ChromeDriverResolver() {
    }

    private static boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase().contains("win");
    }

    private static boolean isMac() {
        return System.getProperty("os.name", "").toLowerCase().contains("mac");
    }

    /** Версия Chrome: переменная окружения CHROME_VERSION имеет приоритет. */
    private static String chromeVersion() {
        String env = System.getenv("CHROME_VERSION");
        if (env != null && !env.isBlank()) {
            return env.trim();
        }
        if (isWindows()) {
            return chromeVersionWindows();
        }
        if (isMac()) {
            return chromeVersionMac();
        }
        return null;
    }

    /** Версия Chrome из реестра Windows. */
    private static String chromeVersionWindows() {
        String[] keys = {
                "HKCU\\Software\\Google\\Chrome\\BLBeacon",
                "HKLM\\Software\\Google\\Chrome\\BLBeacon"
        };
        for (String key : keys) {
            try {
                Process p = new ProcessBuilder("reg", "query", key, "/v", "version")
                        .start();
                String out = new String(p.getInputStream().readAllBytes(),
                        StandardCharsets.UTF_8);
                p.waitFor();
                Matcher m = Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+)").matcher(out);
                if (m.find()) {
                    return m.group(1);
                }
            } catch (Exception ignored) {
                // пробуем следующий источник
            }
        }
        return null;
    }

    /** Версия Chrome на macOS. */
    private static String chromeVersionMac() {
        Path exe = Paths.get("/Applications/Google Chrome.app/Contents/MacOS/Google Chrome");
        if (Files.exists(exe)) {
            try {
                Process p = new ProcessBuilder(exe.toString(), "--version").start();
                String out = new String(p.getInputStream().readAllBytes(),
                        StandardCharsets.UTF_8).trim();
                if (out.contains(" ")) {
                    return out.substring(out.lastIndexOf(' ') + 1);
                }
            } catch (Exception ignored) {
                // не удалось — вернём null
            }
        }
        return null;
    }

    /** Мажорная версия вложенного драйвера (или null, если файла нет). */
    private static String bundledMajor() {
        if (!Files.exists(BUNDLED_DRIVER)) {
            return null;
        }
        try {
            Process p = new ProcessBuilder(BUNDLED_DRIVER.toString(), "--version").start();
            String out = new String(p.getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8);
            Matcher m = Pattern.compile("(\\d+)\\.\\d+\\.\\d+\\.\\d+").matcher(out);
            return m.find() ? m.group(1) : null;
        } catch (Exception e) {
            return null;
        }
    }

    /** Скачивает драйвер нужной мажорной версии через npmmirror. */
    private static Path downloadDriver(String major) {
        try {
            String listing = httpGet(NPMMIRROR);
            List<String> versions = new ArrayList<>();
            Matcher m = Pattern.compile("chrome-for-testing/(\\d+\\.\\d+\\.\\d+\\.\\d+)/")
                    .matcher(listing);
            while (m.find()) {
                String v = m.group(1);
                if (v.split("\\.")[0].equals(major)) {
                    versions.add(v);
                }
            }
            if (versions.isEmpty()) {
                System.err.println("[chromedriver] нет сборки для версии " + major);
                return null;
            }
            versions.sort(Comparator.comparing((String v) ->
                    Long.parseLong(v.split("\\.")[3])));
            String chosen = versions.get(versions.size() - 1);

            String platform = isWindows() ? "win64" : (isMac() ? "mac-arm64" : "linux64");
            String fileName = "chromedriver-" + platform + ".zip";
            String url = NPMMIRROR + "/" + chosen + "/" + fileName;
            System.err.println("[chromedriver] скачиваю " + chosen + " ...");
            byte[] data = httpGetBytes(url);

            Path zip = Files.createTempFile("chromedriver-", ".zip");
            Files.write(zip, data);
            Path target = DRIVER_DIR.resolve(isWindows()
                    ? "chromedriver.exe" : "chromedriver");
            Files.createDirectories(DRIVER_DIR);
            try (ZipFile zf = new ZipFile(zip.toFile())) {
                ZipEntry entry = zf.stream()
                        .filter(e -> e.getName().endsWith(
                                isWindows() ? "chromedriver.exe" : "chromedriver"))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException(
                                "chromedriver не найден в архиве"));
                Files.copy(zf.getInputStream(entry), target,
                        StandardCopyOption.REPLACE_EXISTING);
            }
            Files.deleteIfExists(zip);
            if (!isWindows()) {
                try {
                    new ProcessBuilder("chmod", "+x", target.toString()).start().waitFor();
                } catch (Exception ignored) {
                    // не критично
                }
            }
            System.err.println("[chromedriver] установлен " + chosen);
            return target;
        } catch (Exception e) {
            System.err.println("[chromedriver] скачивание не удалось: " + e);
            return null;
        }
    }

    private static String httpGet(String url) {
        try {
            return new String(httpGetBytes(url), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("HTTP GET failed: " + url, e);
        }
    }

    private static byte[] httpGetBytes(String url)
            throws IOException, InterruptedException {
        java.net.http.HttpClient client = java.net.http.HttpClient.newBuilder()
                .followRedirects(java.net.http.HttpClient.Redirect.ALWAYS).build();
        java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create(url))
                .header("User-Agent", "Mozilla/5.0")
                .GET().build();
        return client.send(request, java.net.http.HttpResponse.BodyHandlers.ofByteArray())
                .body();
    }

    /**
     * Возвращает путь к подходящему chromedriver и выставляет системное
     * свойство webdriver.chrome.driver. Вызывать один раз до создания драйвера.
     */
    public static String resolve() {
        Path resolved;
        String version = chromeVersion();
        String bundledMajor = bundledMajor();

        if (version == null) {
            System.err.println("[chromedriver] версия Chrome не определена, "
                    + "использую вложенный драйвер");
            resolved = BUNDLED_DRIVER;
        } else {
            String major = version.split("\\.")[0];
            if (major.equals(bundledMajor)) {
                resolved = BUNDLED_DRIVER;
            } else {
                System.err.println("[chromedriver] Chrome " + version
                        + ", вложенный драйвер " + bundledMajor + " — подбираю нужный");
                Path downloaded = downloadDriver(major);
                resolved = downloaded != null ? downloaded : BUNDLED_DRIVER;
            }
        }

        if (resolved != null && Files.exists(resolved)) {
            System.setProperty("webdriver.chrome.driver", resolved.toString());
            return resolved.toString();
        }
        return null;
    }
}
