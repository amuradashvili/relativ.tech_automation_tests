package com.relativ.tests.visual;

import com.relativ.core.BaseUiTest;
import com.relativ.core.VisualRegressionSupport;
import com.relativ.pages.BlogPage;
import com.relativ.pages.BlogPostPage;
import com.relativ.pages.CareerDetailPanelPage;
import com.relativ.pages.CareersPage;
import com.relativ.pages.CompanyPage;
import com.relativ.pages.ContactPage;
import com.relativ.pages.HomePage;
import com.relativ.pages.PrivacyPolicyModal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Visual Design Regression")
public class VisualRegressionTest extends BaseUiTest {

    private HomePage homePage;
    private CompanyPage companyPage;
    private BlogPage blogPage;
    private BlogPostPage blogPostPage;
    private CareersPage careersPage;
    private CareerDetailPanelPage careerDetailPanelPage;
    private ContactPage contactPage;
    private PrivacyPolicyModal privacyPolicyModal;
    private VisualRegressionSupport visualRegression;

    @BeforeEach
    void setUpPages() {
        homePage = new HomePage(driver, wait, selectors, config);
        companyPage = new CompanyPage(driver, wait, selectors, config);
        blogPage = new BlogPage(driver, wait, selectors, config);
        blogPostPage = new BlogPostPage(driver, wait, selectors, config);
        careersPage = new CareersPage(driver, wait, selectors, config);
        careerDetailPanelPage = new CareerDetailPanelPage(driver, wait, selectors, config);
        contactPage = new ContactPage(driver, wait, selectors, config);
        privacyPolicyModal = new PrivacyPolicyModal(driver, wait, selectors, config);
        visualRegression = new VisualRegressionSupport(driver, config);
    }

    @Test
    @DisplayName("Home page matches the approved design baseline")
    void home_page_matches_the_approved_design_baseline() {
        step("Open the home page for visual validation");
        homePage.open();

        step("Compare the home page against its approved visual baseline");
        visualRegression.assertPageMatches("home-page");
    }

    @Test
    @DisplayName("Company default page matches the approved design baseline")
    void company_default_page_matches_the_approved_design_baseline() {
        step("Open the company page for visual validation");
        companyPage.open();

        step("Compare the company default state against its approved visual baseline");
        visualRegression.assertPageMatches("company-page-default");
    }

    @Test
    @DisplayName("Company alternate approach state matches the approved design baseline")
    void company_alternate_approach_state_matches_the_approved_design_baseline() {
        step("Open the company page for visual validation");
        companyPage.open();

        step("Switch to the Operational Flow state");
        companyPage.selectApproach("Operational Flow");

        step("Compare the alternate company state against its approved visual baseline");
        visualRegression.assertPageMatches("company-page-operational-flow");
    }

    @Test
    @DisplayName("Blog index matches the approved design baseline")
    void blog_index_matches_the_approved_design_baseline() {
        step("Open the blog index page for visual validation");
        blogPage.open();

        step("Compare the blog index against its approved visual baseline");
        visualRegression.assertPageMatches("blog-page");
    }

    @Test
    @DisplayName("Featured blog detail page matches the approved design baseline")
    void featured_blog_detail_page_matches_the_approved_design_baseline() {
        step("Open the blog index page");
        blogPage.open();

        step("Open the featured blog post");
        blogPage.openFeaturedPost();
        blogPostPage.waitUntilLoaded();

        step("Compare the featured blog detail page against its approved visual baseline");
        visualRegression.assertPageMatches("blog-post-featured");
    }

    @Test
    @DisplayName("Careers page matches the approved design baseline")
    void careers_page_matches_the_approved_design_baseline() {
        step("Open the careers page for visual validation");
        careersPage.open();

        step("Compare the careers page against its approved visual baseline");
        visualRegression.assertPageMatches("careers-page");
    }

    @Test
    @DisplayName("General application panel matches the approved design baseline")
    void general_application_panel_matches_the_approved_design_baseline() {
        step("Open the careers page");
        careersPage.open();

        step("Open the general application panel");
        careersPage.openGeneralApplication();
        careerDetailPanelPage.waitUntilOpen();

        step("Compare the general application panel against its approved visual baseline");
        visualRegression.assertPageMatches("careers-general-application");
    }

    @Test
    @DisplayName("Contact page matches the approved design baseline")
    void contact_page_matches_the_approved_design_baseline() {
        step("Open the contact page for visual validation");
        contactPage.open();

        step("Compare the contact page against its approved visual baseline");
        visualRegression.assertPageMatches("contact-page");
    }

    @Test
    @DisplayName("Contact privacy policy modal matches the approved design baseline")
    void contact_privacy_policy_modal_matches_the_approved_design_baseline() {
        step("Open the contact page");
        contactPage.open();

        step("Open the privacy policy modal");
        contactPage.openPrivacyPolicy();
        privacyPolicyModal.waitUntilOpen();

        step("Compare the privacy policy modal against its approved visual baseline");
        visualRegression.assertPageMatches("contact-privacy-policy-modal");
    }
}
