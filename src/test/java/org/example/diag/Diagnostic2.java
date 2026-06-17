package org.example.diag;

import org.example.pages.VisualisasiPetaPage;
import org.example.utils.ConfigReader;
import org.example.utils.DriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * TEMPORARY diagnostic: opens the public map, searches for manhole
 * "4.2_J1.MH-26", opens its popup, clicks "Lapor Masalah", and dumps DOM
 * info for the report form (description field, photo dropzone, captcha
 * block) so locators can be pinned down precisely.
 *
 * Run once locally (this requires a real Chrome + network access to the
 * target site), copy the console output, then delete this file.
 * Does NOT submit the form (no data mutation) — it stops right before
 * clicking "Kirim Laporan".
 */
public class Diagnostic2 {

    public static void main(String[] args) throws InterruptedException {
        WebDriver driver = DriverFactory.getDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        try {
            VisualisasiPetaPage mapPage = new VisualisasiPetaPage(driver);
            mapPage.openPublicMap();
            System.out.println("=== Map loaded: " + driver.getCurrentUrl());

            // --- Search for the manhole -----------------------------------
            mapPage.inputSearch("4.2_J1.MH-26");
            mapPage.pressEnterOnSearch();
            Thread.sleep(3000);

            System.out.println("\n=== AFTER SEARCH — page source snippet (popups/leaflet) ===");
            dumpElementsByCss(driver, ".leaflet-popup, .leaflet-popup-content, [class*='popup']");

            System.out.println("\n=== Buttons / links containing 'Lapor' ===");
            dumpElementsByXpath(driver,
                    "//*[contains(normalize-space(.),'Lapor') and (self::button or self::a)]");

            // --- Click "Lapor Masalah" if found ----------------------------
            List<WebElement> laporButtons = driver.findElements(By.xpath(
                    "//*[contains(normalize-space(.),'Lapor Masalah') and (self::button or self::a)]"));
            if (!laporButtons.isEmpty()) {
                System.out.println("\n=== Found 'Lapor Masalah' button, clicking it ===");
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView({block:'center'});", laporButtons.get(0));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", laporButtons.get(0));
                Thread.sleep(3000);
            } else {
                System.out.println("\n!!! 'Lapor Masalah' button NOT found after search. "
                        + "Dumping full page text for inspection:");
                System.out.println(driver.findElement(By.tagName("body")).getText());
            }

            System.out.println("\n=== Current URL after clicking Lapor Masalah: " + driver.getCurrentUrl());

            // --- Dump the report form structure ----------------------------
            System.out.println("\n=== Inputs / textareas / selects on the form ===");
            dumpFormControls(driver);

            System.out.println("\n=== #field-deskripsi element ===");
            dumpElementsByCss(driver, "#field-deskripsi");

            System.out.println("\n=== #foto-dropzone element and children ===");
            dumpElementsByCss(driver, "#foto-dropzone");
            dumpElementsByCss(driver, "#foto-dropzone input[type='file']");
            dumpElementsByCss(driver, "#foto-dropzone *");

            System.out.println("\n=== Elements mentioning 'Verifikasi' / captcha ===");
            dumpElementsByXpath(driver,
                    "//*[contains(normalize-space(.),'Verifikasi') or contains(normalize-space(.),'hasil dari')]");

            System.out.println("\n=== Inputs near captcha (siblings/following of captcha text) ===");
            dumpElementsByXpath(driver,
                    "//*[contains(normalize-space(.),'hasil dari')]/following::input[1]");

            System.out.println("\n=== 'Ganti' button (captcha refresh) ===");
            dumpElementsByXpath(driver, "//*[contains(normalize-space(.),'Ganti')]");

            System.out.println("\n=== 'Kirim Laporan' submit button ===");
            dumpElementsByXpath(driver,
                    "//button[contains(normalize-space(.),'Kirim Laporan')] | //*[@type='submit']");

            System.out.println("\n=== Titik Lokasi / ID IPAL fields (readonly autofill) ===");
            dumpElementsByXpath(driver,
                    "//*[contains(normalize-space(.),'ID IPAL') or contains(normalize-space(.),'Titik Lokasi')]");

            System.out.println("\n=== FULL outerHTML of the form (best-effort) ===");
            try {
                WebElement form = driver.findElement(By.tagName("form"));
                String outerHtml = (String) ((JavascriptExecutor) driver)
                        .executeScript("return arguments[0].outerHTML;", form);
                System.out.println(outerHtml);
            } catch (Exception e) {
                System.out.println("No <form> tag found, or failed to read outerHTML: " + e.getMessage());
            }

            System.out.println("\n=== DONE. NOT submitting the form. ===");

        } finally {
            DriverFactory.quitDriver();
        }
    }

    private static void dumpElementsByCss(WebDriver driver, String css) {
        try {
            List<WebElement> els = driver.findElements(By.cssSelector(css));
            System.out.println("[css=" + css + "] found " + els.size() + " element(s)");
            for (WebElement el : els) {
                printElementInfo(driver, el);
            }
        } catch (Exception e) {
            System.out.println("[css=" + css + "] ERROR: " + e.getMessage());
        }
    }

    private static void dumpElementsByXpath(WebDriver driver, String xpath) {
        try {
            List<WebElement> els = driver.findElements(By.xpath(xpath));
            System.out.println("[xpath=" + xpath + "] found " + els.size() + " element(s)");
            for (WebElement el : els) {
                printElementInfo(driver, el);
            }
        } catch (Exception e) {
            System.out.println("[xpath=" + xpath + "] ERROR: " + e.getMessage());
        }
    }

    private static void dumpFormControls(WebDriver driver) {
        for (String tag : new String[]{"input", "textarea", "select", "button"}) {
            List<WebElement> els = driver.findElements(By.tagName(tag));
            System.out.println("--- <" + tag + "> elements: " + els.size() + " ---");
            for (WebElement el : els) {
                printElementInfo(driver, el);
            }
        }
    }

    private static void printElementInfo(WebDriver driver, WebElement el) {
        try {
            String tag = el.getTagName();
            String id = safe(el.getAttribute("id"));
            String name = safe(el.getAttribute("name"));
            String type = safe(el.getAttribute("type"));
            String cls = safe(el.getAttribute("class"));
            String placeholder = safe(el.getAttribute("placeholder"));
            String text = safe(el.getText());
            boolean displayed = el.isDisplayed();
            System.out.println(String.format(
                    "  <%s> id='%s' name='%s' type='%s' class='%s' placeholder='%s' displayed=%s text='%s'",
                    tag, id, name, type, cls, placeholder, displayed,
                    text.length() > 80 ? text.substring(0, 80) + "..." : text));
        } catch (Exception e) {
            System.out.println("  [stale or error reading element: " + e.getMessage() + "]");
        }
    }

    private static String safe(String s) {
        return s == null ? "" : s.replace("\n", " ").trim();
    }
}