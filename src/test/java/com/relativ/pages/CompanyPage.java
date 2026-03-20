package com.relativ.pages;

import com.relativ.core.SelectorRepository;
import com.relativ.core.UiConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class CompanyPage extends BasePage {

    private static final By HERO_TITLE = By.xpath("//main//h1[contains(.,'Architects of Financial Flow')]");
    private static final By ACTIVE_APPROACH_TITLE = By.xpath("//section[.//h2[normalize-space()='Our Approach']]//div[contains(@class,'rounded-2xl')]//h3");
    private static final By JOIN_NOW = By.cssSelector("main a[href='/careers']");

    public CompanyPage(WebDriver driver, WebDriverWait wait, SelectorRepository selectors, UiConfig config) {
        super(driver, wait, selectors, config);
    }

    public void open() {
        openPath("/company");
        waitVisible(HERO_TITLE);
    }

    public void selectApproach(String title) {
        click(By.xpath("//section[.//h2[normalize-space()='Our Approach']]//button[.//span[normalize-space()='" + title + "']]"));
        waitForText(ACTIVE_APPROACH_TITLE, title);
    }

    public String activeApproachTitle() {
        return text(ACTIVE_APPROACH_TITLE);
    }

    public void clickJoinNow() {
        click(By.xpath("//main//a[@href='/careers' and normalize-space()='Join now']"));
    }
}
