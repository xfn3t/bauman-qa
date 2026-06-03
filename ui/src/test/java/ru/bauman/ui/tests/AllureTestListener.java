package ru.bauman.ui.tests;

import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class AllureTestListener implements ITestListener {

    private static final Logger log = LoggerFactory.getLogger(
        AllureTestListener.class
    );

    @Override
    public void onTestStart(ITestResult result) {
        Allure.parameter("browser", System.getProperty("browser", "firefox"));
        Allure.parameter("java.version", System.getProperty("java.version"));
        Allure.parameter("os.name", System.getProperty("os.name"));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info(
            "PASSED: {}.{}",
            result.getTestClass().getName(),
            result.getMethod().getMethodName()
        );
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log.error(
            "FAILED: {}.{}",
            result.getTestClass().getName(),
            result.getMethod().getMethodName()
        );

        Object testInstance = result.getInstance();
        if (testInstance instanceof BaseTest) {
            WebDriver driver = ((BaseTest) testInstance).driver;
            if (driver != null) {
                saveScreenshot(driver, result.getMethod().getMethodName());
            }
        }

        if (result.getThrowable() != null) {
            Allure.addAttachment(
                "Stack Trace",
                "text/plain",
                result.getThrowable().toString()
            );
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn(
            "SKIPPED: {}.{}",
            result.getTestClass().getName(),
            result.getMethod().getMethodName()
        );
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        log.warn(
            "FAILED_WITHIN_SUCCESS: {}.{}",
            result.getTestClass().getName(),
            result.getMethod().getMethodName()
        );
    }

    @Override
    public void onStart(ITestContext context) {
        log.info("Suite [{}] started", context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        log.info(
            "Suite [{}] finished | passed: {}, failed: {}, skipped: {}",
            context.getName(),
            context.getPassedTests().size(),
            context.getFailedTests().size(),
            context.getSkippedTests().size()
        );
    }

    @Attachment(value = "Screenshot: {testName}", type = "image/png")
    private byte[] saveScreenshot(WebDriver driver, String testName) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }
}
