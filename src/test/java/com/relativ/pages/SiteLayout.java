package com.relativ.pages;

import com.relativ.core.SelectorRepository;
import com.relativ.core.UiConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class SiteLayout extends BasePage {

    private static final By LOGO = By.cssSelector("nav a[href='/']");
    private static final By MOBILE_MENU_BUTTON = By.cssSelector("nav button[aria-label='Open menu']");
    private static final By MOBILE_CLOSE_BUTTON = By.cssSelector("button[aria-label='Close menu']");

    public SiteLayout(WebDriver driver, WebDriverWait wait, SelectorRepository selectors, UiConfig config) {
        super(driver, wait, selectors, config);
    }

    public void waitUntilVisible() {
        waitVisible(LOGO);
    }

    public void clickLogo() {
        click(LOGO);
    }

    public void clickDesktopNav(String label) {
        click(By.xpath("//nav//ul//a[normalize-space()='" + label + "']"));
    }

    public void openMobileMenu() {
        click(MOBILE_MENU_BUTTON);
        waitVisible(MOBILE_CLOSE_BUTTON);
    }

    public boolean isMobileMenuOpen() {
        return isDisplayed(MOBILE_CLOSE_BUTTON);
    }

    public void closeMobileMenu() {
        click(MOBILE_CLOSE_BUTTON);
        wait.until(d -> d.findElements(MOBILE_CLOSE_BUTTON).isEmpty());
    }

    public void clickMobileNav(String label) {
        click(By.xpath("//button[@aria-label='Close menu']/ancestor::div[contains(@class,'fixed')]//a[normalize-space()='" + label + "']"));
    }
}
