package ru.qa.course.core;

/**
 * Тестовые данные каталога BookMarket: id (используется в data-test="add-{id}"),
 * название и цена. Цены здесь — вместо «магических» чисел 1290/1850 в тестах.
 */
public enum Book {

    CLEAN_CODE(1, "Чистый код", 1290),
    CODE_COMPLETE(2, "Совершенный код", 1850),
    TESTING_DOT_COM(3, "Тестирование Дот Ком", 990),
    REFACTORING(4, "Рефакторинг", 2100);

    private final int id;
    private final String title;
    private final int price;

    Book(int id, String title, int price) {
        this.id = id;
        this.title = title;
        this.price = price;
    }

    public int id() {
        return id;
    }

    public String title() {
        return title;
    }

    public int price() {
        return price;
    }

    /** Название книги — так параметр {book} выглядит в шагах Allure. */
    @Override
    public String toString() {
        return title;
    }
}
