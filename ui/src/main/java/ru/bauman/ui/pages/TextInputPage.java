package ru.bauman.ui.pages;

import io.qameta.allure.Step;
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

    @Step("Открытие страницы Text Input")
    public void open() {
        log.info("Opening Text Input page");
        driver.get("http://uitestingplayground.com/textinput");
    }

    @Step("Ввод имени кнопки: '{name}'")
    public void enterButtonName(String name) {
        log.info("Entering button name: {}", name);
        inputField.clear();
        inputField.sendKeys(name);
    }

    @Step("Клик по кнопке обновления")
    public void clickUpdateButton() {
        log.info("Clicking update button");
        updateButton.click();
    }

    @Step("Получение текста кнопки")
    public String getButtonText() {
        String text = updateButton.getText();
        log.info("Button text: {}", text);
        return text;
    }
}
