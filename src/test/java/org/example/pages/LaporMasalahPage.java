package org.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

/**
 * Page Object for /ipal/lapor-masalah — the public "Lapor Masalah Jaringan IPAL"
 * form. Reached either directly (ID IPAL / Titik Lokasi show "—", unfilled) or
 * via the manhole popup's "Lapor Masalah" link on the public map (ID IPAL /
 * Titik Lokasi are pre-filled from the selected asset).
 *
 * Captcha: a simple "a + b" arithmetic question rendered as plain text in
 * #captcha-question (e.g. "1 + 3"). The page resolves it automatically via
 * {@link #solveCaptcha()}.
 */
public class LaporMasalahPage extends BasePage {

    private static final By FIELD_ID_IPAL = By.id("field-id-ipal");
    private static final By FIELD_KOORDINAT = By.id("field-koordinat");
    private static final By FIELD_DESKRIPSI = By.id("field-deskripsi");
    private static final By DESKRIPSI_ERROR = By.id("deskripsi-error");

    private static final By FOTO_DROPZONE = By.id("foto-dropzone");
    private static final By FOTO_INPUT = By.id("foto-input"); // hidden <input type="file">
    private static final By FOTO_PREVIEW = By.id("foto-preview");

    private static final By CAPTCHA_QUESTION = By.id("captcha-question");
    private static final By CAPTCHA_ANSWER_INPUT = By.id("field-captcha-answer");
    private static final By CAPTCHA_REFRESH_BUTTON = By.id("captcha-refresh");
    private static final By CAPTCHA_ERROR_TEXT = By.id("captcha-error-text");

//    private static final By GLOBAL_ERROR = By.id("global-error");
    private static final By SUBMIT_BUTTON = By.id("submit-btn");

    private final By deskripsiError = By.id("deskripsi-error");
    private final By fotoError = By.id("foto-error");
    private final By globalError = By.id("global-error");

    /** Nomor tiket di-cache di sini karena DOM-nya hilang begitu app redirect ke /ipal/map. */
    private String lastTicketNumber = "";



    // Success state: #success-banner berisi teks "Laporan berhasil terkirim" + nomor
    // tiket. PENTING: aplikasi auto-redirect ke /ipal/map sekitar 2-3 detik setelah
    // banner ini tampil, jadi teksnya harus ditangkap SEGERA saat terdeteksi —
    // jangan ditunda dengan sleep/diagnostic tambahan sebelum membacanya.
    private static final By SUCCESS_MESSAGE = By.id("success-banner");

    public LaporMasalahPage(WebDriver driver) {
        super(driver);
    }
    /**
     * Cek visibility error dengan explicit wait pendek (3 detik), karena
     * beberapa pesan error dirender setelah delay singkat (mis. setelah
     * request validasi captcha/foto selesai), bukan langsung saat klik submit.
     */
    private boolean isErrorVisible(By locator, int timeoutSeconds) {
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(timeoutSeconds))
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));
            String text = driver.findElement(locator).getText().trim();
            return !text.isEmpty();
        } catch (TimeoutException e) {
            return false;
        }
    }

    /** Dump state untuk debugging — cetak status semua elemen pesan setelah submit. */
    public void dumpSubmissionState(String label) {
        System.out.println("=== [DIAG:" + label + "] URL: " + driver.getCurrentUrl());
        for (String id : new String[]{"success-banner", "global-error", "deskripsi-error", "foto-error"}) {
            try {
                WebElement el = driver.findElement(By.id(id));
                System.out.println("  #" + id + " displayed=" + el.isDisplayed()
                        + " style='" + el.getAttribute("style") + "' text='" + el.getText() + "'");
            } catch (Exception e) {
                System.out.println("  #" + id + " not found");
            }
        }
    }

    public LaporMasalahPage waitLoaded() {
        wait.until(ExpectedConditions.urlContains("/ipal/lapor-masalah"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(FIELD_DESKRIPSI));
        return this;
    }

    public boolean isLoaded() {
        return driver.getCurrentUrl().contains("/ipal/lapor-masalah");
    }

    // --- Auto-filled asset fields ---------------------------------------------

    public String getIdIpalValue() {
        return driver.findElement(FIELD_ID_IPAL).getAttribute("value");
    }

    public String getKoordinatValue() {
        return driver.findElement(FIELD_KOORDINAT).getAttribute("value");
    }

    /** True when ID IPAL has been pre-filled (i.e. not the placeholder "—"). */
    public boolean isAssetPreFilled() {
        String v = getIdIpalValue();
        return v != null && !v.trim().isEmpty() && !v.trim().equals("—");
    }

    // --- Deskripsi --------------------------------------------------------

    public LaporMasalahPage typeDeskripsi(String text) {
        WebElement ta = waitVisible(FIELD_DESKRIPSI);
        ta.clear();
        // Use JS to set value directly for very long strings — sendKeys can be slow
        // and is prone to dropping keystrokes on large inputs in some browsers.
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1]; "
                        + "arguments[0].dispatchEvent(new Event('input', {bubbles:true}));",
                ta, text);
        return this;
    }

    public String getDeskripsiErrorText() {
        try {
            return driver.findElement(deskripsiError).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public String getFotoErrorText() {
        try {
            return driver.findElement(fotoError).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public String getDeskripsiValue() {
        return driver.findElement(FIELD_DESKRIPSI).getAttribute("value");
    }

    public boolean isDeskripsiErrorDisplayed() {
        return isErrorVisible(deskripsiError, 5);
    }


    // --- Foto dokumentasi ---------------------------------------------------

    /**
     * Sends the absolute file path to the hidden #foto-input. The dropzone div
     * only forwards clicks to this input; sendKeys works directly on the file
     * input without needing it to be visible.
     */
    public LaporMasalahPage chooseFoto(String absolutePath) {
        WebElement input = driver.findElement(FOTO_INPUT);
        input.sendKeys(absolutePath);
        return this;
    }

    public boolean isFotoErrorDisplayed() {
        return isErrorVisible(fotoError, 5);
    }


    public boolean isFotoPreviewDisplayed() {
        try {
            WebElement preview = driver.findElement(FOTO_PREVIEW);
            return preview.findElements(By.xpath("./*")).size() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    // --- Captcha -------------------------------------------------------------

    /** Returns the raw captcha question text, e.g. "1 + 3". */
    public String getCaptchaQuestionText() {
        return waitVisible(CAPTCHA_QUESTION).getText().trim();
    }

    /**
     * Parses the "a + b" (or "a - b" / "a x b") captcha question and computes
     * the answer, then types it into the answer field. Supports +, -, x/*.
     */
    public LaporMasalahPage solveCaptcha() {
        String question = getCaptchaQuestionText(); // e.g. "1 + 3"
        int answer = evaluateCaptcha(question);
        WebElement answerInput = waitVisible(CAPTCHA_ANSWER_INPUT);
        answerInput.clear();
        answerInput.sendKeys(String.valueOf(answer));
        return this;
    }

    /** Types a deliberately wrong captcha answer (for negative-path tests). */
    public LaporMasalahPage typeWrongCaptchaAnswer() {
        String question = getCaptchaQuestionText();
        int correct = evaluateCaptcha(question);
        int wrong = correct + 1; // far enough off to never collide
        WebElement answerInput = waitVisible(CAPTCHA_ANSWER_INPUT);
        answerInput.clear();
        answerInput.sendKeys(String.valueOf(wrong));
        return this;
    }

    public LaporMasalahPage clickGantiCaptcha() {
        click(CAPTCHA_REFRESH_BUTTON);
        return this;
    }

    public boolean isCaptchaErrorDisplayed() {
        return isDisplayed(CAPTCHA_ERROR_TEXT) && !getCaptchaErrorText().isEmpty();
    }

    public String getCaptchaErrorText() {
        try {
            return driver.findElement(CAPTCHA_ERROR_TEXT).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public String waitForCaptchaOrGlobalErrorText() {
        try {
            return new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(d -> {
                        if (isDisplayed(CAPTCHA_ERROR_TEXT)) {
                            String text = getCaptchaErrorText();
                            if (!text.isEmpty()) {
                                return text;
                            }
                        }
                        if (isDisplayed(globalError)) {
                            String text = getGlobalErrorText();
                            if (!text.isEmpty()) {
                                return text;
                            }
                        }
                        return null;
                    });
        } catch (TimeoutException e) {
            return "";
        }
    }

    private static int evaluateCaptcha(String question) {
        // Expected formats: "a + b", "a - b", "a x b" / "a * b"
        String normalized = question.replace("x", "*").replace("X", "*");
        String[] parts = normalized.trim().split("\\s+");
        if (parts.length != 3) {
            throw new IllegalStateException("Unexpected captcha format: '" + question + "'");
        }
        int a = Integer.parseInt(parts[0]);
        int b = Integer.parseInt(parts[2]);
        return switch (parts[1]) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            default -> throw new IllegalStateException("Unsupported captcha operator: " + parts[1]);
        };
    }

    // --- Submission ------------------------------------------------------

    public LaporMasalahPage clickKirimLaporan() {
        click(SUBMIT_BUTTON);
        return this;
    }

    public boolean isSubmissionSuccessful() {
        try {
            WebElement banner = wait.until(ExpectedConditions.visibilityOfElementLocated(SUCCESS_MESSAGE));
            // Tangkap teksnya SEKARANG — beberapa detik lagi app redirect ke
            // /ipal/map dan elemen ini lenyap dari DOM.
            lastTicketNumber = extractTicketNumber(banner.getText());
            return true;
        } catch (TimeoutException e) {
            try {
                return wait.until(ExpectedConditions.urlContains("/ipal/map"));
            } catch (TimeoutException ignored) {
                return false;
            }
        }
    }

    private static String extractTicketNumber(String bannerText) {
        if (bannerText == null) return "";
        for (String line : bannerText.split("\\R")) {
            if (line.toLowerCase().contains("nomor tiket")) {
                return line.replaceAll("(?i).*nomor tiket\\s*:?\\s*", "").trim();
            }
        }
        return "";
    }

    private static String truncateText(String s, int max) {
        if (s == null) return "";
        s = s.replace("\n", " ").trim();
        return s.length() > max ? s.substring(0, max) + "..." : s;
    }

    public boolean isGlobalErrorDisplayed() {
        return isErrorVisible(globalError, 5);
    }

    public String getGlobalErrorText() {
        try {
            return driver.findElement(globalError).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    /** Best-effort extraction of the ticket number shown after a successful submission. */
    /** Nomor tiket dari pengiriman terakhir, di-cache saat {@link #isSubmissionSuccessful()} dipanggil. */
    public String getTicketNumber() {
        return lastTicketNumber;
    }
}