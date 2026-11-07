package ru.qa.course.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.qa.course.core.BaseTest;
import ru.qa.course.core.Book;
import ru.qa.course.pages.CartPage;
import ru.qa.course.pages.CatalogPage;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Регрессионный набор BookMarket — стабильная версия.
 *
 * Каждый тест независим: свой браузер (BaseTest, @BeforeEach), свой вход,
 * свои данные. Порядок выполнения (@Order) больше не нужен.
 */
@Epic("BookMarket")
@DisplayName("Регрессионный набор BookMarket")
class BookShopRegressionTest extends BaseTest {

    @Test
    @Feature("Вход")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Вход открывает каталог со всеми книгами")
    @Description("Было test1: Thread.sleep(1000) вместо ожидания. Стало: вход ждёт полной отрисовки каталога.")
    void loginShowsCatalogWithAllBooks() {
        CatalogPage catalog = loginAsTestUser();

        assertTrue(catalog.isVisible());
        assertEquals(Book.values().length, catalog.bookCount());
    }

    @Test
    @Feature("Каталог")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Каталог содержит все книги (порядок не важен)")
    @Description("Было test2: проверка «первая книга — Рефакторинг», хотя порядок книг случайный. "
            + "Стало: проверяется состав каталога без привязки к позиции.")
    void catalogContainsAllBooksInAnyOrder() {
        CatalogPage catalog = loginAsTestUser();

        List<String> expected = Arrays.stream(Book.values()).map(Book::title).sorted().toList();
        List<String> actual = catalog.bookTitles().stream().sorted().toList();
        assertEquals(expected, actual);
    }

    @Test
    @Feature("Корзина")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Дважды добавленная книга даёт «2» на бейдже корзины")
    @Description("Было test3: книга по позиции в перемешанном списке и чтение бейджа без ожидания. "
            + "Стало: книга по data-test, ожидание значения бейджа (custom condition).")
    void addingBookTwiceShowsTwoOnBadge() {
        CatalogPage catalog = loginAsTestUser()
                .addBookToCart(Book.REFACTORING)
                .addBookToCart(Book.REFACTORING);

        assertEquals("2", catalog.cartBadgeText("2"));
    }

    @Test
    @Feature("Корзина")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Корзина показывает добавленные книги и итог")
    @Description("Было test4: корзину наполнял test3 (зависимость от порядка). "
            + "Стало: тест сам входит и сам наполняет корзину.")
    void cartShowsAddedBooksAndTotal() {
        CartPage cart = loginAsTestUser()
                .addBookToCart(Book.REFACTORING)
                .addBookToCart(Book.REFACTORING)
                .openCart();

        String expectedTotal = String.valueOf(2 * Book.REFACTORING.price()); // 4200
        assertEquals(expectedTotal, cart.totalText(expectedTotal));
        assertEquals(List.of(Book.REFACTORING.title() + " x2"), cart.lines());
    }
}
