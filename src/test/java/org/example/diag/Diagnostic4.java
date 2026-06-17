package org.example.diag;

import org.example.pages.VisualisasiPetaPage;
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
 * TEMPORARY diagnostic: step-by-step debug of the manhole search flow on the
 * public map. Does NOT submit any form. Run once, then delete.
 */
public class Diagnostic4 {

    public static void main(String[] args) throws InterruptedException {
        WebDriver driver = DriverFactory.getDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        try {
            driver.get(ConfigReader.mapUrl());
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("search-input")));
            Thread.sleep(2000); // let map tiles / data layer finish loading
            System.out.println("=== Map page loaded: " + driver.getCurrentUrl());

            System.out.println("\n=== search-input element ===");
            WebElement searchInput = driver.findElement(By.id("search-input"));
            System.out.println("displayed=" + searchInput.isDisplayed()
                    + " enabled=" + searchInput.isEnabled()
                    + " placeholder=" + searchInput.getAttribute("placeholder")
                    + " value=" + searchInput.getAttribute("value"));

            System.out.println("\n=== search-btn element ===");
            List<WebElement> searchBtns = driver.findElements(By.id("search-btn"));
            System.out.println("found=" + searchBtns.size());
            if (!searchBtns.isEmpty()) {
                WebElement b = searchBtns.get(0);
                System.out.println("displayed=" + b.isDisplayed() + " text='" + b.getText() + "'");
            }

            // --- Type the keyword ---------------------------------------------------
            searchInput.clear();
            searchInput.sendKeys("4.2_J1.MH-26");
            Thread.sleep(800);
            System.out.println("\n=== After typing, input value=" + searchInput.getAttribute("value"));

            // Check for an autocomplete/suggestion dropdown appearing while typing.
            System.out.println("\n=== Possible autocomplete/suggestion elements (class*=suggest, class*=autocomplete, class*=dropdown, class*=result) ===");
            List<WebElement> suggestions = driver.findElements(By.cssSelector(
                    "[class*='suggest'], [class*='autocomplete'], [class*='dropdown'], [class*='result'], [class*='option']"));
            for (WebElement el : suggestions) {
                if (el.isDisplayed()) {
                    System.out.println("  <" + el.getTagName() + "> class='" + el.getAttribute("class")
                            + "' text='" + truncate(el.getText()) + "'");
                }
            }

            // --- Press Enter ---------------------------------------------------------
            searchInput.sendKeys(Keys.ENTER);
            Thread.sleep(3000);
            System.out.println("\n=== After ENTER — current URL: " + driver.getCurrentUrl());

            System.out.println("=== Body text snippet (first 500 chars) ===");
            String bodyText = driver.findElement(By.tagName("body")).getText();
            System.out.println(bodyText.length() > 500 ? bodyText.substring(0, 500) : bodyText);

            System.out.println("\n=== Any 'tidak ditemukan' / no-result message visible? ===");
            List<WebElement> noResult = driver.findElements(By.xpath(
                    "//*[contains(normalize-space(.),'tidak ditemukan') or contains(normalize-space(.),'Tidak menemukan')]"));
            for (WebElement el : noResult) {
                if (el.isDisplayed()) {
                    System.out.println("  FOUND: " + el.getText());
                }
            }

            System.out.println("\n=== All elements containing 'MH-26' or 'MH26' or '4.2_J1' (any visibility) ===");
            List<WebElement> mhEls = driver.findElements(By.xpath(
                    "//*[contains(.,'MH-26') or contains(.,'MH26') or contains(.,'4.2_J1')]"));
            System.out.println("found total=" + mhEls.size());
            for (WebElement el : mhEls) {
                System.out.println("  <" + el.getTagName() + "> displayed=" + el.isDisplayed()
                        + " class='" + el.getAttribute("class") + "' text='" + truncate(el.getText()) + "'");
            }

            // --- Try clicking the search button instead, in case Enter doesn't trigger it ---
            System.out.println("\n=== Retrying via clicking #search-btn instead of Enter ===");
            searchInput.clear();
            searchInput.sendKeys("4.2_J1.MH-26");
            Thread.sleep(500);
            if (!searchBtns.isEmpty()) {
                WebElement btn = driver.findElement(By.id("search-btn"));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
                Thread.sleep(3000);
            }
            System.out.println("=== URL after clicking search-btn: " + driver.getCurrentUrl());

            System.out.println("\n=== All elements containing 'MH-26' after button click ===");
            mhEls = driver.findElements(By.xpath("//*[contains(.,'MH-26')]"));
            System.out.println("found total=" + mhEls.size());
            for (WebElement el : mhEls) {
                System.out.println("  <" + el.getTagName() + "> displayed=" + el.isDisplayed()
                        + " class='" + el.getAttribute("class") + "' text='" + truncate(el.getText()) + "'");
            }

            System.out.println("\n=== Leaflet markers present on map (img.leaflet-marker-icon, .leaflet-interactive) ===");
            List<WebElement> markers = driver.findElements(By.cssSelector(
                    ".leaflet-marker-icon, .leaflet-interactive, [class*='marker']"));
            System.out.println("marker-like elements found=" + markers.size());

            System.out.println("\n=== Trying a known-good simpler keyword: 'Gondokusuman' (from existing feature file) ===");
            searchInput.clear();
            searchInput.sendKeys("Gondokusuman");
            searchInput.sendKeys(Keys.ENTER);
            Thread.sleep(3000);
            List<WebElement> whEls = driver.findElements(By.xpath("//*[contains(.,'WH1')]"));
            System.out.println("Elements containing 'WH1' after searching 'Gondokusuman': " + whEls.size());
            for (WebElement el : whEls) {
                System.out.println("  <" + el.getTagName() + "> displayed=" + el.isDisplayed()
                        + " text='" + truncate(el.getText()) + "'");
            }

            System.out.println("\n=== DONE ===");
        } finally {
            DriverFactory.quitDriver();
        }
    }

    private static String truncate(String s) {
        if (s == null) return "";
        s = s.replace("\n", " ");
        return s.length() > 150 ? s.substring(0, 150) + "..." : s;
    }
}