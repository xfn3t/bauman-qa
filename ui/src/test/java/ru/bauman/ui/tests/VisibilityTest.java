package ru.bauman.ui.tests;

import io.qameta.allure.Description;
import org.testng.Assert;
import org.testng.annotations.Test;
import ru.bauman.ui.pages.VisibilityPage;

public class VisibilityTest extends BaseTest {

    @Test
    @Description("Проверка скрытия элементов после нажатия Hide")
    public void testVisibility() {
        VisibilityPage page = new VisibilityPage(driver);
        page.open();

        Assert.assertTrue(page.isRemovedButtonPresent(), "removedButton should exist initially");
        Assert.assertTrue(page.getZeroWidthButtonWidth() > 0, "zeroWidthButton should have width > 0 initially");

        page.clickHideButton();

        Assert.assertFalse(page.isRemovedButtonPresent(), "removedButton should disappear");
        Assert.assertEquals(page.getZeroWidthButtonWidth(), 0, "zeroWidthButton width should become 0");
    }
}