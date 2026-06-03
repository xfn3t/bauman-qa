package ru.bauman.ui.pages;

import io.qameta.allure.Step;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class FramesPage extends BasePage {

    public FramesPage(WebDriver driver) {
        super(driver);
    }

    @Step("Открытие страницы Frames")
    public void open() {
        log.info("Opening Frames page");
        driver.get("http://uitestingplayground.com/frames");
    }

    @Step("Получение текста из Frame 1")
    public String getFrame1BodyText() {
        log.info("Switching to frame 1");
        driver.switchTo().frame(0);
        String text = driver.findElement(By.tagName("body")).getText();
        log.info("Frame 1 text length: {} chars", text.length());
        driver.switchTo().defaultContent();
        return text;
    }

    @Step("Получение текста из Frame 2 (вложенный iframe)")
    public String getFrame2BodyText() {
        log.info("Switching to frame 2");
        driver.switchTo().frame(0);
        List<org.openqa.selenium.WebElement> nestedFrames = driver.findElements(
            By.tagName("iframe")
        );
        log.info("Nested iframes count: {}", nestedFrames.size());

        driver.switchTo().frame(0);
        String text = driver.findElement(By.tagName("body")).getText();
        log.info("Frame 2 text length: {} chars", text.length());
        driver.switchTo().defaultContent();
        return text;
    }
}
