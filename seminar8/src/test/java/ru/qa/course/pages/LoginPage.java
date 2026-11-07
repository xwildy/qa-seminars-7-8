package ru.qa.course.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.qa.course.core.Config;

/**
 * Page Object экрана входа.
 */
public class LoginPage extends BasePage {

    private final By loginNavButton = By.cssSelector("[data-test=nav-login]");
    private final By emailInput = By.cssSelector("[data-test=login-email]");
    private final By passwordInput = By.cssSelector("[data-test=login-password]");
    private final By loginButton = By.cssSelector("[data-test=login-button]");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    @Step("Открыть стенд BookMarket и экран входа")
    public LoginPage open() {
        driver.get(Config.APP_URL);
        click(loginNavButton);
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput));
        return this;
    }

    /**
     * Вход завершается, только когда каталог полностью отрисован
     * (результат входа приходит с задержкой 0–500 мс).
     */
    @Step("Вход: {email}")
    public CatalogPage loginAs(String email, String password) {
        type(emailInput, email);
        type(passwordInput, password);
        click(loginButton);
        return new CatalogPage(driver).waitUntilLoaded();
    }
}
