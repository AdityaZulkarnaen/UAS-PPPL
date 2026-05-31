package org.example.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.pages.DataJaringanPage;
import org.example.pages.LoginPage;
import org.example.pages.UploadModal;
import org.example.utils.ConfigReader;
import org.example.utils.DriverFactory;
import org.example.utils.TestFileFactory;
import org.openqa.selenium.WebDriver;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Step definitions for the GeoJSON upload feature. Glue only delegates to the
 * Page Objects (POM) — no raw locators live here.
 */
public class UploadJaringanSteps {

    private static final String SUCCESS_MARKER = "Successfully imported";

    private final WebDriver driver = DriverFactory.getDriver();
    private DataJaringanPage dataJaringanPage;
    private UploadModal uploadModal;
    private String lastAlertText = "";

    @Given("admin sudah login dan berada di halaman Data Jaringan")
    public void adminLoggedInOnDataJaringan() {
        dataJaringanPage = new LoginPage(driver)
                .openMap()
                .clickAdminLogin()
                .selectEmailTab()
                .loginWithEmail(ConfigReader.email(), ConfigReader.password())
                .waitLoaded()
                .openModuleIpal()
                .waitLoaded()
                .openDataJaringan()
                .waitLoaded();
    }

    @When("admin memilih dataset {string}")
    public void adminMemilihDataset(String dataset) {
        dataJaringanPage.selectTab(dataset);
        uploadModal = dataJaringanPage.openUploadModal().waitOpen();
    }

    @And("admin memilih berkas dengan kasus {string}")
    public void adminMemilihBerkas(String caseKey) {
        String path = TestFileFactory.fileForCase(caseKey);
        uploadModal.chooseFile(path);
    }

    @And("admin menekan tombol Unggah")
    public void adminMenekanUnggah() {
        uploadModal.clickUnggah();
        lastAlertText = uploadModal.getResultAlertText();
    }

    @And("admin menekan tombol Unggah tanpa memilih berkas")
    public void adminMenekanUnggahTanpaBerkas() {
        // If the button is disabled with no file, treat that as "cannot submit".
        if (uploadModal.isUnggahEnabled()) {
            uploadModal.clickUnggah();
            lastAlertText = uploadModal.getResultAlertText();
        } else {
            lastAlertText = "";
        }
    }

    @Then("hasil unggah seharusnya {string}")
    public void hasilUnggahSeharusnya(String hasil) {
        boolean succeeded = lastAlertText.contains(SUCCESS_MARKER);
        if (hasil.equalsIgnoreCase("berhasil")) {
            assertTrue(succeeded,
                    "Mengharapkan unggahan BERHASIL, tetapi pesan: '" + lastAlertText + "'");
        } else {
            assertFalse(succeeded,
                    "Mengharapkan unggahan GAGAL (ditolak), tetapi unggahan berhasil: '"
                            + lastAlertText + "'");
        }
    }

    @Then("pesan hasil memuat teks {string}")
    public void pesanHasilMemuat(String fragment) {
        assertTrue(lastAlertText.contains(fragment),
                "Pesan hasil '" + lastAlertText + "' tidak memuat '" + fragment + "'");
    }

    @Then("unggahan seharusnya gagal")
    public void unggahanSeharusnyaGagal() {
        assertFalse(lastAlertText.contains(SUCCESS_MARKER),
                "Unggahan tanpa berkas seharusnya gagal, tetapi berhasil: '"
                        + lastAlertText + "'");
    }
}
