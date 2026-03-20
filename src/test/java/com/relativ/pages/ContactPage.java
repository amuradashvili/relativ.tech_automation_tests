package com.relativ.pages;

import com.relativ.core.SelectorRepository;
import com.relativ.core.UiConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class ContactPage extends BasePage {

    private static final By HERO_TITLE = By.xpath("//main//h1[normalize-space()='Contact']");
    private static final By FULL_NAME_INPUT = By.id("contact-full-name");
    private static final By EMAIL_INPUT = By.id("contact-email");
    private static final By MESSAGE_INPUT = By.id("contact-message");
    private static final By CONSENT_INPUT = By.id("contact-consent");
    private static final By SUBMIT_BUTTON = By.cssSelector("main button[type='submit']");
    private static final By PRIVACY_BUTTON = By.xpath("//main//button[@type='button' and contains(.,'Privacy Policy')]");
    private static final By CONSENT_ERROR = By.xpath("//main//p[contains(.,'You must agree to the Privacy Policy')]");
    private static final By MAILTO_LINK = By.cssSelector("main a[href='mailto:partnership@relativ.tech']");
    private static final By LINKEDIN_LINK = By.cssSelector("main a[href*='linkedin.com/company/relativ-tech']");

    public ContactPage(WebDriver driver, WebDriverWait wait, SelectorRepository selectors, UiConfig config) {
        super(driver, wait, selectors, config);
    }

    public void open() {
        openPath("/contact");
        waitVisible(HERO_TITLE);
    }

    public String mailtoHref() {
        return attribute(MAILTO_LINK, "href");
    }

    public String linkedInHref() {
        return attribute(LINKEDIN_LINK, "href");
    }

    public void submitEmptyForm() {
        click(SUBMIT_BUTTON);
    }

    public void fillFullName(String value) {
        type(FULL_NAME_INPUT, value);
    }

    public void fillEmail(String value) {
        type(EMAIL_INPUT, value);
    }

    public void fillMessage(String value) {
        type(MESSAGE_INPUT, value);
    }

    public void setConsent(boolean checked) {
        boolean selected = waitPresent(CONSENT_INPUT).isSelected();
        if (selected != checked) {
            click(By.xpath("//label[@for='contact-consent']"));
        }
    }

    public void openPrivacyPolicy() {
        click(PRIVACY_BUTTON);
    }

    public String fullNameError() {
        return text(By.id("contact-full-name-error"));
    }

    public String emailError() {
        return text(By.id("contact-email-error"));
    }

    public String messageError() {
        return text(By.id("contact-message-error"));
    }

    public String consentError() {
        return text(CONSENT_ERROR);
    }
}
