package com.relativ.pages;

import com.relativ.core.SelectorRepository;
import com.relativ.core.UiConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class CareersPage extends BasePage {

    private static final By HERO_TITLE = By.xpath("//main//h1[contains(.,'Careers At Relativ Tech')]");

    public CareersPage(WebDriver driver, WebDriverWait wait, SelectorRepository selectors, UiConfig config) {
        super(driver, wait, selectors, config);
    }

    public void open() {
        openPath("/careers");
        waitVisible(HERO_TITLE);
    }

    public void selectTab(String label) {
        click(By.xpath("//section//button[normalize-space()='" + label + "']"));
    }

    public boolean isRoleVisible(String title) {
        return isDisplayed(By.xpath("//section//button[.//p[normalize-space()='" + title + "']]"));
    }

    public boolean isRoleDisabled(String title) {
        return !isEnabled(By.xpath("//section//button[.//p[normalize-space()='" + title + "']]"));
    }

    public void openGeneralApplication() {
        click(By.xpath("//section//button[.//p[normalize-space()='Introduce your expertise']]"));
    }

    public void openRole(String title) {
        click(By.xpath("//section//button[.//p[normalize-space()='" + title + "']]"));
    }
}
