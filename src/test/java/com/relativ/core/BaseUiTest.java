package com.relativ.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class BaseUiTest {

    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(20);
    private static final Duration IMPLICIT_WAIT = Duration.ofSeconds(2);

    @RegisterExtension
    final TestResultLogger testResultLogger = new TestResultLogger();

    protected UiConfig config;
    protected SelectorRepository selectors;
    protected WebDriver driver;
    protected WebDriverWait wait;

    protected void step(String message) {
        TestExecutionLog.step(message);
    }

    @BeforeEach
    void setUpDriver() {
        TestExecutionLog.step("Load UI config and selector repository");
        config = new UiConfig();
        selectors = new SelectorRepository();
        if (!shouldStartDriver()) {
            TestExecutionLog.step("Driver startup skipped for this test");
            return;
        }
        TestExecutionLog.step("Start Chrome driver");
        driver = DriverFactory.createChrome(config);
        driver.manage().timeouts().implicitlyWait(IMPLICIT_WAIT);
        wait = new WebDriverWait(driver, WAIT_TIMEOUT);
        TestExecutionLog.step("Chrome driver ready");
    }

    protected boolean shouldStartDriver() {
        return true;
    }

    @AfterEach
    void tearDownDriver() {
        if (driver != null) {
            TestExecutionLog.step("Close Chrome driver");
            driver.quit();
        }
    }
}
