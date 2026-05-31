# Test Strategy — Upload Jaringan (GeoJSON)

## 1. Tujuan
Memverifikasi fitur **Upload Jaringan Pipa / Manhole (GeoJSON)** pada aplikasi
BPAL PJK IPAL melalui pengujian fungsional otomatis berbasis BDD.

## 2. Ruang Lingkup
- **In-scope:** validasi tipe berkas, ukuran berkas (batas 50 MB), validasi isi
  GeoJSON (FeatureCollection), serta jalur sukses untuk dataset Jalur Pipa & Manhole.
- **Out-of-scope:** performa, keamanan, kompatibilitas lintas-browser (hanya Chrome).

## 3. Teknik Desain Test
- **Equivalence Partitioning (EP):** mempartisi input (tipe & isi berkas) menjadi
  kelas valid/invalid sehingga satu wakil tiap kelas mewakili seluruh kelas.
- **Boundary Value Analysis (BVA):** menguji nilai batas ukuran berkas (0 byte,
  >50 MB) di sekitar batas yang ditentukan sistem.

Detail lengkap test case ada di [test-cases.md](test-cases.md).

## 4. Arsitektur Otomasi
- **Bahasa/Build:** Java 23 + Maven.
- **Driver UI:** Selenium WebDriver (Chrome; Selenium Manager menyediakan driver).
- **BDD:** Cucumber 7 (Gherkin) + JUnit 5 (junit-platform).
- **Pola desain:** **Page Object Model (POM)** — seluruh locator terpusat di kelas
  `pages/`; step definitions hanya memanggil method Page Object.
- **Pelaporan:** `maven-cucumber-reporting` (Masterthought) menghasilkan laporan HTML
  otomatis dari `cucumber.json`; tangkapan layar dilampirkan otomatis saat skenario gagal.

### Struktur
```
src/test/java/org/example/
  pages/   POM (LoginPage, MainDashboardPage, IpalDashboardPage, DataJaringanPage, UploadModal, BasePage)
  steps/   UploadJaringanSteps (glue)
  hooks/   Hooks (setup/teardown + screenshot on failure)
  runners/ RunCucumberTest (JUnit5 @Suite)
  utils/   ConfigReader, DriverFactory, TestFileFactory
src/test/resources/features/upload_jaringan.feature
```

## 5. Data Uji
Berkas uji dibuat saat runtime oleh `TestFileFactory` (termasuk berkas >50 MB)
agar tidak perlu menyimpan berkas besar di repositori.

## 6. Kriteria Lulus
- Semua skenario **gagal/negatif** harus **ditolak** sistem (tidak ada pesan sukses).
- Semua skenario **berhasil/positif** harus memunculkan pesan `Successfully imported`.
- Jika sebuah skenario negatif justru berhasil di-upload → **bug** (lihat bug-report.md).

## 7. Cara Menjalankan
```powershell
# 1. Salin konfigurasi & isi kredensial
copy src\test\resources\config.properties.example src\test\resources\config.properties

# 2. Jalankan test + generate laporan HTML
mvn clean verify

# 3. Generate ringkasan bug otomatis dari hasil run
powershell -ExecutionPolicy Bypass -File scripts\generate-bug-summary.ps1
```
- Laporan HTML: `target/cucumber-html-reports/overview-features.html`
- Laporan Cucumber: `target/cucumber-reports/cucumber.html`
- Ringkasan bug otomatis: bagian "Auto-Generated Summary" di [bug-report.md](bug-report.md)

> Set `-Dheadless=false` untuk melihat browser berjalan:
> `mvn clean verify -Dheadless=false`
