package com.relativ.pages;

import com.relativ.core.SelectorRepository;
import com.relativ.core.UiConfig;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class LoginPage extends BasePage {

    public LoginPage(WebDriver driver, WebDriverWait wait, SelectorRepository selectors, UiConfig config) {
        super(driver, wait, selectors, config);
    }

    public void login(String username, String password) {
        type("login.username", username);
        type("login.password", password);
        click("login.submit");
    }

    public boolean waitForSuccessfulLogin(String beforeSubmitUrl) {
        return wait.until(d -> {
            boolean hasError = hasVisibleOptional("login.error");
            if (hasError) {
                return false;
            }

            boolean hasSuccess = hasVisibleOptional("login.success");
            boolean urlChanged = !beforeSubmitUrl.equals(currentUrl());
            boolean passwordFieldHidden = findVisibleOptional("login.password").isEmpty();

            return hasSuccess || (urlChanged && passwordFieldHidden);
        });
    }
}

