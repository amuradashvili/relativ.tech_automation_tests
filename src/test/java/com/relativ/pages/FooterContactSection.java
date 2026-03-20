package com.relativ.pages;

import com.relativ.core.SelectorRepository;
import com.relativ.core.UiConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class FooterContactSection extends BasePage {

    private static final By ROOT = By.cssSelector("section#contact");
    private static final By COMPANY_INPUT = By.id("footer-company");
    private static final By EMAIL_INPUT = By.id("footer-email");
    private static final By MESSAGE_INPUT = By.id("footer-message");
    private static final By CONSENT_INPUT = By.id("footer-consent");
    private static final By SUBMIT_BUTTON = By.cssSelector("section#contact button[type='submit']");
    private static final By PRIVACY_BUTTON = By.xpath("//section[@id='contact']//button[@type='button' and contains(.,'Privacy Policy')]");
    private static final By CONSENT_ERROR = By.xpath("//section[@id='contact']//p[contains(.,'You must agree to the Privacy Policy')]");
    private static final By MAILTO_LINK = By.cssSelector("section#contact a[href='mailto:partnership@relativ.tech']");
    private static final By LINKEDIN_LINK = By.cssSelector("section#contact a[href*='linkedin.com/company/relativ-tech']");

    public FooterContactSection(WebDriver driver, WebDriverWait wait, SelectorRepository selectors, UiConfig config) {
        super(driver, wait, selectors, config);
    }

    public void waitUntilVisible() {
        waitVisible(ROOT);
    }

    public void clickFooterNav(String label) {
        click(By.xpath("//section[@id='contact']//nav[@aria-label='Footer navigation']//a[normalize-space()='" + label + "']"));
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

    public void fillCompany(String value) {
        type(COMPANY_INPUT, value);
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
            click(By.xpath("//label[@for='footer-consent']"));
        }
    }

    public String companyError() {
        return text(By.id("footer-company-error"));
    }

    public String emailError() {
        return text(By.id("footer-email-error"));
    }

    public String messageError() {
        return text(By.id("footer-message-error"));
    }

    public String consentError() {
        return text(CONSENT_ERROR);
    }

    public void openPrivacyPolicy() {
        click(PRIVACY_BUTTON);
    }
}
