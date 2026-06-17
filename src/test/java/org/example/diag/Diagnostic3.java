package org.example.diag;

import org.example.pages.VisualisasiPetaPage;
import org.example.utils.DriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * TEMPORARY diagnostic: searches manhole "4.2_J1.MH-26" on the public map,
 * inspects the resulting popup, finds the "Lapor Masalah" button INSIDE that
 * popup (not the navbar link), clicks it, and confirms whether
 * field-id-ipal / field-koordinat get auto-filled.
 * Does NOT submit the form. Run once, then delete.
 */
public class Diagnostic3 {

    public static void main(String[] args) throws InterruptedException {
        WebDriver driver = DriverFactory.getDriver();
        try {
            VisualisasiPetaPage mapPage = new VisualisasiPetaPage(driver);
            mapPage.openPublicMap();

            mapPage.inputSearch("4.2_J1.MH-26");
            mapPage.pressEnterOnSearch();
            Thread.sleep(3000);

            System.out.println("=== Current URL after search: " + driver.getCurrentUrl());

            System.out.println("\n=== All visible elements containing 'MH-26' ===");
            List<WebElement> mhEls = driver.findElements(
                    By.xpath("//*[contains(normalize-space(.),'MH-26')]"));
            System.out.println("found " + mhEls.size());
            for (WebElement el : mhEls) {
                print(el);
            }

            System.out.println("\n=== Popup / detail container candidates (class*=popup, class*=detail, class*=panel, class*=card, class*=sheet) ===");
            List<WebElement> popups = driver.findElements(By.cssSelector(
                    "[class*='popup'], [class*='detail'], [class*='panel'], [class*='card'], [class*='sheet'], [class*='modal']"));
            System.out.println("found " + popups.size());
            for (WebElement el : popups) {
                if (el.isDisplayed()) {
                    print(el);
                }
            }

            System.out.println("\n=== ALL buttons/links containing 'Lapor' (visible only) ===");
            List<WebElement> laporEls = driver.findElements(By.xpath(
                    "//*[contains(normalize-space(.),'Lapor') and (self::button or self::a)]"));
            for (WebElement el : laporEls) {
                System.out.println("displayed=" + el.isDisplayed() + " | tag=" + el.getTagName()
                        + " | class=" + el.getAttribute("class")
                        + " | href=" + el.getAttribute("href")
                        + " | text='" + el.getText() + "'");
            }

            // Try clicking the VISIBLE one that is not the navbar (heuristic: navbar link
            // has href ending in /ipal/lapor-masalah with no query/state; popup button is
            // likely a <button> or has different class). We print both for you to compare.
            WebElement visibleLaporButton = null;
            for (WebElement el : laporEls) {
                if (el.isDisplayed() && el.getTagName().equals("button")) {
                    visibleLaporButton = el;
                    break;
                }
            }
            if (visibleLaporButton == null) {
                // fallback: any displayed one
                for (WebElement el : laporEls) {
                    if (el.isDisplayed()) {
                        visibleLaporButton = el;
                        break;
                    }
                }
            }

            if (visibleLaporButton != null) {
                System.out.println("\n=== Clicking candidate popup 'Lapor Masalah' button: tag="
                        + visibleLaporButton.getTagName() + " class=" + visibleLaporButton.getAttribute("class"));
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView({block:'center'});", visibleLaporButton);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", visibleLaporButton);
                Thread.sleep(2500);

                System.out.println("=== URL after click: " + driver.getCurrentUrl());
                System.out.println("=== field-id-ipal value: "
                        + getValueSafe(driver, "#field-id-ipal"));
                System.out.println("=== field-koordinat value: "
                        + getValueSafe(driver, "#field-koordinat"));
            } else {
                System.out.println("\n!!! No visible 'Lapor Masalah' element found near the popup.");
            }

        } finally {
            DriverFactory.quitDriver();
        }
    }

    private static String getValueSafe(WebDriver driver, String css) {
        try {
            return driver.findElement(By.cssSelector(css)).getAttribute("value");
        } catch (Exception e) {
            return "N/A (" + e.getMessage() + ")";
        }
    }

    private static void print(WebElement el) {
        try {
            System.out.println("  <" + el.getTagName() + "> class='" + el.getAttribute("class")
                    + "' id='" + el.getAttribute("id") + "' displayed=" + el.isDisplayed()
                    + " text='" + truncate(el.getText()) + "'");
        } catch (Exception e) {
            System.out.println("  [stale: " + e.getMessage() + "]");
        }
    }

    private static String truncate(String s) {
        if (s == null) return "";
        s = s.replace("\n", " ");
        return s.length() > 100 ? s.substring(0, 100) + "..." : s;
    }
}