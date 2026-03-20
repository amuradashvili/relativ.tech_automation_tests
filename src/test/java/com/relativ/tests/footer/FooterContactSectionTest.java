package com.relativ.tests.footer;

import com.relativ.core.BaseUiTest;
import com.relativ.pages.FooterContactSection;
import com.relativ.pages.HomePage;
import com.relativ.pages.PrivacyPolicyModal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Footer Contact Section Flows")
public class FooterContactSectionTest extends BaseUiTest {

    private HomePage homePage;
    private FooterContactSection footerContactSection;
    private PrivacyPolicyModal privacyPolicyModal;

    @BeforeEach
    void setUpPages() {
        homePage = new HomePage(driver, wait, selectors, config);
        footerContactSection = new FooterContactSection(driver, wait, selectors, config);
        privacyPolicyModal = new PrivacyPolicyModal(driver, wait, selectors, config);
    }

    @Test
    @DisplayName("User can see the footer contact section on the Home page")
    void footer_contact_section_is_visible_on_home_page() {
        openRoute("/");

        step("Assert the footer contact section is visible");
        footerContactSection.waitUntilVisible();

        step("Assert the footer partnership email link is visible");
        Assertions.assertEquals("mailto:partnership@relativ.tech", footerContactSection.mailtoHref());

        step("Assert the footer LinkedIn link is visible");
        Assertions.assertTrue(footerContactSection.linkedInHref().contains("linkedin.com"));
    }

    @Test
    @DisplayName("User can see the footer contact section on the Company page")
    void footer_contact_section_is_visible_on_company_page() {
        openRoute("/company");

        step("Assert the footer contact section is visible");
        footerContactSection.waitUntilVisible();

        step("Assert the footer partnership email link is visible");
        Assertions.assertEquals("mailto:partnership@relativ.tech", footerContactSection.mailtoHref());

        step("Assert the footer LinkedIn link is visible");
        Assertions.assertTrue(footerContactSection.linkedInHref().contains("linkedin.com"));
    }

    @Test
    @DisplayName("User can see the footer contact section on the Blog page")
    void footer_contact_section_is_visible_on_blog_page() {
        openRoute("/blog");

        step("Assert the footer contact section is visible");
        footerContactSection.waitUntilVisible();

        step("Assert the footer partnership email link is visible");
        Assertions.assertEquals("mailto:partnership@relativ.tech", footerContactSection.mailtoHref());

        step("Assert the footer LinkedIn link is visible");
        Assertions.assertTrue(footerContactSection.linkedInHref().contains("linkedin.com"));
    }

    @Test
    @DisplayName("User can see the footer contact section on the Careers page")
    void footer_contact_section_is_visible_on_careers_page() {
        openRoute("/careers");

        step("Assert the footer contact section is visible");
        footerContactSection.waitUntilVisible();

        step("Assert the footer partnership email link is visible");
        Assertions.assertEquals("mailto:partnership@relativ.tech", footerContactSection.mailtoHref());

        step("Assert the footer LinkedIn link is visible");
        Assertions.assertTrue(footerContactSection.linkedInHref().contains("linkedin.com"));
    }

    @Test
    @DisplayName("User sees required validation on the footer contact form")
    void footer_contact_form_required_validation_path() {
        openFooterSectionFromHomePage();

        step("Submit the empty footer contact form");
        footerContactSection.submitEmptyForm();

        step("Assert required validation messages are shown");
        Assertions.assertTrue(footerContactSection.companyError().contains("Company is required"));
        Assertions.assertTrue(footerContactSection.emailError().contains("Email address is required"));
        Assertions.assertTrue(footerContactSection.messageError().contains("Message is required"));
        Assertions.assertTrue(footerContactSection.consentError().contains("You must agree"));
    }

    @Test
    @DisplayName("User sees email validation for an invalid footer contact email")
    void footer_contact_form_invalid_email_shows_email_validation_error() {
        openFooterSectionFromHomePage();

        step("Fill the footer contact form with an invalid email address");
        footerContactSection.fillCompany("Relativ Tech");
        footerContactSection.fillEmail("bad-email");
        footerContactSection.fillMessage("Need more information");

        step("Submit the footer contact form");
        footerContactSection.submitEmptyForm();

        step("Assert the invalid email validation message is shown");
        Assertions.assertTrue(footerContactSection.emailError().contains("valid email address"));
    }

    @Test
    @DisplayName("User can open the privacy policy from the footer contact form")
    void footer_privacy_policy_modal_path() {
        openFooterSectionFromHomePage();

        step("Open the privacy policy modal from the footer contact form");
        footerContactSection.openPrivacyPolicy();

        step("Assert the privacy policy modal is visible");
        privacyPolicyModal.waitUntilOpen();

        step("Close the privacy policy modal");
        privacyPolicyModal.close();
    }

    @Test
    @DisplayName("User can navigate to Blog from the footer")
    void footer_blog_navigation_path() {
        openFooterSectionFromHomePage();

        step("Click the Blog link in the footer navigation");
        footerContactSection.clickFooterNav("Blog");

        step("Assert the browser navigates to the blog page");
        homePage.waitForUrlPath("/blog");
    }

    private void openFooterSectionFromHomePage() {
        step("Open the home page");
        homePage.open();

        step("Assert the footer contact section is visible");
        footerContactSection.waitUntilVisible();
    }

    private void openRoute(String route) {
        step("Open route " + route);
        homePage.openPath(route);
    }
}
