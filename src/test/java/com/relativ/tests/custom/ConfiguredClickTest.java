package com.relativ.tests.custom;

import com.relativ.core.BaseUiTest;
import com.relativ.pages.HomePage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

@DisplayName("Configured Click Flow")
public class ConfiguredClickTest extends BaseUiTest {

    private HomePage homePage;

    @BeforeEach
    void setUpPages() {
        homePage = new HomePage(driver, wait, selectors, config);
    }

    @Test
    @DisplayName("User can run all configured click actions")
    void configured_click_path_runs_all_configured_selectors() {
        step("Open the configured base site");
        homePage.openSite();

        for (String selectorKey : config.clickKeys()) {
            step("Run configured click for selector key " + selectorKey);
            homePage.click(selectorKey);
        }

        step("Keep the browser open for the configured observation interval");
        pauseBrowser(config.stayOpenSeconds());

        step("Assert the browser is still on a valid page URL");
        Assertions.assertTrue(
                homePage.currentUrl().startsWith("http"),
                "Browser did not open a valid page URL."
        );
    }

    private void pauseBrowser(int seconds) {
        if (seconds <= 0) {
            return;
        }
        try {
            Thread.sleep(Duration.ofSeconds(seconds).toMillis());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            Assertions.fail("Browser wait was interrupted.", exception);
        }
    }
}
