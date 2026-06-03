package ru.bauman.ui.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class TextInputPage extends BasePage {

    @FindBy(id = "newButtonName")
    private WebElement inputField;

    @FindBy(id = "updatingButton")
    private WebElement updateButton;

    public TextInputPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        driver.get("http://uitestingplayground.com/textinput");
    }

    public void enterButtonName(String name) {
        inputField.clear();
        inputField.sendKeys(name);
    }

    public void clickUpdateButton() {
        updateButton.click();
    }

    public String getButtonText() {
        return updateButton.getText();
    }
}