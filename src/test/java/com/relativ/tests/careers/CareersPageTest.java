package com.relativ.tests.careers;

import com.relativ.core.BaseUiTest;
import com.relativ.core.TestResourceFiles;
import com.relativ.pages.CareerDetailPanelPage;
import com.relativ.pages.CareersPage;
import com.relativ.pages.PrivacyPolicyModal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Careers Page Flows")
public class CareersPageTest extends BaseUiTest {

    private CareersPage careersPage;
    private CareerDetailPanelPage careerDetailPanelPage;
    private PrivacyPolicyModal privacyPolicyModal;

    @BeforeEach
    void setUpPages() {
        careersPage = new CareersPage(driver, wait, selectors, config);
        careerDetailPanelPage = new CareerDetailPanelPage(driver, wait, selectors, config);
        privacyPolicyModal = new PrivacyPolicyModal(driver, wait, selectors, config);
    }

    @Test
    @DisplayName("User sees all expected roles on the All tab")
    void careers_all_tab_shows_all_expected_roles() {
        openCareersPage();

        step("Assert all expected roles are visible on the default All tab");
        Assertions.assertTrue(careersPage.isRoleVisible("Backend Engineer [Financial Systems]"));
        Assertions.assertTrue(careersPage.isRoleVisible("Payments Integration Engineer"));
        Assertions.assertTrue(careersPage.isRoleVisible("UI/UX Designer"));
        Assertions.assertTrue(careersPage.isRoleVisible("PM: Payments & Settlement"));
    }

    @Test
    @DisplayName("User sees only Tech roles on the Tech tab")
    void careers_tech_tab_shows_only_tech_roles_and_disabled_state() {
        openCareersPage();

        step("Switch to the Tech tab");
        careersPage.selectTab("Tech");

        step("Assert only Tech roles are visible and inactive roles stay disabled");
        Assertions.assertTrue(careersPage.isRoleVisible("Backend Engineer [Financial Systems]"));
        Assertions.assertTrue(careersPage.isRoleVisible("Payments Integration Engineer"));
        Assertions.assertFalse(careersPage.isRoleVisible("UI/UX Designer"));
        Assertions.assertFalse(careersPage.isRoleVisible("PM: Payments & Settlement"));
        Assertions.assertTrue(careersPage.isRoleDisabled("Backend Engineer [Financial Systems]"));
        Assertions.assertTrue(careersPage.isRoleDisabled("Payments Integration Engineer"));
    }

    @Test
    @DisplayName("User sees only Product roles on the Product tab")
    void careers_product_tab_shows_only_product_roles_and_disabled_state() {
        openCareersPage();

        step("Switch to the Product tab");
        careersPage.selectTab("Product");

        step("Assert only Product roles are visible and disabled");
        Assertions.assertFalse(careersPage.isRoleVisible("Backend Engineer [Financial Systems]"));
        Assertions.assertFalse(careersPage.isRoleVisible("Payments Integration Engineer"));
        Assertions.assertTrue(careersPage.isRoleVisible("UI/UX Designer"));
        Assertions.assertTrue(careersPage.isRoleVisible("PM: Payments & Settlement"));
        Assertions.assertTrue(careersPage.isRoleDisabled("UI/UX Designer"));
        Assertions.assertTrue(careersPage.isRoleDisabled("PM: Payments & Settlement"));
    }

    @Test
    @DisplayName("User can upload a resume in the general application form")
    void general_application_resume_upload_path() {
        openGeneralApplicationPanel();

        step("Assert the general application panel title is shown");
        Assertions.assertEquals("Introduce Your Expertise", careerDetailPanelPage.panelTitle());

        step("Upload a sample resume file");
        careerDetailPanelPage.uploadResume(TestResourceFiles.absolutePath("files/sample-resume.pdf"));

        step("Assert the uploaded file name is visible");
        Assertions.assertTrue(
                careerDetailPanelPage.uploadedFileName().contains("sample-resume.pdf"),
                "Expected uploaded file name to be visible in the apply panel."
        );
    }

    @Test
    @DisplayName("User can open the privacy policy from the general application form")
    void general_application_privacy_policy_modal_path() {
        openGeneralApplicationPanel();

        step("Open the privacy policy modal from the application form");
        careerDetailPanelPage.openPrivacyPolicy();

        step("Assert the privacy policy modal is visible");
        privacyPolicyModal.waitUntilOpen();

        step("Close the privacy policy modal");
        privacyPolicyModal.close();
    }

    @Test
    @DisplayName("User sees required validation on the general application form")
    void general_application_required_validation_path() {
        openGeneralApplicationPanel();

        step("Assert the general application panel title is shown");
        Assertions.assertEquals("Introduce Your Expertise", careerDetailPanelPage.panelTitle());

        step("Submit the empty application form");
        careerDetailPanelPage.submitEmptyForm();

        step("Assert all required validation messages are shown");
        Assertions.assertTrue(careerDetailPanelPage.firstNameError().contains("First name is required"));
        Assertions.assertTrue(careerDetailPanelPage.lastNameError().contains("Last name is required"));
        Assertions.assertTrue(careerDetailPanelPage.phoneError().contains("Phone number is required"));
        Assertions.assertTrue(careerDetailPanelPage.emailError().contains("Email address is required"));
        Assertions.assertTrue(careerDetailPanelPage.cvError().contains("Please upload your CV"));
        Assertions.assertTrue(careerDetailPanelPage.consentError().contains("You must agree"));
    }

    private void openCareersPage() {
        step("Open the careers page");
        careersPage.open();
    }

    private void openGeneralApplicationPanel() {
        openCareersPage();

        step("Open the general application panel");
        careersPage.openGeneralApplication();

        step("Assert the application panel is visible");
        careerDetailPanelPage.waitUntilOpen();
    }
}
