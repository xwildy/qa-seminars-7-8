package ru.qa.course.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import ru.qa.course.core.Config;

/**
 * Page Object экрана входа. ВНИМАНИЕ: ожидания НЕ реализованы —
 * приложение отдаёт элементы с задержкой, тесты без ожиданий нестабильны.
 */
public class LoginPage {

    private final WebDriver driver;

    private final By emailInput = By.cssSelector("[data-test=login-email]");
    private final By passwordInput = By.cssSelector("[data-test=login-password]");
    private final By loginButton = By.cssSelector("[data-test=login-button]");
    private final By errorLabel = By.cssSelector("[data-test=login-error]");
    private final By catalogSection = By.cssSelector("[data-test=section-catalog]");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public LoginPage open() {
        driver.get(Config.APP_URL);
        driver.findElement(By.cssSelector("[data-test=nav-login]")).click();
        return this;
    }

    public LoginPage loginAs(String email, String password) {
        driver.findElement(emailInput).sendKeys(email);
        driver.findElement(passwordInput).sendKeys(password);
        driver.findElement(loginButton).click();
        return this;
    }

    public String errorMessage() {
        return driver.findElement(errorLabel).getText();
    }

    public boolean catalogIsVisible() {
        return driver.findElement(catalogSection).isDisplayed();
    }
}
