package com.relativ.tests.auth;

import com.relativ.core.BaseUiTest;
import com.relativ.core.UiConfig;
import com.relativ.pages.HomePage;
import com.relativ.pages.LoginPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("optional-login")
@DisplayName("Login Flows")
public class LoginTest extends BaseUiTest {

    private HomePage homePage;
    private LoginPage loginPage;

    @Override
    protected boolean shouldStartDriver() {
        return UiConfig.hasCredentialsConfigured();
    }

    @BeforeEach
    void setUpPages() {
        homePage = new HomePage(driver, wait, selectors, config);
        loginPage = new LoginPage(driver, wait, selectors, config);
    }

    @Test
    @DisplayName("User can sign in with configured credentials")
    void login_success_path() {
        Assumptions.assumeTrue(
                config.hasCredentials(),
                "Set RELATIV_USERNAME and RELATIV_PASSWORD to run the login test."
        );

        step("Open the configured site");
        homePage.openSite();

        step("Open the login page");
        Assertions.assertTrue(
                homePage.openLoginPage(),
                "Could not locate the login page. Set RELATIV_LOGIN_URL or update the home.loginTrigger selector."
        );

        step("Submit configured credentials");
        String beforeSubmitUrl = loginPage.currentUrl();
        loginPage.login(config.username(), config.password());

        step("Assert login is successful");
        Assertions.assertTrue(
                loginPage.waitForSuccessfulLogin(beforeSubmitUrl),
                "Login was not confirmed. Check selectors and credentials."
        );
    }
}
