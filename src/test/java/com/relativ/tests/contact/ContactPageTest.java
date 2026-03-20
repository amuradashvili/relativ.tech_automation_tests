package com.relativ.tests.contact;

import com.relativ.core.BaseUiTest;
import com.relativ.pages.ContactPage;
import com.relativ.pages.PrivacyPolicyModal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Contact Page Flows")
public class ContactPageTest extends BaseUiTest {

    private ContactPage contactPage;
    private PrivacyPolicyModal privacyPolicyModal;

    @BeforeEach
    void setUpPages() {
        contactPage = new ContactPage(driver, wait, selectors, config);
        privacyPolicyModal = new PrivacyPolicyModal(driver, wait, selectors, config);
    }

    @Test
    @DisplayName("User can see contact information links")
    void contact_information_links_are_available() {
        openContactPage();

        step("Assert the partnership email link is visible");
        Assertions.assertEquals("mailto:partnership@relativ.tech", contactPage.mailtoHref());

        step("Assert the LinkedIn link is visible");
        Assertions.assertTrue(contactPage.linkedInHref().contains("linkedin.com"));
    }

    @Test
    @DisplayName("User sees required validation on the contact form")
    void contact_form_required_validation_path() {
        openContactPage();

        step("Submit the empty contact form");
        contactPage.submitEmptyForm();

        step("Assert required validation messages are shown");
        Assertions.assertTrue(contactPage.fullNameError().contains("Full name is required"));
        Assertions.assertTrue(contactPage.emailError().contains("Email address is required"));
        Assertions.assertTrue(contactPage.messageError().contains("Message is required"));
        Assertions.assertTrue(contactPage.consentError().contains("You must agree"));
    }

    @Test
    @DisplayName("User sees email validation for an invalid contact email")
    void contact_form_invalid_email_shows_email_validation_error() {
        openContactPage();

        step("Fill the contact form with an invalid email address");
        contactPage.fillFullName("Jane Doe");
        contactPage.fillEmail("invalid-email");
        contactPage.fillMessage("Hello from Selenium");

        step("Submit the contact form");
        contactPage.submitEmptyForm();

        step("Assert the invalid email validation message is shown");
        Assertions.assertTrue(contactPage.emailError().contains("valid email address"));
    }

    @Test
    @DisplayName("User can open the privacy policy from the contact form")
    void contact_privacy_policy_modal_path() {
        openContactPage();

        step("Open the privacy policy modal from the contact form");
        contactPage.openPrivacyPolicy();

        step("Assert the privacy policy modal is visible");
        privacyPolicyModal.waitUntilOpen();

        step("Close the privacy policy modal");
        privacyPolicyModal.close();
    }

    private void openContactPage() {
        step("Open the contact page");
        contactPage.open();
    }
}
