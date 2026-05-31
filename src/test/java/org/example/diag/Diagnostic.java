package org.example.diag;

import org.example.pages.LoginPage;
import org.example.utils.ConfigReader;
import org.example.utils.DriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;

/**
 * TEMPORARY diagnostic: logs in, navigates to /ipal/upload, opens the upload
 * modal, and dumps DOM info so the React-SPA locators can be pinned down.
 * Run once, then delete. Does NOT upload anything (no data mutation).
 */
public class Diagnostic {

    public static void main(String[] args) throws InterruptedException {
        WebDriver driver = DriverFactory.getDriver();
        try {
            new LoginPage(driver)
                    .openMap()
                    .clickAdminLogin()
                    .selectEmailTab()
                    .loginWithEmail(ConfigReader.email(), ConfigReader.password());

            Thread.sleep(4000);
            System.out.println("=== After login URL: " + driver.getCurrentUrl());

            System.out.println("\n=== /dashboard LINKS (a) ===");
            for (WebElement a : driver.findElements(By.tagName("a"))) {
                String txt = a.getText().trim();
                if (!txt.isEmpty()) {
                    System.out.println("[a] '" + txt + "' href=" + a.getAttribute("href"));
                }
            }

            // Navigate to the IPAL module dashboard and dump its sidebar.
            driver.get(ConfigReader.baseUrl() + "/ipal/dashboard");
            Thread.sleep(4000);
            System.out.println("\n=== /ipal/dashboard URL: " + driver.getCurrentUrl());
            System.out.println("=== /ipal/dashboard LINKS (a) ===");
            for (WebElement a : driver.findElements(By.tagName("a"))) {
                String txt = a.getText().trim();
                if (!txt.isEmpty()) {
                    System.out.println("[a] '" + txt + "' href=" + a.getAttribute("href"));
                }
            }
        } finally {
            DriverFactory.quitDriver();
        }
    }
}
