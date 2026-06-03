package ru.bauman.ui.tests;

import ru.bauman.ui.pages.TextInputPage;
import org.testng.Assert;
import org.testng.annotations.Test;
import io.qameta.allure.Description;

public class TextInputTest extends BaseTest {

    @Test
    @Description("Изменение текста кнопки через поле ввода")
    public void testTextInput() {
        TextInputPage textInputPage = new TextInputPage(driver);
        textInputPage.open();
        String newButtonName = "MyNewButton";
        textInputPage.enterButtonName(newButtonName);
        textInputPage.clickUpdateButton();
        Assert.assertEquals(textInputPage.getButtonText(), newButtonName);
    }
}