package com.relativ.tests.home;

import com.relativ.core.BaseUiTest;
import com.relativ.pages.BlogPostPage;
import com.relativ.pages.HomePage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Home Page Flows")
public class HomePageTest extends BaseUiTest {

    private HomePage homePage;
    private BlogPostPage blogPostPage;

    @BeforeEach
    void setUpPages() {
        homePage = new HomePage(driver, wait, selectors, config);
        blogPostPage = new BlogPostPage(driver, wait, selectors, config);
    }

    @Test
    @DisplayName("User can navigate Home to Company")
    void home_company_cta_navigates_to_company_page() {
        openHomePage();

        step("Click the company Learn more CTA");
        homePage.clickCompanyLearnMore();

        step("Assert the browser navigates to the company page");
        homePage.waitForUrlPath("/company");
    }

    @Test
    @DisplayName("User can navigate Home to Blog")
    void home_blog_index_cta_navigates_to_blog_page() {
        openHomePage();

        step("Click the View more blogs CTA");
        homePage.clickViewMoreBlogs();

        step("Assert the browser navigates to the blog index page");
        homePage.waitForUrlPath("/blog");
    }

    @Test
    @DisplayName("User can open a blog post from Home")
    void home_featured_blog_card_navigates_to_blog_post() {
        openHomePage();

        step("Click the first home page blog card");
        homePage.clickFirstBlogCard();

        step("Assert a blog detail page is loaded");
        blogPostPage.waitUntilLoaded();
        org.junit.jupiter.api.Assertions.assertTrue(
                homePage.currentPath().startsWith("/blog/"),
                "Expected home blog CTA to open a blog detail page."
        );
    }

    @Test
    @DisplayName("User can open Careers directly from Home")
    void home_careers_cta_navigates_to_careers_page() {
        openHomePage();

        step("Click the Join now careers CTA");
        homePage.clickJoinNow();

        step("Assert the browser navigates to the careers page");
        homePage.waitForUrlPath("/careers");
    }

    private void openHomePage() {
        step("Open the home page");
        homePage.open();
    }
}
