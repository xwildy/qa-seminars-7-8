package ru.qa.course.core;

import java.nio.file.Paths;

/**
 * Конфигурация тестового набора: ГДЕ приложение и С КАКИМИ данными работаем.
 * Любое изменение адреса или учётных данных — правка только здесь.
 */
public final class Config {

    /** Тестовый стенд: локальная страница test-app/shop.html. */
    public static final String APP_URL =
            Paths.get("test-app", "shop.html").toAbsolutePath().toUri().toString();

    /** Тестовый аккаунт (указан на странице входа стенда). */
    public static final String LOGIN_EMAIL = "qa@bookmarket.local";
    public static final String LOGIN_PASSWORD = "correct-horse";

    /** Заведомо неверный пароль для негативного сценария. */
    public static final String WRONG_PASSWORD = "wrong-password";

    private Config() {
    }
}
