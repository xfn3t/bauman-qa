package ru.bauman.ui.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class SampleAppPage extends BasePage {

    @FindBy(name = "UserName")
    private WebElement userNameField;

    @FindBy(name = "Password")
    private WebElement passwordField;

    @FindBy(id = "login")
    private WebElement loginButton;

    @FindBy(id = "loginstatus")
    private WebElement loginStatus;

    public SampleAppPage(WebDriver driver) {
        super(driver);
    }

    @Step("Открытие страницы Sample App")
    public void open() {
        log.info("Opening Sample App page");
        driver.get("http://uitestingplayground.com/sampleapp");
    }

    @Step("Ввод имени пользователя: '{userName}'")
    public void enterUserName(String userName) {
        log.info("Entering username: {}", userName);
        userNameField.clear();
        userNameField.sendKeys(userName);
    }

    @Step("Ввод пароля")
    public void enterPassword(String password) {
        log.info("Entering password");
        passwordField.clear();
        passwordField.sendKeys(password);
    }

    @Step("Клик по кнопке Login")
    public void clickLogin() {
        log.info("Clicking login button");
        loginButton.click();
    }

    @Step("Получение статуса логина")
    public String getLoginStatus() {
        String status = loginStatus.getText();
        log.info("Login status: {}", status);
        return status;
    }
}
