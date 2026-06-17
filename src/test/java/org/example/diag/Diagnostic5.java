package org.example.diag;

import org.example.utils.ConfigReader;
import org.example.utils.DriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * TEMPORARY diagnostic: searches "4.2_J1.MH-26", scopes strictly inside
 * .leaflet-popup-content to find the "Lapor Masalah" trigger, dumps its
 * exact tag/attributes, clicks it, and verifies field-id-ipal /
 * field-koordinat auto-fill on /ipal/lapor-masalah.
 * Does NOT submit the form. Run once, then delete.
 */
public class Diagnostic5 {

    public static void main(String[] args) throws InterruptedException {
        WebDriver driver = DriverFactory.getDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        try {
            driver.get(ConfigReader.mapUrl());
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("search-input")));
            Thread.sleep(1500);

            WebElement searchInput = driver.findElement(By.id("search-input"));
            searchInput.clear();
            searchInput.sendKeys("4.2_J1.MH-26");
            Thread.sleep(500);
            searchInput.sendKeys(Keys.ENTER);
            Thread.sleep(3000);

            System.out.println("=== Looking for .leaflet-popup-content ===");
            List<WebElement> popups = driver.findElements(By.cssSelector(".leaflet-popup-content"));
            System.out.println("found=" + popups.size());

            if (popups.isEmpty()) {
                System.out.println("!!! No popup content found. Aborting.");
                return;
            }

            WebElement popup = popups.get(0);

            System.out.println("\n=== FULL innerHTML of .leaflet-popup-content ===");
            String innerHtml = (String) ((JavascriptExecutor) driver)
                    .executeScript("return arguments[0].innerHTML;", popup);
            System.out.println(innerHtml);

            System.out.println("\n=== Elements INSIDE popup containing 'Lapor' ===");
            List<WebElement> laporInPopup = popup.findElements(
                    By.xpath(".//*[contains(normalize-space(.),'Lapor')]"));
            System.out.println("found=" + laporInPopup.size());
            for (WebElement el : laporInPopup) {
                System.out.println("  <" + el.getTagName() + "> id='" + el.getAttribute("id")
                        + "' class='" + el.getAttribute("class")
                        + "' onclick='" + el.getAttribute("onclick")
                        + "' href='" + el.getAttribute("href")
                        + "' displayed=" + el.isDisplayed()
                        + " text='" + el.getText() + "'");
            }

            // Click the deepest / most specific "Lapor Masalah" element inside the popup
            if (!laporInPopup.isEmpty()) {
                WebElement target = laporInPopup.get(laporInPopup.size() - 1);
                System.out.println("\n=== Clicking innermost match: <" + target.getTagName()
                        + "> text='" + target.getText() + "' ===");
                try {
                    ((JavascriptExecutor) driver).executeScript(
                            "arguments[0].scrollIntoView({block:'center'});", target);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", target);
                } catch (Exception e) {
                    System.out.println("JS click failed: " + e.getMessage() + " — trying .click()");
                    target.click();
                }
                Thread.sleep(2500);

                System.out.println("=== URL after click: " + driver.getCurrentUrl());
                if (driver.getCurrentUrl().contains("lapor-masalah")) {
                    System.out.println("=== field-id-ipal value: " + safeValue(driver, "#field-id-ipal"));
                    System.out.println("=== field-koordinat value: " + safeValue(driver, "#field-koordinat"));
                } else {
                    System.out.println("!!! Did not navigate to lapor-masalah page.");
                }
            }

            System.out.println("\n=== DONE ===");
        } finally {
            DriverFactory.quitDriver();
        }
    }

    private static String safeValue(WebDriver driver, String css) {
        try {
            return driver.findElement(By.cssSelector(css)).getAttribute("value");
        } catch (Exception e) {
            return "N/A (" + e.getMessage() + ")";
        }
    }
}