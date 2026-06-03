package ru.bauman.ui.tests;

import ru.bauman.ui.pages.SampleAppPage;
import org.testng.Assert;
import org.testng.annotations.Test;
import io.qameta.allure.Description;

public class SampleAppTest extends BaseTest {

    @Test
    @Description("Логин с валидными данными")
    public void testSampleAppLogin() {
        SampleAppPage sampleApp = new SampleAppPage(driver);
        sampleApp.open();
        sampleApp.enterUserName("TestUser");
        sampleApp.enterPassword("pwd");
        sampleApp.clickLogin();
        Assert.assertEquals(sampleApp.getLoginStatus(), "Welcome, TestUser!");
    }
}