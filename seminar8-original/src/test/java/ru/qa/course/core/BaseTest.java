package ru.qa.course.core;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import ru.qa.course.pages.CatalogPage;
import ru.qa.course.pages.LoginPage;

/**
 * Базовый класс. ВНИМАНИЕ: драйвер ОДИН на весь класс (static) —
 * тесты делят браузер и сессию между собой. Это осознанная часть
 * "плохого" проекта: зависимость тестов друг от друга.
 */
public abstract class BaseTest {

    protected static WebDriver driver;

    @BeforeAll
    static void setUpBrowser() {
        // подбор chromedriver под установленную версию Chrome
        ChromeDriverResolver.resolve();
        driver = new ChromeDriver();
    }

    @AfterAll
    static void tearDownBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected static CatalogPage loginAsTestUser() {
        LoginPage login = new LoginPage(driver).open();
        login.loginAs(Config.LOGIN_EMAIL, Config.LOGIN_PASSWORD);
        return new CatalogPage(driver);
    }

    protected static org.openqa.selenium.By navigationLink(String name) {
        return org.openqa.selenium.By.cssSelector("[data-test=nav-" + name + "]");
    }

    protected static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
