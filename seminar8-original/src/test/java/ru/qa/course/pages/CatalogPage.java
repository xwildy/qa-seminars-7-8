package ru.qa.course.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object каталога. ВНИМАНИЕ: ожиданий нет; addBookByPosition
 * использует локатор по позиции (приложение перемешивает книги) —
 * источник нестабильности.
 */
public class CatalogPage {

    private final WebDriver driver;

    private final By badge = By.cssSelector("[data-test=cart-badge]");
    private final By cartLink = By.cssSelector("[data-test=nav-cart]");

    public CatalogPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isVisible() {
        return driver.findElement(By.cssSelector("[data-test=section-catalog]")).isDisplayed();
    }

    public int bookCount() {
        return driver.findElements(
                By.cssSelector("#book-list div.book-row")).size();
    }

    /** Кнопка «В корзину» у книги, стоящей на заданной позиции в списке. */
    public void addBookByPosition(int position) {
        driver.findElement(By.xpath(
                "//div[@id='book-list']/div[" + position + "]/button")).click();
    }

    /** Название первой книги в списке (тесты предполагают стабильный порядок). */
    public String firstBookTitle() {
        return driver.findElement(
                By.cssSelector("#book-list div.book-row .t")).getText();
    }

    public String badgeText() {
        return driver.findElement(badge).getText();
    }

    public CartPage openCart() {
        driver.findElement(cartLink).click();
        return new CartPage(driver);
    }
}
