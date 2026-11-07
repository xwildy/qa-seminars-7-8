package ru.qa.course.core;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import ru.qa.course.pages.CatalogPage;
import ru.qa.course.pages.LoginPage;

import java.io.ByteArrayInputStream;

/**
 * Базовый класс тестов.
 *
 * Изоляция: драйвер НЕ static — каждый тест получает свой браузер
 * (@BeforeEach) и сам выполняет вход. Тесты одинаково проходят отдельно,
 * в наборе и в любом порядке.
 */
public abstract class BaseTest {

    protected WebDriver driver;

    /**
     * Скриншот при падении теста прикладывается к Allure-отчёту.
     * Используется AfterTestExecutionCallback, а не TestWatcher: колбэки
     * TestWatcher вызываются уже ПОСЛЕ @AfterEach, когда браузер закрыт,
     * а этот колбэк срабатывает сразу после тела теста — браузер ещё открыт.
     */
    @RegisterExtension
    final AfterTestExecutionCallback screenshotOnFailure = context -> {
        if (context.getExecutionException().isPresent() && driver != null) {
            byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment("Скриншот при падении", "image/png",
                    new ByteArrayInputStream(png), "png");
        }
    };

    @BeforeEach
    void setUpBrowser() {
        // подбор chromedriver под установленную версию Chrome
        ChromeDriverResolver.resolve();
        driver = new ChromeDriver();
    }

    @AfterEach
    void tearDownBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Step("Вход тестовым пользователем")
    protected CatalogPage loginAsTestUser() {
        return new LoginPage(driver)
                .open()
                .loginAs(Config.LOGIN_EMAIL, Config.LOGIN_PASSWORD);
    }
}
