package com.relativ.pages;

import com.relativ.core.SelectorRepository;
import com.relativ.core.UiConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public final class BlogPage extends BasePage {

    private static final By HERO_TITLE = By.xpath("//main//h1[normalize-space()='Blog']");
    private static final By BLOG_POST_LINKS = By.xpath("//main//a[contains(@href,'/blog/')][.//h2 or .//h3]");

    public BlogPage(WebDriver driver, WebDriverWait wait, SelectorRepository selectors, UiConfig config) {
        super(driver, wait, selectors, config);
    }

    public void open() {
        openPath("/blog");
        waitVisible(HERO_TITLE);
    }

    public void openFeaturedPost() {
        clickBlogLink(0);
    }

    public void openListPost(int index) {
        clickBlogLink(index + 1);
    }

    public int blogLinkCount() {
        return waitForBlogLinks(1).size();
    }

    private void clickBlogLink(int index) {
        List<WebElement> links = waitForBlogLinks(index + 1);
        WebElement link = links.get(index);
        scrollIntoView(link);
        try {
            link.click();
        } catch (RuntimeException exception) {
            jsClick(link);
        }
    }

    private List<WebElement> waitForBlogLinks(int minimumCount) {
        return wait.until(d -> {
            List<WebElement> links = visibleElements(BLOG_POST_LINKS);
            return links.size() >= minimumCount ? links : null;
        });
    }
}
