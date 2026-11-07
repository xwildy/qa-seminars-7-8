package ru.qa.course.core;

import java.nio.file.Paths;

/**
 * Конфигурация тестового набора.
 */
public final class Config {

    public static final String APP_URL =
            Paths.get("test-app", "shop.html").toAbsolutePath().toUri().toString();

    public static final String LOGIN_EMAIL = "qa@bookmarket.local";
    public static final String LOGIN_PASSWORD = "correct-horse";

    private Config() {
    }
}
