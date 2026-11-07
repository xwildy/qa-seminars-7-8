package ru.qa.course.core;

import java.nio.file.Paths;
import java.time.Duration;

/**
 * Конфигурация тестового набора.
 */
public final class Config {

    public static final String APP_URL =
            Paths.get("test-app", "shop.html").toAbsolutePath().toUri().toString();

    public static final String LOGIN_EMAIL = "qa@bookmarket.local";
    public static final String LOGIN_PASSWORD = "correct-horse";

    /**
     * Максимальное время явного ожидания. Приложение отвечает за 0–500 мс,
     * поэтому 5 секунд — запас на медленную машину, а не «sleep с запасом»:
     * ожидание завершается сразу, как только нужное состояние наступило.
     */
    public static final Duration WAIT_TIMEOUT = Duration.ofSeconds(5);

    private Config() {
    }
}
