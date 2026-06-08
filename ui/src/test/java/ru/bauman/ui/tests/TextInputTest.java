package ru.bauman.ui.tests;

import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;
import ru.bauman.ui.pages.TextInputPage;

@Epic("UI Testing Playground")
@Feature("Текстовый ввод")
public class TextInputTest extends BaseTest {

    @Test(description = "Изменение текста кнопки через поле ввода")
    @Story("Обновление текста кнопки")
    @Severity(SeverityLevel.NORMAL)
    @Description(
        "Проверка, что текст кнопки обновляется после ввода нового имени в поле"
    )
    public void testTextInput() {
        TextInputPage textInputPage = new TextInputPage(driver);
        textInputPage.open();

        String newButtonName = "MyNewButton";

        Allure.parameter("newButtonName", newButtonName);

        textInputPage.enterButtonName(newButtonName);
        textInputPage.clickUpdateButton();

        String actualButtonText = textInputPage.getButtonText();

        Assert.assertEquals(
            actualButtonText,
            newButtonName,
            "Текст кнопки не совпадает с ожидаемым"
        );
    }
}
