package org.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * IPAL module dashboard (/ipal/dashboard).
 * Sidebar contains "Data Jaringan".
 */
public class IpalDashboardPage extends BasePage {

    private static final By DATA_JARINGAN_LINK =
            By.xpath("//*[self::a or self::button or self::span]"
                    + "[contains(normalize-space(.),'Data Jaringan')]");

    public IpalDashboardPage(WebDriver driver) {
        super(driver);
    }

    public IpalDashboardPage waitLoaded() {
        wait.until(ExpectedConditions.urlContains("/ipal/dashboard"));
        return this;
    }

    public DataJaringanPage openDataJaringan() {
        click(DATA_JARINGAN_LINK);
        return new DataJaringanPage(driver);
    }
}
