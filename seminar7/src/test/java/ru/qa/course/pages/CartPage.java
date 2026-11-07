package ru.qa.course.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object корзины.
 */
public class CartPage {

    private final WebDriver driver;

    private final By cartSection = By.cssSelector("[data-test=section-cart]");
    private final By total = By.cssSelector("[data-test=cart-total]");

    public CartPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isVisible() {
        return driver.findElement(cartSection).isDisplayed();
    }

    /** Итоговая сумма корзины (число без «руб.»). */
    public String totalText() {
        return driver.findElement(total).getText();
    }
}
