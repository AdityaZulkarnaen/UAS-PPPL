# Bug Reporting & Automate Generation of Report

Bug reporting dipisah menjadi **3 suite fitur**. Tiap report dihasilkan
**otomatis** dari hasil eksekusi Cucumber (`target/cucumber-reports/cucumber.json`)
oleh skrip [`scripts/generate-bug-report.ps1`](../scripts/generate-bug-report.ps1).

## Daftar Report per Suite

| # | Suite Fitur | Feature File | Report |
|---|---|---|---|
| 1 | **Upload Data** | `upload_jaringan.feature` | [bug-report-upload-data.md](bug-report-upload-data.md) |
| 2 | **Login & Visualisasi Data Jaringan** | `login.feature` + `visitor-map.feature` | [bug-report-login-visualisasi.md](bug-report-login-visualisasi) |
| 3 | **Kirim Aduan (Lapor Masalah Manhole)** | `lapor_masalah_manhole.feature` | [bug-report-kirim-aduan.md](bug-report-kirim-aduan.md) |

> Suite **Manajemen Aduan** (`aduan_management.feature`) adalah suite terpisah
> yang sudah memiliki laporannya sendiri, sehingga tidak diikutkan di sini.

## Isi Tiap Report
1. **Ringkasan Hasil Pengujian** — total test case, jumlah passed, failed, pass rate.
2. **Daftar Hasil (Expected vs Actual)** — tabel tiap test case beserta hasil
   yang diharapkan vs hasil aktual dan status PASS/FAIL.
3. **Daftar Bug Ditemukan** — hanya skenario gagal, memakai template bug report
   (Bug ID, Severity, Priority, Komponen, Langkah Reproduksi, Hasil Diharapkan,
   Hasil Aktual, Detail Error, Status).

## Cara Generate (Automate Generation of Report)

```powershell
# 1. (sekali) salin konfigurasi
copy src\test\resources\config.properties.example src\test\resources\config.properties

# 2. Jalankan test suite -> menghasilkan target/cucumber-reports/cucumber.json
mvn clean verify

# 3. Generate ke-3 report bug otomatis dari hasil run
powershell -ExecutionPolicy Bypass -File scripts\generate-bug-report.ps1
```

Suite mana yang dijalankan ditentukan oleh filter tag di
[`RunCucumberTest.java`](../src/test/java/org/example/runners/RunCucumberTest.java):

| Suite | Tag filter |
|---|---|
| Upload Data | `@upload` |
| Login & Visualisasi | `@login or @map` |
| Kirim Aduan | `@lapor_masalah` |
| Ketiganya sekaligus | `@upload or @login or @map or @lapor_masalah` |

> Generator hanya menimpa report untuk suite yang **ada** di `cucumber.json` pada
> run terkini. Suite yang tidak dijalankan akan **dilewati** (report lamanya
> dipertahankan), sehingga suite bisa dijalankan satu per satu tanpa menghapus
> report suite lain.

## Template Bug Report (acuan)

| Field | Isi |
|---|---|
| **Bug ID** | BUG-`<PREFIX>`-XX |
| **Judul** | Ringkas dan deskriptif (nama skenario) |
| **Severity** | Critical / Major / Minor / Trivial |
| **Priority** | High / Medium / Low |
| **Komponen** | Suite/halaman terkait |
| **Environment** | Browser, OS, URL |
| **Langkah Reproduksi** | Langkah Gherkin Given/When/Then |
| **Hasil Diharapkan** | Apa yang seharusnya terjadi |
| **Hasil Aktual** | Apa yang benar-benar terjadi + pesan error |
| **Status** | Open / In Progress / Closed |
