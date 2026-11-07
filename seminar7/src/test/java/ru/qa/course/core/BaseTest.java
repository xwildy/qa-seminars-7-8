package ru.qa.course.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import ru.qa.course.pages.CatalogPage;
import ru.qa.course.pages.LoginPage;

/**
 * Базовый класс тестов: жизненный цикл браузера и общие сценарии.
 * Каждый тест получает свой браузер (поле не static) — тесты не зависят друг от друга.
 */
public abstract class BaseTest {

    protected WebDriver driver;

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

    /** Вход тестовым пользователем — один метод вместо четырёх копий. */
    protected CatalogPage loginAsTestUser() {
        return new LoginPage(driver)
                .open()
                .loginAs(Config.LOGIN_EMAIL, Config.LOGIN_PASSWORD);
    }
}
