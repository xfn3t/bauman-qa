package ru.bauman.ui.tests;

import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;
import ru.bauman.ui.pages.SampleAppPage;

@Epic("UI Testing Playground")
@Feature("Авторизация")
public class SampleAppTest extends BaseTest {

    @Test(description = "Успешный логин с валидными данными")
    @Story("Логин пользователя")
    @Severity(SeverityLevel.BLOCKER)
    @Description(
        "Проверка успешной авторизации с корректными username и password"
    )
    public void testSampleAppLogin() {
        SampleAppPage sampleApp = new SampleAppPage(driver);
        sampleApp.open();

        String username = "TestUser";
        String password = "pwd";

        Allure.parameter("username", username);
        Allure.parameter("password", password);

        sampleApp.enterUserName(username);
        sampleApp.enterPassword(password);
        sampleApp.clickLogin();

        String expectedStatus = "Welcome, " + username + "!";
        String actualStatus = sampleApp.getLoginStatus();

        Assert.assertEquals(
            actualStatus,
            expectedStatus,
            "Статус логина не совпадает с ожидаемым"
        );
    }
}
