package ru.qa.course;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import ru.qa.course.core.ChromeDriverResolver;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Тесты интернет-магазина BookMarket.
 *
 * FIXME: этот класс — результат "быстрой разработки": всё в одном файле,
 * вход скопирован в каждый тест, локаторы хрупкие (xpath по позиции),
 * url дублируется, магические строки захардкожены.
 * Задача семинара — провести структурный рефакторинг (POM), сохранив
 * поведение тестов.
 */
public class BookShopTests {

    static WebDriver driver;

    @BeforeEach
    void setUpBrowser() {
        // подбор chromedriver под установленную версию Chrome
        ChromeDriverResolver.resolve();
        driver = new ChromeDriver();
    }

    @AfterEach
    void tearDownBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    // ВНИМАНИЕ: url вычисляется в каждом тесте. Не редактируйте по одному месту.
    String appUrl() {
        return Paths.get("test-app", "shop.html").toAbsolutePath().toUri().toString();
    }

    @Test
    void test1() {
        driver.get(appUrl());

        // вход: скопировано из первого рабочего теста, работает, не трогать!
        driver.findElement(By.xpath("/html/body/header/button[1]")).click();
        driver.findElement(By.xpath("/html/body/main/section[1]/input[1]"))
                .sendKeys("qa@bookmarket.local");
        driver.findElement(By.xpath("/html/body/main/section[1]/input[2]"))
                .sendKeys("correct-horse");
        driver.findElement(By.xpath("/html/body/main/section[1]/button[1]")).click();

        // после входа виден каталог
        assertTrue(driver.findElement(By.xpath("/html/body/main/section[2]")).isDisplayed());
        assertEquals("Каталог", driver.findElement(
                By.xpath("/html/body/main/section[2]/h1")).getText());
    }

    @Test
    void test2() {
        driver.get(appUrl());

        // вход с неверным паролем (скопировано из test1, поменян пароль)
        driver.findElement(By.xpath("/html/body/header/button[1]")).click();
        driver.findElement(By.xpath("/html/body/main/section[1]/input[1]"))
                .sendKeys("qa@bookmarket.local");
        driver.findElement(By.xpath("/html/body/main/section[1]/input[2]"))
                .sendKeys("wrong-password");
        driver.findElement(By.xpath("/html/body/main/section[1]/button[1]")).click();

        assertEquals("Неверный email или пароль", driver.findElement(
                By.xpath("/html/body/main/section[1]/div[1]")).getText());
        // каталог НЕ показан
        assertFalse(driver.findElement(By.xpath("/html/body/main/section[2]")).isDisplayed());
    }

    @Test
    void test3() {
        driver.get(appUrl());

        // вход (снова скопировано)
        driver.findElement(By.xpath("/html/body/header/button[1]")).click();
        driver.findElement(By.xpath("/html/body/main/section[1]/input[1]"))
                .sendKeys("qa@bookmarket.local");
        driver.findElement(By.xpath("/html/body/main/section[1]/input[2]"))
                .sendKeys("correct-horse");
        driver.findElement(By.xpath("/html/body/main/section[1]/button[1]")).click();

        // добавляем "Чистый код" в корзину
        driver.findElement(By.xpath("/html/body/main/section[2]/div[1]/div[1]/button")).click();
        // добавляем "Совершенный код" в корзину
        driver.findElement(By.xpath("/html/body/main/section[2]/div[1]/div[2]/button")).click();

        // бейдж корзины = 2
        assertEquals("2", driver.findElement(
                By.xpath("/html/body/header/button[3]/span")).getText());
    }

    @Test
    void test4() {
        driver.get(appUrl());

        // вход (копия #4)
        driver.findElement(By.xpath("/html/body/header/button[1]")).click();
        driver.findElement(By.xpath("/html/body/main/section[1]/input[1]"))
                .sendKeys("qa@bookmarket.local");
        driver.findElement(By.xpath("/html/body/main/section[1]/input[2]"))
                .sendKeys("correct-horse");
        driver.findElement(By.xpath("/html/body/main/section[1]/button[1]")).click();

        // добавляем книги
        driver.findElement(By.xpath("/html/body/main/section[2]/div[1]/div[1]/button")).click();
        driver.findElement(By.xpath("/html/body/main/section[2]/div[1]/div[2]/button")).click();
        driver.findElement(By.xpath("/html/body/main/section[2]/div[1]/div[2]/button")).click();

        // открываем корзину и проверяем итог: 1290 + 1850*2 = 4990
        driver.findElement(By.xpath("/html/body/header/button[3]")).click();
        assertEquals("4990", driver.findElement(
                By.xpath("/html/body/main/section[3]/div[2]/span")).getText());
    }
}
