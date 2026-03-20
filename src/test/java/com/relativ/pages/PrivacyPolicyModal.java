package com.relativ.pages;

import com.relativ.core.SelectorRepository;
import com.relativ.core.UiConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class PrivacyPolicyModal extends BasePage {

    private static final By DIALOG = By.cssSelector("dialog[open]");
    private static final By TITLE = By.xpath("//dialog[@open]//h2[normalize-space()='Privacy Policy']");
    private static final By CLOSE_BUTTON = By.cssSelector("dialog[open] button[aria-label='Close privacy policy']");

    public PrivacyPolicyModal(WebDriver driver, WebDriverWait wait, SelectorRepository selectors, UiConfig config) {
        super(driver, wait, selectors, config);
    }

    public void waitUntilOpen() {
        waitVisible(TITLE);
    }

    public boolean isOpen() {
        return isDisplayed(DIALOG);
    }

    public void close() {
        click(CLOSE_BUTTON);
        wait.until(d -> !isDisplayed(DIALOG));
    }
}
