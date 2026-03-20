package com.relativ.tests.navigation;

import com.relativ.core.BaseUiTest;
import com.relativ.pages.HomePage;
import com.relativ.pages.SiteLayout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.Map;

@DisplayName("Site Navigation Flows")
public class SiteNavigationTest extends BaseUiTest {

    private HomePage homePage;
    private SiteLayout siteLayout;

    @BeforeEach
    void setUpPages() {
        homePage = new HomePage(driver, wait, selectors, config);
        siteLayout = new SiteLayout(driver, wait, selectors, config);
    }

    @Test
    @DisplayName("User can navigate to Company from the desktop header")
    void desktop_company_navigation_opens_company_page() {
        openHomePage();

        step("Click desktop Company navigation link");
        siteLayout.clickDesktopNav("Company");

        step("Assert the browser navigates to the company page");
        homePage.waitForUrlPath("/company");
    }

    @Test
    @DisplayName("User can navigate to Blog from the desktop header")
    void desktop_blog_navigation_opens_blog_page() {
        openHomePage();

        step("Click desktop Blog navigation link");
        siteLayout.clickDesktopNav("Blog");

        step("Assert the browser navigates to the blog page");
        homePage.waitForUrlPath("/blog");
    }

    @Test
    @DisplayName("User can navigate to Careers from the desktop header")
    void desktop_careers_navigation_opens_careers_page() {
        openHomePage();

        step("Click desktop Careers navigation link");
        siteLayout.clickDesktopNav("Careers");

        step("Assert the browser navigates to the careers page");
        homePage.waitForUrlPath("/careers");
    }

    @Test
    @DisplayName("User can navigate to Contact from the desktop header")
    void desktop_contact_navigation_opens_contact_page() {
        openHomePage();

        step("Click desktop Contact navigation link");
        siteLayout.clickDesktopNav("Contact");

        step("Assert the browser navigates to the contact page");
        homePage.waitForUrlPath("/contact");
    }

    @Test
    @DisplayName("User can return Home by clicking the site logo")
    void logo_navigation_returns_to_home_page() {
        step("Open the company page directly");
        homePage.openPath("/company");
        siteLayout.waitUntilVisible();

        step("Click the site logo");
        siteLayout.clickLogo();

        step("Assert the browser returns to the home page");
        homePage.waitForUrlPath("/");
    }

    @Test
    @DisplayName("User can open and close the mobile menu")
    void mobile_menu_can_open_and_close() {
        enableMobileViewport();
        openHomePage();

        step("Open the mobile navigation menu");
        siteLayout.openMobileMenu();

        step("Assert the mobile menu is visible");
        org.junit.jupiter.api.Assertions.assertTrue(siteLayout.isMobileMenuOpen(), "Expected mobile menu to be open.");

        step("Close the mobile navigation menu");
        siteLayout.closeMobileMenu();

        step("Assert the mobile menu is no longer visible");
        org.junit.jupiter.api.Assertions.assertFalse(siteLayout.isMobileMenuOpen(), "Expected mobile menu to close.");
    }

    @Test
    @DisplayName("User can navigate to Contact from the mobile menu")
    void mobile_contact_navigation_opens_contact_page() {
        enableMobileViewport();
        openHomePage();

        step("Open the mobile navigation menu");
        siteLayout.openMobileMenu();

        step("Tap the Contact link in the mobile menu");
        siteLayout.clickMobileNav("Contact");

        step("Assert the browser navigates to the contact page");
        homePage.waitForUrlPath("/contact");
    }

    private void openHomePage() {
        step("Open the home page");
        homePage.open();

        step("Assert the main site navigation is visible");
        siteLayout.waitUntilVisible();
    }

    private void enableMobileViewport() {
        step("Enable mobile viewport emulation while keeping the browser window full screen");
        driver.manage().window().maximize();
        ((ChromeDriver) driver).executeCdpCommand(
                "Emulation.setDeviceMetricsOverride",
                Map.of(
                        "width", 390,
                        "height", 844,
                        "deviceScaleFactor", 3,
                        "mobile", true
                )
        );
    }
}
