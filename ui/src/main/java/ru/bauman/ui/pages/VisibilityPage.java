package ru.bauman.ui.pages;

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

    public void open() {
        driver.get("https://www.uitestingplayground.com/visibility");
        wait.until(ExpectedConditions.visibilityOfElementLocated(hideButton));
    }

    public void clickHideButton() {
        wait.until(ExpectedConditions.elementToBeClickable(hideButton)).click();
    }

    public boolean isRemovedButtonPresent() {
        return !driver.findElements(removedButton).isEmpty();
    }

    public int getZeroWidthButtonWidth() {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(zeroWidthButton));
        return element.getSize().getWidth();
    }
}