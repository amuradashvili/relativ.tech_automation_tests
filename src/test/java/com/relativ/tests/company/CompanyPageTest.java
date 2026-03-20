package com.relativ.tests.company;

import com.relativ.core.BaseUiTest;
import com.relativ.pages.CompanyPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Company Page Flows")
public class CompanyPageTest extends BaseUiTest {

    private CompanyPage companyPage;

    @BeforeEach
    void setUpPages() {
        companyPage = new CompanyPage(driver, wait, selectors, config);
    }

    @Test
    @DisplayName("User sees Diagnostic Mapping selected by default")
    void company_default_approach_is_diagnostic_mapping() {
        openCompanyPage();

        step("Assert Diagnostic Mapping is active by default");
        Assertions.assertEquals("Diagnostic Mapping", companyPage.activeApproachTitle());
    }

    @Test
    @DisplayName("User can select Operational Flow on Company page")
    void company_operational_flow_selection_updates_the_active_card() {
        openCompanyPage();

        step("Select the Operational Flow approach");
        companyPage.selectApproach("Operational Flow");

        step("Assert Operational Flow becomes the active approach");
        Assertions.assertEquals("Operational Flow", companyPage.activeApproachTitle());
    }

    @Test
    @DisplayName("User can select Institutional Governance on Company page")
    void company_institutional_governance_selection_updates_the_active_card() {
        openCompanyPage();

        step("Select the Institutional Governance approach");
        companyPage.selectApproach("Institutional Governance");

        step("Assert Institutional Governance becomes the active approach");
        Assertions.assertEquals("Institutional Governance", companyPage.activeApproachTitle());
    }

    @Test
    @DisplayName("User can navigate Company to Careers")
    void company_join_now_cta_navigates_to_careers_page() {
        openCompanyPage();

        step("Click the Join now CTA");
        companyPage.clickJoinNow();

        step("Assert the browser navigates to the careers page");
        companyPage.waitForUrlPath("/careers");
    }

    private void openCompanyPage() {
        step("Open the company page");
        companyPage.open();
    }
}
