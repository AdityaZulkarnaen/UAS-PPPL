package org.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Main admin dashboard (/dashboard) reached right after login.
 * Sidebar contains the "Module IPAL" entry under SETTING.
 */
public class MainDashboardPage extends BasePage {

    private static final By MODULE_IPAL_LINK =
            By.xpath("//*[self::a or self::button or self::span]"
                    + "[contains(normalize-space(.),'Module IPAL')]");

    public MainDashboardPage(WebDriver driver) {
        super(driver);
    }

    /** Waits until the dashboard URL is reached. */
    public MainDashboardPage waitLoaded() {
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        return this;
    }

    public IpalDashboardPage openModuleIpal() {
        click(MODULE_IPAL_LINK);
        return new IpalDashboardPage(driver);
    }
}
