package ru.qa.course.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.qa.course.core.BaseTest;
import ru.qa.course.core.Config;
import ru.qa.course.pages.CatalogPage;
import ru.qa.course.pages.LoginPage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Вход в BookMarket")
class LoginTest extends BaseTest {

    @Test
    @DisplayName("Успешный вход открывает каталог")
    void successfulLoginShowsCatalog() {
        CatalogPage catalog = loginAsTestUser();

        assertTrue(catalog.isVisible());
        assertEquals("Каталог", catalog.title());
    }

    @Test
    @DisplayName("Неверный пароль: сообщение об ошибке, каталог не открыт")
    void wrongPasswordShowsErrorAndKeepsCatalogHidden() {
        LoginPage login = new LoginPage(driver)
                .open()
                .loginExpectingError(Config.LOGIN_EMAIL, Config.WRONG_PASSWORD);

        assertEquals("Неверный email или пароль", login.errorMessage());
        assertFalse(new CatalogPage(driver).isVisible());
    }
}
