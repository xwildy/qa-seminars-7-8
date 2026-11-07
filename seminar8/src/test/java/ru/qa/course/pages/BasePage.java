package ru.qa.course.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.qa.course.core.Config;

/**
 * Общие приёмы работы со страницей: все действия и чтения — через явные ожидания.
 *
 * - Элемент каждый раз ищется заново по локатору (By), ссылки на WebElement
 *   между действиями не хранятся — StaleElementReferenceException невозможен
 *   по построению, а если элемент перерисуется прямо во время действия,
 *   ожидание просто повторит попытку.
 * - Ни одного Thread.sleep: ожидание длится ровно до нужного состояния.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final FluentWait<WebDriver> wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        // WebDriverWait по умолчанию игнорирует NoSuchElementException;
        // добавляем перерисовку элемента и перекрытие кликом.
        this.wait = new WebDriverWait(driver, Config.WAIT_TIMEOUT)
                .ignoring(StaleElementReferenceException.class)
                .ignoring(ElementClickInterceptedException.class);
    }

    /** Дождаться, пока элемент станет видимым и доступным, и кликнуть по нему. */
    protected void click(By locator) {
        wait.until(d -> {
            WebElement element = d.findElement(locator);
            if (!element.isDisplayed() || !element.isEnabled()) {
                return false;
            }
            element.click();
            return true;
        });
    }

    /** Дождаться видимости поля и ввести текст. */
    protected void type(By locator, String text) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).sendKeys(text);
    }

    /** Текст элемента (элемент ищется заново, перерисовка не страшна). */
    protected String text(By locator) {
        return wait.until(d -> d.findElement(locator).getText());
    }

    /**
     * Custom condition «текст элемента == expected».
     * Ждёт нужного значения; если за таймаут оно не появилось — возвращает
     * фактический текст, чтобы assertEquals в тесте показал понятную разницу
     * «ожидалось / получено», а не голый TimeoutException.
     */
    protected String textOnceEquals(By locator, String expected) {
        try {
            wait.until(d -> expected.equals(d.findElement(locator).getText()));
        } catch (TimeoutException e) {
            // значение не дождались — тест упадёт на проверке с фактическим значением
        }
        return text(locator);
    }
}
