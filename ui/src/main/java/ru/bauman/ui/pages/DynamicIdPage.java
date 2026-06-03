package ru.bauman.ui.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class DynamicIdPage extends BasePage {

    @FindBy(xpath = "//button[text()='Button with Dynamic ID']")
    private WebElement dynamicButton;

    public DynamicIdPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        driver.get("http://uitestingplayground.com/dynamicid");
    }

    public void clickDynamicButton() {
        dynamicButton.click();
    }

    public boolean isButtonDisplayed() {
        return dynamicButton.isDisplayed();
    }
}