package ru.qa.course.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.qa.course.core.BaseTest;
import ru.qa.course.core.Book;
import ru.qa.course.pages.CartPage;
import ru.qa.course.pages.CatalogPage;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Корзина BookMarket")
class CartTest extends BaseTest {

    @Test
    @DisplayName("Две добавленные книги отображаются на бейдже корзины")
    void addingTwoBooksUpdatesCartBadge() {
        CatalogPage catalog = loginAsTestUser()
                .addBookToCart(Book.CLEAN_CODE)
                .addBookToCart(Book.CODE_COMPLETE);

        assertEquals("2", catalog.cartBadgeText());
    }

    @Test
    @DisplayName("Итог корзины = сумма цен с учётом количества")
    void cartTotalIsSumOfPricesTimesQuantity() {
        CartPage cart = loginAsTestUser()
                .addBookToCart(Book.CLEAN_CODE)
                .addBookToCart(Book.CODE_COMPLETE)
                .addBookToCart(Book.CODE_COMPLETE)
                .openCart();

        int expectedTotal = Book.CLEAN_CODE.price() + 2 * Book.CODE_COMPLETE.price(); // 4990
        assertEquals(String.valueOf(expectedTotal), cart.totalText());
    }
}
