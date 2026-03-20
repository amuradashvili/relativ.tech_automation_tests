package com.relativ.tests.blog;

import com.relativ.core.BaseUiTest;
import com.relativ.pages.BlogPage;
import com.relativ.pages.BlogPostPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Blog Page Flows")
public class BlogPageTest extends BaseUiTest {

    private BlogPage blogPage;
    private BlogPostPage blogPostPage;

    @BeforeEach
    void setUpPages() {
        blogPage = new BlogPage(driver, wait, selectors, config);
        blogPostPage = new BlogPostPage(driver, wait, selectors, config);
    }

    @Test
    @DisplayName("User can open the featured blog post")
    void blog_featured_card_opens_a_blog_post() {
        openBlogIndex();

        step("Assert the blog index exposes at least three blog entry links");
        Assertions.assertTrue(blogPage.blogLinkCount() >= 3, "Expected at least three blog links on the blog index.");

        step("Open the featured blog card");
        blogPage.openFeaturedPost();

        step("Assert the featured card opens a blog detail page");
        blogPostPage.waitUntilLoaded();
        Assertions.assertTrue(blogPostPage.currentPath().startsWith("/blog/"), "Expected featured CTA to open a blog detail route.");
    }

    @Test
    @DisplayName("User can open a different list blog post")
    void blog_list_card_opens_a_different_post_than_the_featured_card() {
        openBlogIndex();

        step("Open the featured blog card and capture its path");
        blogPage.openFeaturedPost();
        blogPostPage.waitUntilLoaded();
        String featuredPath = blogPostPage.currentPath();

        step("Return to the blog index");
        blogPage.open();

        step("Open the first list blog card");
        blogPage.openListPost(0);

        step("Assert the list card opens a different blog detail page");
        blogPostPage.waitUntilLoaded();
        Assertions.assertTrue(blogPostPage.currentPath().startsWith("/blog/"));
        Assertions.assertNotEquals(featuredPath, blogPostPage.currentPath(), "Expected list card to open a different post.");
    }

    @Test
    @DisplayName("User can open a related article from More to read")
    void blog_more_to_read_opens_a_different_related_article() {
        openBlogIndex();

        step("Open the featured blog card");
        blogPage.openFeaturedPost();
        blogPostPage.waitUntilLoaded();
        String currentPath = blogPostPage.currentPath();

        step("Assert the More to read section is visible");
        Assertions.assertTrue(blogPostPage.hasMoreToRead(), "Expected More to read section on blog detail page.");

        step("Open the first More to read article");
        blogPostPage.clickMoreToReadCard(0);

        step("Assert the related article navigation changes the blog detail path");
        wait.until(d -> !blogPostPage.currentPath().equals(currentPath));
        blogPostPage.waitUntilLoaded();
        Assertions.assertTrue(blogPostPage.currentPath().startsWith("/blog/"));
        Assertions.assertNotEquals(currentPath, blogPostPage.currentPath(), "Expected More to read to open a different article.");
    }

    private void openBlogIndex() {
        step("Open the blog index page");
        blogPage.open();
    }
}
