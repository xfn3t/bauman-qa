package ru.bauman.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class FramesPage extends BasePage {

    public FramesPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        driver.get("http://uitestingplayground.com/frames");
    }

    public String getFrame1BodyText() {
        driver.switchTo().frame(0);

        String text = driver.findElement(By.tagName("body")).getText();

        System.out.println("FRAME1:");
        System.out.println(text);

        driver.switchTo().defaultContent();

        return text;
    }

    public String getFrame2BodyText() {
        driver.switchTo().frame(0);

        System.out.println("Nested iframes: " +
                driver.findElements(By.tagName("iframe")).size());

        driver.switchTo().frame(0);

        String text = driver.findElement(By.tagName("body")).getText();

        System.out.println("FRAME2:");
        System.out.println(text);

        driver.switchTo().defaultContent();

        return text;
    }
}