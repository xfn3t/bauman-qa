package ru.bauman.ui.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class VisibilityPage extends BasePage {

    private final By hideButton = By.id("hideButton");
    private final By removedButton = By.id("removedButton");
    private final By zeroWidthButton = By.id("zeroWidthButton");

    public VisibilityPage(WebDriver driver) {
        super(driver);
    }

    @Step("Открытие страницы Visibility")
    public void open() {
        log.info("Opening Visibility page");
        driver.get("https://www.uitestingplayground.com/visibility");
        wait.until(ExpectedConditions.visibilityOfElementLocated(hideButton));
        log.info("Visibility page loaded, hide button is visible");
    }

    @Step("Клик по кнопке Hide")
    public void clickHideButton() {
        log.info("Clicking hide button");
        wait.until(ExpectedConditions.elementToBeClickable(hideButton)).click();
    }

    @Step("Проверка наличия removedButton в DOM")
    public boolean isRemovedButtonPresent() {
        boolean present = !driver.findElements(removedButton).isEmpty();
        log.info("Removed button present: {}", present);
        return present;
    }

    @Step("Получение ширины zeroWidthButton")
    public int getZeroWidthButtonWidth() {
        WebElement element = wait.until(
            ExpectedConditions.presenceOfElementLocated(zeroWidthButton)
        );
        int width = element.getSize().getWidth();
        log.info("ZeroWidth button width: {}px", width);
        return width;
    }
}
