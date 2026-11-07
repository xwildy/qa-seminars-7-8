package ru.qa.course.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.qa.course.core.Book;

import java.util.List;

/**
 * Page Object каталога. Книги ищутся по data-test (id книги), а не по позиции:
 * приложение перемешивает список при каждой отрисовке.
 */
public class CatalogPage extends BasePage {

    private final By catalogSection = By.cssSelector("[data-test=section-catalog]");
    private final By catalogSpinner = By.cssSelector("[data-test=catalog-spinner]");
    private final By bookRows = By.cssSelector("[data-test=book-list] .book-row");
    private final By bookTitles = By.cssSelector("[data-test=book-list] .book-row .t");
    private final By cartBadge = By.cssSelector("[data-test=cart-badge]");
    private final By cartNavButton = By.cssSelector("[data-test=nav-cart]");

    public CatalogPage(WebDriver driver) {
        super(driver);
    }

    /** Каталог показан, индикатор загрузки скрыт, отрисованы все книги. */
    @Step("Дождаться отрисовки каталога")
    public CatalogPage waitUntilLoaded() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(catalogSection));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(catalogSpinner));
        wait.until(ExpectedConditions.numberOfElementsToBe(bookRows, Book.values().length));
        return this;
    }

    public boolean isVisible() {
        return driver.findElement(catalogSection).isDisplayed();
    }

    public int bookCount() {
        return driver.findElements(bookRows).size();
    }

    /** Названия всех книг каталога (в том порядке, в каком их отрисовало приложение). */
    @Step("Прочитать названия книг каталога")
    public List<String> bookTitles() {
        return wait.until(d -> d.findElements(bookTitles).stream()
                .map(WebElement::getText)
                .toList());
    }

    @Step("Добавить в корзину: {book}")
    public CatalogPage addBookToCart(Book book) {
        click(By.cssSelector("[data-test=add-" + book.id() + "]"));
        return this;
    }

    /** Бейдж обновляется асинхронно: ждём ожидаемого значения (custom condition). */
    @Step("Дождаться значения бейджа корзины «{expected}»")
    public String cartBadgeText(String expected) {
        return textOnceEquals(cartBadge, expected);
    }

    @Step("Открыть корзину")
    public CartPage openCart() {
        click(cartNavButton);
        return new CartPage(driver).waitUntilVisible();
    }
}
