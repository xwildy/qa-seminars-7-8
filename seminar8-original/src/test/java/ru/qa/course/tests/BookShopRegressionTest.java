package ru.qa.course.tests;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import ru.qa.course.core.BaseTest;
import ru.qa.course.pages.CatalogPage;
import ru.qa.course.pages.CartPage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Регрессионный набор BookMarket. ВНИМАНИЕ: ожиданий нет — приложение
 * перерисовывает экраны с задержкой 0-500 мс и перемешивает книги.
 *
 * Тесты выполняются в порядке @Order и делят ОДИН браузер:
 * тест 1 оставляет приложение в состоянии "вход выполнен", тесты 3-4
 * наполняют корзину. Запуск любого теста отдельно — падение
 * (зависимость от порядка выполнения).
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookShopRegressionTest extends BaseTest {

    @Test
    @Order(1)
    void test1LoginStoresSession() {
        CatalogPage catalog = loginAsTestUser();
        sleep(1000);   // Thread.sleep вместо ожидания — "костыль с запасом"

        assertTrue(catalog.isVisible());
        assertEquals(4, catalog.bookCount());
    }

    @Test
    @Order(2)
    void test2CatalogFirstBookStable() {
        // кликаем по навигации — каталог перерисовывается.
        // Авторы предположили, что порядок книг стабильный.
        driver.findElement(navigationLink("catalog")).click();
        CatalogPage catalog = new CatalogPage(driver);

        assertEquals("Рефакторинг", catalog.firstBookTitle());   // flaky: порядок меняется
    }

    @Test
    @Order(3)
    void test3AddBookByPositionUpdatesBadge() {
        driver.findElement(navigationLink("catalog")).click();
        CatalogPage catalog = new CatalogPage(driver);

        catalog.addBookByPosition(2);   // локатор по позиции — порядок меняется
        catalog.addBookByPosition(2);

        assertEquals("2", catalog.badgeText());   // flaky: бейдж обновляется с задержкой
    }

    @Test
    @Order(4)
    void test4CartShowsAddedBooks() {
        // корзина наполнилась в тесте 3 (зависимость от состояния)
        driver.findElement(navigationLink("cart")).click();
        CartPage cart = new CartPage(driver);

        assertEquals("Рефакторинг", cart.firstLineTitle());   // flaky: состав зависит от порядка
    }
}
