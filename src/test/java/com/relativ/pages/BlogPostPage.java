package com.relativ.pages;

import com.relativ.core.SelectorRepository;
import com.relativ.core.UiConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URI;
import java.util.List;

public final class BlogPostPage extends BasePage {

    private static final By PAGE_TITLE = By.cssSelector("main h1");
    private static final By MORE_TO_READ_TITLE = By.xpath("//section//h2[normalize-space()='More to read']");
    private static final By MORE_TO_READ_CARDS = By.xpath("//section[.//h2[normalize-space()='More to read']]//a[contains(@href,'/blog/')]");

    public BlogPostPage(WebDriver driver, WebDriverWait wait, SelectorRepository selectors, UiConfig config) {
        super(driver, wait, selectors, config);
    }

    public void waitUntilLoaded() {
        wait.until(d -> {
            String path = normalizePath(URI.create(d.getCurrentUrl()).getPath());
            if (!path.startsWith("/blog/")) {
                return false;
            }

            List<WebElement> titles = d.findElements(PAGE_TITLE);
            if (titles.isEmpty()) {
                return false;
            }

            String title = titles.get(0).getText().trim();
            return titles.get(0).isDisplayed() && !title.isBlank() && !"Blog".equals(title);
        });
    }

    public boolean hasMoreToRead() {
        return isDisplayed(MORE_TO_READ_TITLE);
    }

    public void clickMoreToReadCard(int index) {
        List<WebElement> cards = visibleElements(MORE_TO_READ_CARDS);
        WebElement card = cards.get(index);
        scrollIntoView(card);
        try {
            card.click();
        } catch (RuntimeException exception) {
            jsClick(card);
        }
    }
}
