package org.example.pages;

import org.example.utils.ConfigReader;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class VisualisasiPetaPage extends BasePage {

    private final WebDriverWait wait;

    private static final By MAP_CONTAINER = By.cssSelector(".leaflet-container");

    private static final By FILTER_HEADER =
            By.xpath("//*[contains(normalize-space(),'Filter Jaringan')]");

    private static final By SEARCH_INPUT = By.id("search-input");
    private static final By SEARCH_BUTTON = By.id("search-btn");

    private static final By FILTER_BAIK =
            By.xpath("//button[contains(normalize-space(),'Baik')]");

    private static final By FILTER_PERBAIKAN =
            By.xpath("//button[contains(normalize-space(),'Perbaikan')]");

    private static final By FILTER_RUSAK =
            By.xpath("//button[contains(normalize-space(),'Rusak')]");

    private static final By FILTER_FUNGSI_PIPA = By.id("filter-jenis");

    private static final By NO_RESULT_MESSAGE =
            By.xpath("//*[contains(normalize-space(),'Tidak menemukan hasil')]");

    private static final By POPUP_DETAIL =
            By.xpath("//*[contains(normalize-space(),'KODE MANHOLE') " +
                    "or contains(normalize-space(),'KONDISI') " +
                    "or contains(normalize-space(),'KLASIFIKASI')]");

    // --- Manhole popup / "Lapor Masalah" locators ----------------------------

    private static final By POPUP_CONTENT = By.cssSelector(".leaflet-popup-content");

    // The "Lapor Masalah" trigger is an <a> rendered inside the popup content,
    // linking to /ipal/lapor-masalah?type=manhole&id=...&kode=...&coord=...
    private static final By POPUP_LAPOR_MASALAH_LINK =
            By.xpath(".//a[contains(normalize-space(.),'Lapor Masalah')]");

    private static final By POPUP_KODE_MANHOLE =
            By.xpath(".//*[contains(normalize-space(.),'KODE MANHOLE')]" +
                    "/following-sibling::*[1] | " +
                    ".//div[contains(@style,'font-size:18px')]");

    private String beforeBaikClass;
    private String beforePerbaikanClass;
    private String beforeRusakClass;

    public VisualisasiPetaPage(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void openPublicMap() {
        driver.get(ConfigReader.mapUrl());
        wait.until(ExpectedConditions.visibilityOfElementLocated(MAP_CONTAINER));
        waitForMarkersLoaded();
    }

    /**
     * Waits until at least one manhole/pipe marker has actually rendered on
     * the map. The map container and tiles can appear before the network
     * asset data (fetched separately via API) has finished loading, which
     * causes searches issued too early to find nothing.
     */
    private void waitForMarkersLoaded() {
        try {
            wait.until(d -> !d.findElements(By.cssSelector(
                    ".leaflet-marker-icon, .leaflet-interactive")).isEmpty());
        } catch (TimeoutException e) {
            System.out.println("[DIAG] No map markers detected after waiting — "
                    + "data layer may not have loaded.");
        }
    }

    public boolean isMapDisplayed() {
        return isVisible(MAP_CONTAINER);
    }

    public boolean isFilterDisplayed() {
        return isVisible(FILTER_HEADER);
    }

    public boolean isSearchBarDisplayed() {
        return isVisible(SEARCH_INPUT);
    }

    public void clickFilterBaik() {
        openFilterPanelIfNeeded();
        beforeBaikClass = driver.findElement(FILTER_BAIK).getAttribute("class");
        clickByJavaScript(FILTER_BAIK);
    }

    public void clickFilterPerbaikan() {
        openFilterPanelIfNeeded();
        beforePerbaikanClass = driver.findElement(FILTER_PERBAIKAN).getAttribute("class");
        clickByJavaScript(FILTER_PERBAIKAN);
    }

    public void clickFilterRusak() {
        openFilterPanelIfNeeded();
        beforeRusakClass = driver.findElement(FILTER_RUSAK).getAttribute("class");
        clickByJavaScript(FILTER_RUSAK);
    }

    public boolean isBaikFilterChanged() {
        return !beforeBaikClass.equals(
                driver.findElement(FILTER_BAIK).getAttribute("class")
        );
    }

    public boolean isPerbaikanFilterChanged() {
        return !beforePerbaikanClass.equals(
                driver.findElement(FILTER_PERBAIKAN).getAttribute("class")
        );
    }

    public boolean isRusakFilterChanged() {
        return !beforeRusakClass.equals(
                driver.findElement(FILTER_RUSAK).getAttribute("class")
        );
    }

    public void openDropdownFungsiPipa() {
        openFilterPanelIfNeeded();
        clickByJavaScript(FILTER_FUNGSI_PIPA);
    }

    public boolean isDropdownFungsiPipaDisplayed() {
        return driver.findElements(FILTER_FUNGSI_PIPA).size() > 0;
    }

    public void inputSearch(String keyword) {
        WebElement input = wait.until(
                ExpectedConditions.visibilityOfElementLocated(SEARCH_INPUT)
        );
        input.clear();
        input.sendKeys(keyword);
    }

    public void clickSearchButton() {
        clickByJavaScript(SEARCH_BUTTON);
    }

    public void pressEnterOnSearch() {
        WebElement input = wait.until(
                ExpectedConditions.visibilityOfElementLocated(SEARCH_INPUT)
        );
        input.sendKeys(Keys.ENTER);
    }

    public boolean isNoResultMessageDisplayed() {
        return isVisible(NO_RESULT_MESSAGE);
    }

    public boolean isDetailManholeDisplayed(String code) {
        By detailCode = By.xpath("//*[contains(normalize-space(),'" + code + "')]");
        return isVisible(POPUP_DETAIL) && isVisible(detailCode);
    }

    public boolean isPopupDetailManholeDisplayed() {
        return isVisible(POPUP_DETAIL);
    }

    // --- Manhole search -> popup -> "Lapor Masalah" --------------------------

    /**
     * Searches for the given manhole code via the search bar and presses Enter,
     * waiting for the resulting Leaflet popup to render.
     *
     * <p>The manhole/pipe data layer is fetched asynchronously and can still be
     * loading even after {@link #waitForMarkersLoaded()} sees the *first*
     * marker — a fresh page load combined with a fast Enter can land the
     * search before the full dataset (including the specific manhole being
     * searched for) is indexed, causing a false "tidak ditemukan" even though
     * manual testing finds it reliably. To compensate, this retries the
     * search a few times whenever the "tidak ditemukan" message appears
     * instead of the popup, giving the data layer more time to finish
     * loading between attempts.
     */
    public VisualisasiPetaPage searchManhole(String kodeManhole) {
        final int maxAttempts = 4;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(SEARCH_INPUT));
            input.clear();
            input.sendKeys(kodeManhole);
            sleep(600);
            input.sendKeys(Keys.ENTER);

            SearchOutcome outcome = waitForSearchOutcome();
            if (outcome == SearchOutcome.POPUP_FOUND) {
                return this;
            }

            // outcome == NOT_FOUND or TIMED_OUT: data layer likely still loading.
            System.out.println("[DIAG] searchManhole('" + kodeManhole + "') attempt " + attempt
                    + "/" + maxAttempts + " -> " + outcome + ". Retrying after extra wait...");
            sleep(2000 + (attempt * 1000)); // back off a bit more each retry
        }

        dumpSearchFailureDiagnostics(kodeManhole);
        throw new TimeoutException("searchManhole('" + kodeManhole + "') did not find a popup "
                + "after " + maxAttempts + " attempts. See [DIAG] output above for the last known state.");
    }

    private enum SearchOutcome { POPUP_FOUND, NOT_FOUND, TIMED_OUT }

    /**
     * Polls for up to ~6 seconds for either the popup or the "tidak
     * ditemukan" message to appear, whichever comes first.
     */
    private SearchOutcome waitForSearchOutcome() {
        long deadline = System.currentTimeMillis() + 6000;
        while (System.currentTimeMillis() < deadline) {
            if (!driver.findElements(POPUP_CONTENT).isEmpty()
                    && driver.findElement(POPUP_CONTENT).isDisplayed()) {
                return SearchOutcome.POPUP_FOUND;
            }
            if (isVisible(NO_RESULT_MESSAGE)) {
                return SearchOutcome.NOT_FOUND;
            }
            sleep(300);
        }
        return SearchOutcome.TIMED_OUT;
    }

    private void dumpSearchFailureDiagnostics(String kodeManhole) {
        System.out.println("\n=== [DIAG] searchManhole('" + kodeManhole + "') failed — dumping state ===");
        System.out.println("[DIAG] Current URL: " + driver.getCurrentUrl());
        try {
            WebElement input = driver.findElement(SEARCH_INPUT);
            System.out.println("[DIAG] search-input value: '" + input.getAttribute("value") + "'");
        } catch (Exception e) {
            System.out.println("[DIAG] search-input not found: " + e.getMessage());
        }
        List<WebElement> suggestions = driver.findElements(By.cssSelector(".search-suggestion-item"));
        System.out.println("[DIAG] suggestion items in DOM: " + suggestions.size());
        for (WebElement s : suggestions) {
            System.out.println("[DIAG]   displayed=" + s.isDisplayed() + " text='" + s.getText() + "'");
        }
        List<WebElement> popups = driver.findElements(By.cssSelector(".leaflet-popup-content"));
        System.out.println("[DIAG] .leaflet-popup-content elements in DOM (any visibility): " + popups.size());
        for (WebElement p : popups) {
            System.out.println("[DIAG]   displayed=" + p.isDisplayed()
                    + " text='" + p.getText().replace("\n", " ") + "'");
        }
        List<WebElement> noResult = driver.findElements(By.xpath(
                "//*[contains(normalize-space(.),'tidak ditemukan') or contains(normalize-space(.),'Tidak menemukan')]"));
        System.out.println("[DIAG] 'tidak ditemukan' message elements: " + noResult.size());
        String bodySnippet = driver.findElement(By.tagName("body")).getText();
        System.out.println("[DIAG] body text snippet (first 400 chars):\n"
                + (bodySnippet.length() > 400 ? bodySnippet.substring(0, 400) : bodySnippet));
        System.out.println("=== [DIAG] end dump ===\n");
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** Returns the manhole code shown in the currently open popup header. */
    public String getPopupKodeManhole() {
        WebElement popup = wait.until(ExpectedConditions.visibilityOfElementLocated(POPUP_CONTENT));
        try {
            return popup.findElement(POPUP_KODE_MANHOLE).getText().trim();
        } catch (NoSuchElementException e) {
            return popup.getText();
        }
    }

    public boolean isPopupOpenWithLaporMasalahLink() {
        try {
            WebElement popup = wait.until(ExpectedConditions.visibilityOfElementLocated(POPUP_CONTENT));
            return !popup.findElements(POPUP_LAPOR_MASALAH_LINK).isEmpty();
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Clicks the "Lapor Masalah" link inside the currently open manhole popup.
     * This navigates to /ipal/lapor-masalah?type=manhole&id=...&kode=...&coord=...
     * which pre-fills the ID IPAL / Titik Lokasi fields on that page.
     */
    public LaporMasalahPage clickLaporMasalahOnPopup() {
        WebElement popup = wait.until(ExpectedConditions.visibilityOfElementLocated(POPUP_CONTENT));
        WebElement laporLink = popup.findElement(POPUP_LAPOR_MASALAH_LINK);
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center'});", laporLink);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", laporLink);
        return new LaporMasalahPage(driver);
    }

    private void openFilterPanelIfNeeded() {
        if (driver.findElements(FILTER_BAIK).isEmpty()) {
            clickByJavaScript(FILTER_HEADER);
        }
    }

    private boolean isVisible(By locator) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    private void clickByJavaScript(By locator) {
        WebElement element = wait.until(
                ExpectedConditions.presenceOfElementLocated(locator)
        );

        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center'});", element);

        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].click();", element);
    }
}