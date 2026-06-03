package ru.bauman.ui.tests;

import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;
import ru.bauman.ui.pages.VisibilityPage;

@Epic("UI Testing Playground")
@Feature("Видимость элементов")
public class VisibilityTest extends BaseTest {

    @Test(description = "Проверка скрытия элементов после нажатия Hide")
    @Story("Скрытие элементов")
    @Severity(SeverityLevel.NORMAL)
    @Description(
        "Проверка, что после нажатия кнопки Hide: removedButton исчезает из DOM, " +
            "а zeroWidthButton получает ширину 0"
    )
    public void testVisibility() {
        VisibilityPage page = new VisibilityPage(driver);
        page.open();

        Assert.assertTrue(
            page.isRemovedButtonPresent(),
            "removedButton should exist initially"
        );
        Assert.assertTrue(
            page.getZeroWidthButtonWidth() > 0,
            "zeroWidthButton should have width > 0 initially"
        );

        page.clickHideButton();

        Assert.assertFalse(
            page.isRemovedButtonPresent(),
            "removedButton should disappear after hide"
        );
        Assert.assertEquals(
            page.getZeroWidthButtonWidth(),
            0,
            "zeroWidthButton width should become 0 after hide"
        );
    }
}
