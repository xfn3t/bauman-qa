package ru.bauman.ui.pages;

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

    public void open() {
        driver.get("http://uitestingplayground.com/sampleapp");
    }

    public void enterUserName(String userName) {
        userNameField.clear();
        userNameField.sendKeys(userName);
    }

    public void enterPassword(String password) {
        passwordField.clear();
        passwordField.sendKeys(password);
    }

    public void clickLogin() {
        loginButton.click();
    }

    public String getLoginStatus() {
        return loginStatus.getText();
    }
}