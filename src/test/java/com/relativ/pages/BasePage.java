package com.relativ.pages;

import com.relativ.core.SelectorRepository;
import com.relativ.core.TestExecutionLog;
import com.relativ.core.UiConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final SelectorRepository selectors;
    protected final UiConfig config;

    protected BasePage(WebDriver driver, WebDriverWait wait, SelectorRepository selectors, UiConfig config) {
        this.driver = driver;
        this.wait = wait;
        this.selectors = selectors;
        this.config = config;
    }

    public void openAbsolute(String url) {
        logStep("Open URL " + url);
        driver.get(url);
        waitForDocumentReady();
    }

    public void openPath(String path) {
        String resolvedUrl = resolveUrl(path);
        logStep("Open path " + normalizePath(path) + " -> " + resolvedUrl);
        driver.get(resolvedUrl);
        waitForDocumentReady();
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }

    public String currentPath() {
        return normalizePath(URI.create(currentUrl()).getPath());
    }

    public String title() {
        return driver.getTitle();
    }

    public void click(String selectorKey) {
        logStep("Click selector key " + selectorKey);
        waitVisible(selectorKey).click();
    }

    public void type(String selectorKey, String value) {
        logStep("Type into selector key " + selectorKey + " value '" + summarizeValue(value) + "'");
        WebElement input = waitVisible(selectorKey);
        input.clear();
        input.sendKeys(value);
    }

    protected void click(By locator) {
        logStep("Click element " + locator);
        WebElement element = waitClickable(locator);
        scrollIntoView(element);
        try {
            element.click();
        } catch (ElementClickInterceptedException exception) {
            jsClick(element);
        }
    }

    protected void type(By locator, String value) {
        logStep("Type into element " + locator + " value '" + summarizeValue(value) + "'");
        WebElement input = waitVisible(locator);
        input.clear();
        input.sendKeys(value);
    }

    protected void upload(By locator, String absolutePath) {
        logStep("Upload file '" + fileNameOnly(absolutePath) + "' into " + locator);
        waitPresent(locator).sendKeys(absolutePath);
    }

    protected String text(By locator) {
        return waitVisible(locator).getText().trim();
    }

    protected String attribute(By locator, String attributeName) {
        return waitPresent(locator).getAttribute(attributeName);
    }

    protected boolean isDisplayed(By locator) {
        return findOptional(locator)
                .map(WebElement::isDisplayed)
                .orElse(false);
    }

    protected boolean isEnabled(By locator) {
        return waitPresent(locator).isEnabled();
    }

    protected List<String> texts(By locator) {
        return findAll(locator).stream()
                .filter(WebElement::isDisplayed)
                .map(WebElement::getText)
                .map(String::trim)
                .filter(text -> !text.isEmpty())
                .collect(Collectors.toList());
    }

    protected void setViewport(int width, int height) {
        logStep("Set viewport to " + width + "x" + height);
        driver.manage().window().setSize(new Dimension(width, height));
    }

    public void waitForUrlPath(String expectedPath) {
        String normalizedPath = normalizePath(expectedPath);
        logStep("Wait for URL path " + normalizedPath);
        wait.until(d -> normalizePath(URI.create(d.getCurrentUrl()).getPath()).equals(normalizedPath));
    }

    protected void waitForUrlContains(String fragment) {
        logStep("Wait for URL to contain '" + fragment + "'");
        wait.until(ExpectedConditions.urlContains(fragment));
    }

    protected void waitForText(By locator, String expectedText) {
        logStep("Wait for text '" + expectedText + "' in " + locator);
        wait.until(ExpectedConditions.textToBe(locator, expectedText));
    }

    protected WebElement waitVisible(String selectorKey) {
        List<String> selectorList = selectors.getRequiredSelectors(selectorKey);
        return wait.until(d -> findVisibleBySelectorList(selectorList).orElse(null));
    }

    protected WebElement waitVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected WebElement waitPresent(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    protected Optional<WebElement> findVisibleOptional(String selectorKey) {
        return selectors.getOptionalSelectors(selectorKey)
                .flatMap(this::findVisibleBySelectorList);
    }

    protected Optional<WebElement> findOptional(By locator) {
        List<WebElement> elements = driver.findElements(locator);
        if (elements.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(elements.get(0));
    }

    protected boolean hasVisibleOptional(String selectorKey) {
        return findVisibleOptional(selectorKey).isPresent();
    }

    protected List<WebElement> findAll(By locator) {
        return driver.findElements(locator);
    }

    protected List<WebElement> visibleElements(By locator) {
        return findAll(locator).stream()
                .filter(WebElement::isDisplayed)
                .collect(Collectors.toList());
    }

    protected Optional<WebElement> findVisibleBySelectorList(List<String> selectorList) {
        for (String selector : selectorList) {
            List<WebElement> elements = driver.findElements(By.cssSelector(selector));
            for (WebElement element : elements) {
                if (element.isDisplayed()) {
                    return Optional.of(element);
                }
            }
        }
        return Optional.empty();
    }

    protected void waitForDocumentReady() {
        wait.until(d -> {
            Object state = ((JavascriptExecutor) d).executeScript("return document.readyState");
            return "complete".equals(state) || "interactive".equals(state);
        });
    }

    protected void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'nearest'});",
                element
        );
    }

    protected void jsClick(WebElement element) {
        logStep("Fallback to JavaScript click");
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    protected void logStep(String message) {
        TestExecutionLog.step(message);
    }

    private String summarizeValue(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.length() > 40 ? value.substring(0, 37) + "..." : value;
    }

    private String fileNameOnly(String absolutePath) {
        int index = Math.max(absolutePath.lastIndexOf('/'), absolutePath.lastIndexOf('\\'));
        return index >= 0 ? absolutePath.substring(index + 1) : absolutePath;
    }

    protected String resolveUrl(String path) {
        if (path.startsWith("http://") || path.startsWith("https://")) {
            return path;
        }
        String normalizedBaseUrl = config.baseUrl().endsWith("/")
                ? config.baseUrl().substring(0, config.baseUrl().length() - 1)
                : config.baseUrl();
        return normalizedBaseUrl + normalizePath(path);
    }

    protected String normalizePath(String path) {
        if (path == null || path.isBlank()) {
            return "/";
        }
        String normalized = path.startsWith("/") ? path : "/" + path;
        if (normalized.length() > 1 && normalized.endsWith("/")) {
            return normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }
}
