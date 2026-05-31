package org.example.pages;

import org.example.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Map landing page + login page.
 * Flow: /ipal/map -> Admin Login -> "Login dengan Email" tab -> Masuk.
 */
public class LoginPage extends BasePage {

    // On the map page: blue "Admin Login" button (top-right).
    private static final By ADMIN_LOGIN_LINK =
            By.xpath("//*[self::a or self::button][contains(normalize-space(.),'Admin Login')]");

    // On /login: the "Login dengan Email" tab button (toggles the email form).
    private static final By EMAIL_TAB = By.id("email-tab");

    private static final By EMAIL_INPUT = By.id("email");
    private static final By PASSWORD_INPUT = By.id("password");
    private static final By MASUK_BUTTON =
            By.cssSelector("#email-login-form button[type='submit']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage openMap() {
        driver.get(ConfigReader.mapUrl());
        return this;
    }

    public LoginPage clickAdminLogin() {
        click(ADMIN_LOGIN_LINK);
        return this;
    }

    public LoginPage selectEmailTab() {
        click(EMAIL_TAB);
        return this;
    }

    /** Full login via the email tab; returns the main dashboard page. */
    public MainDashboardPage loginWithEmail(String email, String password) {
        type(EMAIL_INPUT, email);
        type(PASSWORD_INPUT, password);
        click(MASUK_BUTTON);
        return new MainDashboardPage(driver);
    }
}
