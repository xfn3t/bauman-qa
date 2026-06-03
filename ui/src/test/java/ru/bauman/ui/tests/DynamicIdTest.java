package ru.bauman.ui.tests;

import ru.bauman.ui.pages.DynamicIdPage;
import org.testng.Assert;
import org.testng.annotations.Test;
import io.qameta.allure.Description;

public class DynamicIdTest extends BaseTest {

    @Test
    @Description("Клик по кнопке с динамическим ID")
    public void testDynamicIdButtonClick() {
        DynamicIdPage dynamicIdPage = new DynamicIdPage(driver);
        dynamicIdPage.open();
        Assert.assertTrue(dynamicIdPage.isButtonDisplayed(), "Кнопка не отображается");
        dynamicIdPage.clickDynamicButton();
        Assert.assertTrue(driver.getCurrentUrl().contains("dynamicid"));
    }
}