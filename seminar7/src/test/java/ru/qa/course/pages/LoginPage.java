package ru.qa.course.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import ru.qa.course.core.Config;

/**
 * Page Object экрана входа.
 */
public class LoginPage {

    private final WebDriver driver;

    private final By loginNavButton = By.cssSelector("[data-test=nav-login]");
    private final By emailInput = By.cssSelector("[data-test=login-email]");
    private final By passwordInput = By.cssSelector("[data-test=login-password]");
    private final By loginButton = By.cssSelector("[data-test=login-button]");
    private final By errorLabel = By.cssSelector("[data-test=login-error]");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    /** Открыть стенд и перейти на экран входа. */
    public LoginPage open() {
        driver.get(Config.APP_URL);
        driver.findElement(loginNavButton).click();
        return this;
    }

    /** Успешный вход: после него пользователь попадает в каталог. */
    public CatalogPage loginAs(String email, String password) {
        submitCredentials(email, password);
        return new CatalogPage(driver);
    }

    /** Вход с некорректными данными: пользователь остаётся на экране входа. */
    public LoginPage loginExpectingError(String email, String password) {
        submitCredentials(email, password);
        return this;
    }

    /** Текст сообщения об ошибке входа. */
    public String errorMessage() {
        return driver.findElement(errorLabel).getText();
    }

    private void submitCredentials(String email, String password) {
        driver.findElement(emailInput).sendKeys(email);
        driver.findElement(passwordInput).sendKeys(password);
        driver.findElement(loginButton).click();
    }
}
