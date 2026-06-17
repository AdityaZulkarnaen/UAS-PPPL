package org.example.pages;

import org.example.utils.ConfigReader;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class VisualisasiPetaPage extends BasePage {

    private final WebDriverWait wait;

    private static final By MAP_CONTAINER = By.cssSelector(".leaflet-container");
    private static final By FILTER_HEADER = By.xpath("//*[contains(normalize-space(),'Filter Jaringan')]");
    private static final By SEARCH_INPUT = By.id("search-input");
    private static final By SEARCH_BUTTON = By.id("search-btn");

    private static final By FILTER_BAIK = By.xpath("//button[contains(normalize-space(),'Baik')]");
    private static final By FILTER_PERBAIKAN = By.xpath("//button[contains(normalize-space(),'Perbaikan')]");
    private static final By FILTER_RUSAK = By.xpath("//button[contains(normalize-space(),'Rusak')]");
    private static final By FILTER_FUNGSI_PIPA = By.id("filter-jenis");

    private static final By NO_RESULT_MESSAGE = By.xpath("//*[contains(normalize-space(),'Tidak menemukan hasil')]");
    private static final By POPUP_DETAIL = By.xpath("//*[contains(normalize-space(),'KODE MANHOLE') or contains(normalize-space(),'KONDISI') or contains(normalize-space(),'KLASIFIKASI')]");

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
        return !beforeBaikClass.equals(driver.findElement(FILTER_BAIK).getAttribute("class"));
    }

    public boolean isPerbaikanFilterChanged() {
        return !beforePerbaikanClass.equals(driver.findElement(FILTER_PERBAIKAN).getAttribute("class"));
    }

    public boolean isRusakFilterChanged() {
        return !beforeRusakClass.equals(driver.findElement(FILTER_RUSAK).getAttribute("class"));
    }

    public void openDropdownFungsiPipa() {
        openFilterPanelIfNeeded();
        clickByJavaScript(FILTER_FUNGSI_PIPA);
    }

    public boolean isDropdownFungsiPipaDisplayed() {
        return driver.findElements(FILTER_FUNGSI_PIPA).size() > 0;
    }

    public void inputSearch(String keyword) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(SEARCH_INPUT));
        input.clear();
        input.sendKeys(keyword);
    }

    public void clickSearchButton() {
        clickByJavaScript(SEARCH_BUTTON);
    }

    public void pressEnterOnSearch() {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(SEARCH_INPUT));
        input.sendKeys(Keys.ENTER);
    }

    public boolean isNoResultMessageDisplayed() {
        return isVisible(NO_RESULT_MESSAGE);
    }

    public boolean isPopupDetailManholeDisplayed() {
        return isVisible(POPUP_DETAIL);
    }

    // Compatibility untuk LaporMasalahManholeSteps.java temanmu
    public void searchManhole(String keyword) {
        inputSearch(keyword);
        pressEnterOnSearch();
    }

    public boolean isPopupOpenWithLaporMasalahLink() {
        return isPopupDetailManholeDisplayed()
                && driver.findElements(By.xpath("//*[contains(normalize-space(),'Lapor Masalah')]")).size() > 0;
    }

    public LaporMasalahPage clickLaporMasalahOnPopup() {
        By laporMasalahButton = By.xpath("//*[contains(normalize-space(),'Lapor Masalah')]");
        clickByJavaScript(laporMasalahButton);
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
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", element
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();", element
        );
    }
}