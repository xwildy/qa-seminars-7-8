package ru.qa.course.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object корзины. ВНИМАНИЕ: ожиданий нет — корзина рендерится
 * с задержкой, totalText() может вернуть устаревшее значение.
 */
public class CartPage {

    private final WebDriver driver;

    private final By total = By.cssSelector("[data-test=cart-total]");

    public CartPage(WebDriver driver) {
        this.driver = driver;
    }

    public int cartLineCount() {
        return driver.findElements(
                By.cssSelector("[data-test=cart-line]")).size();
    }

    /** Название в первой строке корзины. */
    public String firstLineTitle() {
        return driver.findElement(
                By.cssSelector("[data-test=cart-line] span")).getText();
    }

    public String totalText() {
        return driver.findElement(total).getText();
    }
}
