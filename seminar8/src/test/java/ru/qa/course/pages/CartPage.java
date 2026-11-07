package ru.qa.course.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/**
 * Page Object корзины. Корзина перерисовывается с задержкой,
 * поэтому значения читаются только после ожидания нужного состояния.
 */
public class CartPage extends BasePage {

    private final By cartSection = By.cssSelector("[data-test=section-cart]");
    private final By cartLines = By.cssSelector("[data-test=cart-line] span:first-child");
    private final By total = By.cssSelector("[data-test=cart-total]");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    @Step("Дождаться открытия корзины")
    public CartPage waitUntilVisible() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(cartSection));
        return this;
    }

    /**
     * Итог и строки корзины рисуются одним и тем же обновлением,
     * поэтому после ожидания итога строки уже актуальны.
     */
    @Step("Дождаться итога корзины «{expected}»")
    public String totalText(String expected) {
        return textOnceEquals(total, expected);
    }

    /** Строки корзины вида «Название xКоличество». */
    @Step("Прочитать строки корзины")
    public List<String> lines() {
        return wait.until(d -> d.findElements(cartLines).stream()
                .map(WebElement::getText)
                .toList());
    }
}
