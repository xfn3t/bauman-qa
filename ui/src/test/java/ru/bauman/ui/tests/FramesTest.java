package ru.bauman.ui.tests;

import ru.bauman.ui.pages.FramesPage;
import io.qameta.allure.Description;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FramesTest extends BaseTest {

    @Test
    @Description("Проверка наличия текста во фреймах")
    public void testFramesContent() {

        FramesPage framesPage = new FramesPage(driver);
        framesPage.open();

        String frame1Text = framesPage.getFrame1BodyText();
        String frame2Text = framesPage.getFrame2BodyText();

        Assert.assertFalse(
                frame1Text.isBlank(),
                "Frame1 is empty"
        );

        Assert.assertFalse(
                frame2Text.isBlank(),
                "Frame2 is empty"
        );

        System.out.println("Frame1:");
        System.out.println(frame1Text);

        System.out.println("Frame2:");
        System.out.println(frame2Text);
    }
}