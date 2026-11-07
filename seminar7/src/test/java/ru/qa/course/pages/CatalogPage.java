package ru.qa.course.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import ru.qa.course.core.Book;

/**
 * Page Object каталога: список книг, добавление в корзину, бейдж корзины.
 */
public class CatalogPage {

    private final WebDriver driver;

    private final By catalogSection = By.cssSelector("[data-test=section-catalog]");
    private final By title = By.cssSelector("[data-test=section-catalog] h1");
    private final By cartBadge = By.cssSelector("[data-test=cart-badge]");
    private final By cartNavButton = By.cssSelector("[data-test=nav-cart]");

    public CatalogPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isVisible() {
        return driver.findElement(catalogSection).isDisplayed();
    }

    public String title() {
        return driver.findElement(title).getText();
    }

    /** Нажать «В корзину» у конкретной книги — по её data-test, а не по позиции в списке. */
    public CatalogPage addBookToCart(Book book) {
        driver.findElement(addButtonOf(book)).click();
        return this;
    }

    /** Число на бейдже корзины в шапке. */
    public String cartBadgeText() {
        return driver.findElement(cartBadge).getText();
    }

    public CartPage openCart() {
        driver.findElement(cartNavButton).click();
        return new CartPage(driver);
    }

    private By addButtonOf(Book book) {
        return By.cssSelector("[data-test=add-" + book.id() + "]");
    }
}
