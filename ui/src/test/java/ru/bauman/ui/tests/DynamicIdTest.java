package ru.bauman.ui.tests;

import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;
import ru.bauman.ui.pages.DynamicIdPage;

@Epic("UI Testing Playground")
@Feature("Динамические элементы")
public class DynamicIdTest extends BaseTest {

    @Test(description = "Клик по кнопке с динамическим ID")
    @Story("Работа с динамическими ID")
    @Severity(SeverityLevel.CRITICAL)
    @Description(
        "Проверка, что кнопка с динамически генерируемым ID отображается и кликабельна"
    )
    public void testDynamicIdButtonClick() {
        DynamicIdPage dynamicIdPage = new DynamicIdPage(driver);
        dynamicIdPage.open();

        Assert.assertTrue(
            dynamicIdPage.isButtonDisplayed(),
            "Кнопка не отображается"
        );

        dynamicIdPage.clickDynamicButton();

        Assert.assertTrue(
            driver.getCurrentUrl().contains("dynamicid"),
            "URL не содержит 'dynamicid', текущий URL: " +
                driver.getCurrentUrl()
        );
    }
}
