# UAS-PPPL - Otomasi Pengujian BPAL PJK IPAL

Proyek pengujian otomatis berbasis BDD untuk aplikasi **BPAL PJK IPAL**, sistem informasi pengelolaan jaringan air limbah milik BPALPJK.

- **Aplikasi yang diuji:** [https://bpalpjk.madanateknologi.web.id](https://bpalpjk.madanateknologi.web.id)
- **Mata kuliah:** Praktikum Pengujian Perangkat Lunak
- **Teknik desain test:** Equivalence Partitioning (EP) dan Boundary Value Analysis (BVA)

---

## Tech Stack

| Komponen | Teknologi |
|---|---|
| Bahasa | Java 21/23 |
| Build tool | Maven 3 |
| Browser automation | Selenium WebDriver 4.27.0 (Chrome headless) |
| BDD framework | Cucumber 7.20.1 (Gherkin) |
| Test runner | JUnit 5 (junit-platform-suite 1.11.3) |
| Reporting | maven-cucumber-reporting 5.7.7 (Masterthought HTML) |
| Design pattern | Page Object Model (POM) |

---

## Pembagian Tugas

| Nama | Suite yang Dikerjakan | Feature File | Jumlah TC |
|---|---|---|---|
| Lilis | Login BPALPJK | `login.feature` | 9 |
| Lilis | Visualisasi Peta | `visitor-map.feature` | 8 |
| Adit | Upload Dataset Jaringan | `upload_jaringan.feature` | 17 |
| Taqi | Kirim Aduan (Lapor Masalah Manhole) | `lapor_masalah_manhole.feature` | 12 |
| Azril | Manajemen Aduan IPAL | `aduan_management.feature` | 15 |

---

## Test Suites

### 1. Login BPALPJK (Lilis)

**Fitur:** Halaman login aplikasi di `/login`

**Halaman yang diuji:** Login WhatsApp (OTP), Login Email, navigasi lupa kata sandi

**Teknik:** EP (7 kelas ekuivalen) + BVA (batas format nomor WhatsApp dan kolom email/password)

**Skenario yang dicakup:**

| ID | Deskripsi | Teknik |
|---|---|---|
| TC-LOGIN-01 | Nomor WhatsApp otomatis diawali `62` saat input selain angka 8 | EP1 + BVA |
| TC-LOGIN-02 | Nomor WhatsApp tetap valid saat input angka 8 | EP2 + BVA |
| TC-LOGIN-03 | OTP tidak dapat dikirim tanpa nomor WhatsApp | EP3 + BVA |
| TC-LOGIN-04 | Membuka form login email | EP8 |
| TC-LOGIN-05 | Login berhasil dengan email dan password valid | EP4 + BVA |
| TC-LOGIN-06 | Login gagal saat email dan password kosong | EP7 + BVA |
| TC-LOGIN-07 | Login gagal dengan email salah | EP5 |
| TC-LOGIN-08 | Login gagal dengan password salah | EP6 |
| TC-LOGIN-09 | Membuka halaman lupa kata sandi | EP9 |

**Hasil:** 9/9 PASS (100%)

---

### 2. Visualisasi Peta / Visitor Map (Lilis)

**Fitur:** Peta jaringan publik di `/ipal/map` -- menampilkan jalur pipa dan manhole dengan filter dan pencarian

**Teknik:** EP (4 kelas ekuivalen) + BVA (filter status aset dan pencarian area/kode)

**Skenario yang dicakup:**

| ID | Deskripsi | Teknik |
|---|---|---|
| TC-MAP-01 | Peta, panel filter, dan search bar tampil (smoke test) | EP |
| TC-MAP-02 | Filter status "Baik" mengubah tampilan peta | EP |
| TC-MAP-03 | Filter status "Perbaikan" mengubah tampilan peta | EP |
| TC-MAP-04 | Filter status "Rusak" mengubah tampilan peta | EP |
| TC-MAP-05 | Dropdown fungsi pipa tersedia | EP |
| TC-MAP-06 | Pencarian data yang tidak ada menghasilkan kondisi kosong | EP (negatif) |
| TC-MAP-07 | Pencarian area "Gondokusuman" menampilkan popup manhole | EP (positif) |
| TC-MAP-08 | Pencarian kode "WH1" menampilkan popup manhole | BVA |

**Hasil:** 8/8 PASS (100%)

---

### 3. Upload Dataset Jaringan (Taqi)

**Fitur:** Unggah file GeoJSON untuk data jaringan pipa dan manhole di `/ipal/upload`

**Teknik:** EP (7 kelas ekuivalen tipe file) + BVA (batas ukuran file 50 MB)

**Skenario yang dicakup:**

| Kelompok | Deskripsi | Teknik |
|---|---|---|
| Valid GeoJSON (.geojson/.json) | Upload Jalur Pipa dan Manhole dengan format valid | EP positif |
| Tipe file tidak valid | Upload .txt, .csv, .png ditolak | EP negatif |
| Konten tidak valid | JSON bukan FeatureCollection, JSON malformed, file kosong | EP negatif |
| BVA ukuran file | File tepat di batas (valid), file >50 MB ditolak | BVA |
| Upload tanpa file | Upload tanpa memilih file ditolak | BVA |
| Validasi pesan error | Pesan validasi non-FeatureCollection sesuai | EP |

Total: 17 skenario

**Hasil:** 17/17 PASS (100%)

---

### 4. Kirim Aduan / Lapor Masalah Manhole (Adit)

**Fitur:** Form pengaduan publik untuk aset manhole bermasalah di `/ipal/map`

**Teknik:** EP (5 kelas ekuivalen: navigasi, deskripsi, foto, captcha, E2E) + BVA (batas 5000 karakter deskripsi)

**Skenario yang dicakup:**

| ID | Deskripsi | Teknik |
|---|---|---|
| TC-M01 | Membuka form Lapor Masalah dari popup manhole | EP (navigasi) |
| TC-M02 | Laporan berhasil dikirim dengan data valid | EP (positif) |
| TC-M03 | Laporan gagal tanpa deskripsi | EP (negatif) |
| TC-M04 | BVA deskripsi 1 karakter -- berhasil dikirim | BVA |
| TC-M05 | BVA deskripsi 5000 karakter -- berhasil dikirim | BVA |
| TC-M06 | BVA deskripsi 5001 karakter -- sistem menolak | BVA |
| TC-M07 | Laporan gagal tanpa foto | EP (negatif) |
| TC-M08 | Upload foto PNG valid -- berhasil dikirim | EP (positif) |
| TC-M09 | Upload foto .pdf ditolak | EP (negatif) |
| TC-M10 | Visitor mengirim laporan dengan jawaban captcha yang salah | EP (negatif) |
| TC-M11 | Ganti soal captcha lalu jawab benar -- berhasil dikirim | EP (positif) |
| TC-M12 | E2E: laporan muncul dan dapat dibuka admin di daftar aduan | E2E |

**Hasil:** 9/12 PASS (75%) -- 3 skenario gagal akibat rate-limit server

---

### 5. Manajemen Aduan IPAL (Azril)

**Fitur:** Halaman admin untuk mengelola aduan masuk di `/ipal/aduan` dan `/ipal/aduan/{id}`

**Teknik:** EP (5 kelas ekuivalen: list, filter, search, detail, workflow) + BVA (batas 5000 karakter catatan progress)

**Skenario yang dicakup:**

| ID | Deskripsi | Teknik |
|---|---|---|
| TC-01 | Admin melihat daftar aduan | EP |
| TC-02 - TC-04 | Filter aduan berdasarkan status (masuk/proses/selesai) | EP |
| TC-05 | Pencarian dengan keyword valid | EP |
| TC-06 | Pencarian dengan keyword kosong menampilkan semua | EP |
| TC-07 | Pencarian keyword tidak ada menampilkan kondisi kosong | EP |
| TC-08 | Membuka halaman detail aduan | EP |
| TC-09 | Admin menerima aduan -- status berubah menjadi "proses" | EP (workflow) |
| TC-10 | Admin menolak aduan -- status berubah menjadi "ditolak" | EP (workflow) |
| TC-11 | Admin memulai perbaikan -- status berubah menjadi "perbaikan" | EP (workflow) |
| TC-12 | BVA catatan progress 1 karakter -- berhasil disimpan | BVA |
| TC-13 | BVA catatan progress 5000 karakter -- berhasil disimpan | BVA |
| TC-14 | BVA catatan progress 5001 karakter -- gagal disimpan | BVA |
| TC-15 | Admin menandai selesai -- status berubah menjadi "selesai" | EP (workflow) |

**Hasil:** 15/15 PASS (100%)

---

## Ringkasan Hasil Keseluruhan

| Suite | Penanggung Jawab | Total TC | Passed | Failed | Pass Rate |
|---|---|---|---|---|---|
| Login BPALPJK | Lilis | 9 | 9 | 0 | 100% |
| Visualisasi Peta | Lilis | 8 | 8 | 0 | 100% |
| Upload Dataset Jaringan | Taqi | 17 | 17 | 0 | 100% |
| Kirim Aduan (Lapor Masalah) | Adit | 12 | 9 | 3 | 75% |
| Manajemen Aduan IPAL | Azril | 15 | 15 | 0 | 100% |
| **Total** | | **61** | **58** | **3** | **~95%** |

---

## Bug Report Ringkasan

Ditemukan **4 defek** selama pengujian:

| Bug ID | Suite | Judul | Severity | Status |
|---|---|---|---|---|
| BUG-ADU-01 | Kirim Aduan | Server mengembalikan pesan rate-limit saat captcha salah, bukan pesan validasi captcha | Perlu ditinjau | Open |
| BUG-ADU-02 | Kirim Aduan | Server mengembalikan rate-limit setelah refresh captcha, laporan tidak terkirim | Perlu ditinjau | Open |
| BUG-ADU-03 | Kirim Aduan | Pengujian E2E gagal karena server rate-limit sebelum laporan tersimpan | Perlu ditinjau | Open |
| BUG-01 | Manajemen Aduan | Aplikasi tidak menolak catatan progress yang melebihi 5000 karakter | Medium | Open |

Catatan BUG-ADU-01 s.d. BUG-ADU-03: kegagalan disebabkan oleh mekanisme rate-limiting server (`"Terlalu banyak pengiriman. Silakan coba lagi dalam beberapa menit."`) yang terpicu karena test suite mengirim banyak laporan dalam satu sesi. Bukan cacat logika inti fitur pengiriman, namun perlu ditangani di level konfigurasi pengujian.

Detail lengkap bug tersedia di direktori `docs/Bug Report/`.

---

## Cara Menjalankan

### Prasyarat

- Java 21 atau lebih baru
- Maven 3.x
- Google Chrome (Selenium Manager akan mengunduh chromedriver secara otomatis)
- PowerShell (untuk Windows, agar bug report otomatis di-generate)

### Konfigurasi

Salin file konfigurasi dan isi kredensial:

```
cp src/test/resources/config.properties.example src/test/resources/config.properties
```

Edit `config.properties`:

```
baseUrl=https://bpalpjk.madanateknologi.web.id
email=admin@gmail.com
password=password
```

### Menjalankan Seluruh Test Suite

```
mvn clean verify
```

**Apa yang terjadi otomatis:**

1. Maven compile Java code
2. Test runner (RunCucumberTest) jalankan semua scenario
3. Hasil test disimpan ke `target/cucumber-reports/cucumber.json`
4. Masterthought plugin generate HTML report
5. PowerShell script (`scripts/generate-bug-report.ps1`) otomatis jalankan untuk generate bug report markdown

### Menjalankan Suite Tertentu

```
# Login dan Visualisasi Peta
mvn clean verify -Dcucumber.filter.tags="@login or @map"

# Upload Dataset
mvn clean verify -Dcucumber.filter.tags="@upload"

# Kirim Aduan
mvn clean verify -Dcucumber.filter.tags="@lapor_masalah"

# Manajemen Aduan
mvn clean verify -Dcucumber.filter.tags="@aduan"
```

### Melihat Laporan

**HTML Report (Detail - semua step):**

Setelah test selesai, laporan HTML tersedia di:

```
target/cucumber-html-reports/cucumber-html-reports/overview-features.html
```

Buka file ini di browser untuk melihat:
- Semua 15+ scenario dengan status (PASSED/FAILED)
- Setiap step (Given/When/And/Then) dengan status
- Tag grouping
- Failures (jika ada)

**Bug Report (Ringkasan - bugs saja):**

Bug report per suite otomatis di-generate dan tersedia di:

```
docs/Bug Report/bug-report-manajemen-aduan.md
docs/Bug Report/bug-report-kirim-aduan.md
docs/Bug Report/bug-report-upload-data.md
docs/Bug Report/bug-report-login-visualisasi.md
```

File ini dibuat otomatis oleh script `scripts/generate-bug-report.ps1` sebagai bagian dari `mvn verify`. Berisi:
- Total test case, passed, failed, pass rate
- Daftar bugs ditemukan dengan severity, priority, dan langkah reproduksi
