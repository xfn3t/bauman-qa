package ru.bauman.ui.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class DynamicIdPage extends BasePage {

    @FindBy(xpath = "//button[text()='Button with Dynamic ID']")
    private WebElement dynamicButton;

    public DynamicIdPage(WebDriver driver) {
        super(driver);
    }

    @Step("Открытие страницы Dynamic ID")
    public void open() {
        log.info("Opening Dynamic ID page");
        driver.get("http://uitestingplayground.com/dynamicid");
    }

    @Step("Клик по кнопке с динамическим ID")
    public void clickDynamicButton() {
        log.info("Clicking dynamic button");
        dynamicButton.click();
    }

    @Step("Проверка отображения кнопки")
    public boolean isButtonDisplayed() {
        boolean displayed = dynamicButton.isDisplayed();
        log.info("Button is displayed: {}", displayed);
        return displayed;
    }
}
