package ru.bauman.ui.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import java.lang.reflect.Method;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest {

    private static final Logger log = LoggerFactory.getLogger(BaseTest.class);

    protected WebDriver driver;

    @BeforeMethod
    public void setUp(Method method) {
        String browser = System.getProperty("browser", "firefox");
        log.info("Test [{}] | browser: {}", method.getName(), browser);

        switch (browser.toLowerCase()) {
            case "chrome" -> {
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                driver = new ChromeDriver(chromeOptions);
            }
            case "edge" -> {
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                driver = new EdgeDriver(edgeOptions);
            }
            default -> {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                driver = new FirefoxDriver(firefoxOptions);
            }
        }

        driver.manage().window().maximize();
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            log.error("FAILED: {}", result.getName());
            if (result.getThrowable() != null) {
                log.error("Reason: {}", result.getThrowable().getMessage());
            }
            if (driver != null) {
                takeScreenshot(result.getName());
            }
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            log.info("PASSED: {}", result.getName());
        } else if (result.getStatus() == ITestResult.SKIP) {
            log.warn("SKIPPED: {}", result.getName());
        }

        if (driver != null) {
            driver.quit();
        }
    }

    @Attachment(value = "Screenshot on failure: {testName}", type = "image/png")
    public byte[] takeScreenshot(String testName) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    @Step("{stepDescription}")
    public void logStep(String stepDescription) {
        log.info("{}", stepDescription);
    }
}
