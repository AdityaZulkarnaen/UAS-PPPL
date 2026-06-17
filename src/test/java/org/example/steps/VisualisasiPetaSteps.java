package org.example.steps;

import io.cucumber.java.en.*;
import org.example.pages.VisualisasiPetaPage;
import org.example.utils.DriverFactory;
import org.junit.jupiter.api.Assertions;

public class VisualisasiPetaSteps {

    VisualisasiPetaPage mapPage;

    @Given("User membuka halaman Public Map IPAL")
    public void userMembukaHalamanPublicMapIPAL() {
        mapPage = new VisualisasiPetaPage(DriverFactory.getDriver());
        mapPage.openPublicMap();
    }

    @Then("Peta jaringan ditampilkan")
    public void petaJaringanDitampilkan() {
        Assertions.assertTrue(mapPage.isMapDisplayed());
    }

    @Then("Filter jaringan ditampilkan")
    public void filterJaringanDitampilkan() {
        Assertions.assertTrue(mapPage.isFilterDisplayed());
    }

    @Then("Search bar ditampilkan")
    public void searchBarDitampilkan() {
        Assertions.assertTrue(mapPage.isSearchBarDisplayed());
    }

    @When("User mengklik filter status Baik")
    public void userMengklikFilterStatusBaik() {
        mapPage.clickFilterBaik();
    }

    @Then("Filter status Baik berhasil berubah")
    public void filterStatusBaikBerhasilBerubah() {
        Assertions.assertTrue(mapPage.isBaikFilterChanged());
    }

    @When("User mengklik filter status Perbaikan")
    public void userMengklikFilterStatusPerbaikan() {
        mapPage.clickFilterPerbaikan();
    }

    @Then("Filter status Perbaikan berhasil berubah")
    public void filterStatusPerbaikanBerhasilBerubah() {
        Assertions.assertTrue(mapPage.isPerbaikanFilterChanged());
    }

    @When("User mengklik filter status Rusak")
    public void userMengklikFilterStatusRusak() {
        mapPage.clickFilterRusak();
    }

    @Then("Filter status Rusak berhasil berubah")
    public void filterStatusRusakBerhasilBerubah() {
        Assertions.assertTrue(mapPage.isRusakFilterChanged());
    }

    @When("User membuka dropdown fungsi pipa")
    public void userMembukaDropdownFungsiPipa() {
        mapPage.openDropdownFungsiPipa();
    }

    @Then("Dropdown fungsi pipa tersedia")
    public void dropdownFungsiPipaTersedia() {
        Assertions.assertTrue(mapPage.isDropdownFungsiPipaDisplayed());
    }

    @When("User mengisi pencarian peta dengan {string}")
    public void userMengisiPencarianPetaDengan(String keyword) {
        mapPage.inputSearch(keyword);
    }

    @When("User mengklik tombol Cari Data pada peta")
    public void userMengklikTombolCariDataPadaPeta() {
        mapPage.clickSearchButton();
    }

    @When("User menekan Enter pada pencarian peta")
    public void userMenekanEnterPadaPencarianPeta() {
        mapPage.pressEnterOnSearch();
    }

    @Then("Sistem menampilkan pesan data tidak ditemukan")
    public void sistemMenampilkanPesanDataTidakDitemukan() {
        Assertions.assertTrue(mapPage.isNoResultMessageDisplayed());
    }

    @Then("Popup detail manhole ditampilkan")
    public void popupDetailManholeDitampilkan() {
        Assertions.assertTrue(mapPage.isPopupDetailManholeDisplayed());
    }
}