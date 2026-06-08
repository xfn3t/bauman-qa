package ru.bauman.ui.tests;

import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;
import ru.bauman.ui.pages.FramesPage;

@Epic("UI Testing Playground")
@Feature("Фреймы (iframes)")
public class FramesTest extends BaseTest {

    @Test(description = "Проверка наличия текста во фреймах")
    @Story("Навигация по iframe")
    @Severity(SeverityLevel.NORMAL)
    @Description(
        "Проверка, что в каждом из двух фреймов есть текстовое содержимое"
    )
    public void testFramesContent() {
        FramesPage framesPage = new FramesPage(driver);
        framesPage.open();

        String frame1Text = framesPage.getFrame1BodyText();
        String frame2Text = framesPage.getFrame2BodyText();

        Allure.addAttachment("Frame 1 Content", "text/plain", frame1Text);
        Allure.addAttachment("Frame 2 Content", "text/plain", frame2Text);

        Assert.assertFalse(frame1Text.isBlank(), "Frame1 is empty");
        Assert.assertFalse(frame2Text.isBlank(), "Frame2 is empty");
    }
}
