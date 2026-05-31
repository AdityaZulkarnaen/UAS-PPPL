package org.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/**
 * "Upload GeoJSON" modal dialog. Contains the hidden file input, the "Unggah"
 * submit button, and renders a result alert (rose box on failure, emerald box
 * on success) after submitting.
 */
public class UploadModal extends BasePage {

    private static final By MODAL_TITLE =
            By.xpath("//*[contains(normalize-space(.),'Upload GeoJSON')]");
    private static final By FILE_INPUT =
            By.cssSelector("input[type='file']");
    private static final By UNGGAH_BUTTON =
            By.xpath("//button[contains(normalize-space(.),'Unggah')]");

    // Result alert boxes (Tailwind utility classes: rose = error, emerald = success).
    private static final By ALERT_BOX =
            By.cssSelector("[class*='rose'], [class*='emerald']");

    public UploadModal(WebDriver driver) {
        super(driver);
    }

    public UploadModal waitOpen() {
        waitVisible(MODAL_TITLE);
        return this;
    }

    /**
     * Sends the absolute file path to the hidden file input. The input does not
     * need to be visible for sendKeys to work on a file chooser.
     */
    public UploadModal chooseFile(String absolutePath) {
        WebElement input = wait.until(
                ExpectedConditions.presenceOfElementLocated(FILE_INPUT));
        input.sendKeys(absolutePath);
        return this;
    }

    public UploadModal clickUnggah() {
        click(UNGGAH_BUTTON);
        return this;
    }

    public boolean isUnggahEnabled() {
        List<WebElement> buttons = driver.findElements(UNGGAH_BUTTON);
        return !buttons.isEmpty() && buttons.get(0).isEnabled();
    }

    /**
     * Returns the text of the result alert (rose or emerald box). Waits for any
     * such box to appear; returns empty string if none shows within the timeout.
     */
    public String getResultAlertText() {
        try {
            WebElement alert = waitVisible(ALERT_BOX);
            return alert.getText().trim();
        } catch (org.openqa.selenium.TimeoutException e) {
            return "";
        }
    }
}
