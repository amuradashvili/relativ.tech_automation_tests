package com.relativ.pages;

import com.relativ.core.SelectorRepository;
import com.relativ.core.UiConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class CareerDetailPanelPage extends BasePage {

    private static final By PANEL_TITLE = By.cssSelector("div[role='dialog'] h2, main h2.text-h3");
    private static final By READ_MORE_BUTTON = By.xpath("//button[normalize-space()='Read more' or normalize-space()='Show less']");
    private static final By PRIVACY_BUTTON = By.xpath("//form//button[@type='button' and contains(normalize-space(),'Privacy Policy')]");
    private static final By SUBMIT_BUTTON = By.xpath("//form//button[@type='submit']");
    private static final By CLOSE_BUTTON = By.cssSelector("button[aria-label='Close detail panel']");
    private static final By FILE_INPUT = By.id("apply-cv");
    private static final By FILE_NAME = By.xpath("//form//p[contains(@class,'text-subtitle2')]");
    private static final By CONSENT_ERROR = By.xpath("//form//p[contains(.,'You must agree to the Privacy Policy')]");
    private static final By CV_ERROR = By.xpath("//form//p[contains(.,'Please upload your CV')]");

    public CareerDetailPanelPage(WebDriver driver, WebDriverWait wait, SelectorRepository selectors, UiConfig config) {
        super(driver, wait, selectors, config);
    }

    public void waitUntilOpen() {
        waitVisible(PANEL_TITLE);
    }

    public String panelTitle() {
        return text(PANEL_TITLE);
    }

    public boolean hasReadMoreToggle() {
        return isDisplayed(READ_MORE_BUTTON);
    }

    public void toggleReadMore() {
        click(READ_MORE_BUTTON);
    }

    public void submitEmptyForm() {
        click(SUBMIT_BUTTON);
    }

    public void uploadResume(String absolutePath) {
        upload(FILE_INPUT, absolutePath);
    }

    public String uploadedFileName() {
        return text(FILE_NAME);
    }

    public void openPrivacyPolicy() {
        click(PRIVACY_BUTTON);
    }

    public void close() {
        click(CLOSE_BUTTON);
    }

    public String firstNameError() {
        return text(By.id("apply-first-name-error"));
    }

    public String lastNameError() {
        return text(By.id("apply-last-name-error"));
    }

    public String phoneError() {
        return text(By.id("apply-phone-error"));
    }

    public String emailError() {
        return text(By.id("apply-email-error"));
    }

    public String consentError() {
        return text(CONSENT_ERROR);
    }

    public String cvError() {
        return text(CV_ERROR);
    }
}
