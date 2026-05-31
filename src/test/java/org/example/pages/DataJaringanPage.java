package org.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * "Manajemen Data Jaringan" page (/ipal/upload).
 * Has a Jalur Pipa / Manhole toggle and the "Upload Jaringan Pipa" button
 * that opens the upload modal.
 */
public class DataJaringanPage extends BasePage {

    private static final By TAB_JALUR_PIPA =
            By.xpath("//button[@id='mode-pipe-btn-desktop']");
    private static final By TAB_MANHOLE =
            By.xpath("//button[@id='mode-manhole-btn-desktop']");
    private static final By UPLOAD_BUTTON =
            By.xpath("//button[contains(normalize-space(.),'Upload Jaringan')]");

    public DataJaringanPage(WebDriver driver) {
        super(driver);
    }

    public DataJaringanPage waitLoaded() {
        wait.until(ExpectedConditions.urlContains("/ipal/upload"));
        return this;
    }

    /** Select which dataset to upload: "Jalur Pipa" or "Manhole". */
    public DataJaringanPage selectTab(String dataset) {
        if (dataset.equalsIgnoreCase("Manhole")) {
            click(TAB_MANHOLE);
        } else {
            click(TAB_JALUR_PIPA);
        }
        return this;
    }

    public UploadModal openUploadModal() {
        click(UPLOAD_BUTTON);
        return new UploadModal(driver);
    }
}
