package com.relativ.pages;

import com.relativ.core.SelectorRepository;
import com.relativ.core.UiConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class HomePage extends BasePage {

    private static final By HERO_TITLE = By.xpath("//main//h1[contains(.,'Architects Of Fintech Flow')]");
    private static final By COMPANY_LEARN_MORE = By.xpath("//section[.//h2[contains(.,'Fintech Ecosystem')]]//a[@href='/company' and normalize-space()='Learn more']");
    private static final By VIEW_MORE_BLOGS = By.cssSelector("main a[href='/blog']");
    private static final By HOME_BLOG_LINKS = By.cssSelector("main a[href^='/blog/']");
    private static final By JOIN_NOW = By.xpath("//main//a[@href='/careers' and normalize-space()='Join now']");

    public HomePage(WebDriver driver, WebDriverWait wait, SelectorRepository selectors, UiConfig config) {
        super(driver, wait, selectors, config);
    }

    public void openSite() {
        openAbsolute(config.baseUrl());
        waitVisible(HERO_TITLE);
    }

    public void open() {
        openPath("/");
        waitVisible(HERO_TITLE);
    }

    public void clickCompanyLearnMore() {
        click(COMPANY_LEARN_MORE);
    }

    public void clickViewMoreBlogs() {
        click(VIEW_MORE_BLOGS);
    }

    public void clickFirstBlogCard() {
        click(HOME_BLOG_LINKS);
    }

    public void clickJoinNow() {
        click(JOIN_NOW);
    }

    public boolean openLoginPage() {
        if (!config.loginUrl().isBlank()) {
            openAbsolute(config.loginUrl());
            return true;
        }

        Optional<WebElement> loginTrigger = findVisibleOptional("home.loginTrigger");
        if (loginTrigger.isPresent()) {
            loginTrigger.get().click();
            return true;
        }

        List<By> fallbackTextLocators = Arrays.asList(
                By.xpath("//a[contains(translate(normalize-space(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'login')]"),
                By.xpath("//button[contains(translate(normalize-space(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'login')]"),
                By.xpath("//a[contains(translate(normalize-space(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'sign in')]"),
                By.xpath("//button[contains(translate(normalize-space(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'sign in')]")
        );

        for (By locator : fallbackTextLocators) {
            List<WebElement> candidates = driver.findElements(locator);
            for (WebElement candidate : candidates) {
                if (candidate.isDisplayed()) {
                    candidate.click();
                    return true;
                }
            }
        }
        return false;
    }
}
