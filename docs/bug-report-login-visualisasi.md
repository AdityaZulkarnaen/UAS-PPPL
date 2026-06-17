# Bug Report - Suite Login & Visualisasi Data Jaringan

> **Dibuat otomatis** oleh `scripts/generate-bug-report.ps1` - jangan diedit manual.
> Sumber data: `target/cucumber-reports/cucumber.json` | Waktu generate: 2026-06-17 21:39:23

| Info | Nilai |
|---|---|
| **Aplikasi** | BPAL PJK IPAL - https://bpalpjk.madanateknologi.web.id |
| **Komponen** | Login BPALPJK + Visualisasi Peta Jaringan/Manhole (/ipal/map) |
| **Environment** | Chrome (Selenium headless), Windows 11 |

## 1. Ringkasan Hasil Pengujian

| Metrik | Nilai |
|---|---|
| Total Test Case | 17 |
| Passed | 16 |
| Failed | 1 |
| Pass Rate | 94.1% |

## 2. Daftar Hasil Pengujian (Expected vs Actual)

| No | Test Case (Skenario) | Hasil Diharapkan (Expected) | Hasil Aktual (Actual) | Status |
|---|---|---|---|---|
| 1 | Nomor WhatsApp otomatis diawali 62 saat user mengetik angka selain 8 | Then Field nomor WhatsApp menampilkan "625" | Sesuai harapan - seluruh langkah lulus. | PASS |
| 2 | Nomor WhatsApp tetap valid saat user mengetik angka 8 | Then Field nomor WhatsApp menampilkan "628" | Sesuai harapan - seluruh langkah lulus. | PASS |
| 3 | User mengirim OTP tanpa mengisi nomor WhatsApp | Then Sistem menampilkan validasi nomor WhatsApp | Sesuai harapan - seluruh langkah lulus. | PASS |
| 4 | User membuka form login email | Then Form login email ditampilkan | Sesuai harapan - seluruh langkah lulus. | PASS |
| 5 | User berhasil login menggunakan email valid | Then User berhasil masuk ke dashboard BPALPJK | Sesuai harapan - seluruh langkah lulus. | PASS |
| 6 | Login email tanpa mengisi data | Then Sistem menampilkan validasi email dan password | Sesuai harapan - seluruh langkah lulus. | PASS |
| 7 | Login email menggunakan kredensial tidak valid | Then Sistem menampilkan pesan login gagal | Sesuai harapan - seluruh langkah lulus. | PASS |
| 8 | Login email menggunakan kredensial tidak valid | Then Sistem menampilkan pesan login gagal | Sesuai harapan - seluruh langkah lulus. | PASS |
| 9 | User membuka halaman lupa kata sandi | Then User diarahkan ke halaman lupa kata sandi | Sesuai harapan - seluruh langkah lulus. | PASS |
| 10 | Halaman visualisasi peta berhasil ditampilkan | Then Peta jaringan ditampilkan; And Filter jaringan ditampilkan; And Search bar ditampilkan | Sesuai harapan - seluruh langkah lulus. | PASS |
| 11 | User mengubah filter status Baik | Then Filter status Baik berhasil berubah | Sesuai harapan - seluruh langkah lulus. | PASS |
| 12 | User mengubah filter status Perbaikan | Then Filter status Perbaikan berhasil berubah | Sesuai harapan - seluruh langkah lulus. | PASS |
| 13 | User mengubah filter status Rusak | Then Filter status Rusak berhasil berubah | Sesuai harapan - seluruh langkah lulus. | PASS |
| 14 | User membuka filter fungsi pipa | Then Dropdown fungsi pipa tersedia | Sesuai harapan - seluruh langkah lulus. | PASS |
| 15 | User mencari data yang tidak tersedia | Then Sistem menampilkan pesan data tidak ditemukan | Sesuai harapan - seluruh langkah lulus. | PASS |
| 16 | User mencari wilayah yang tersedia | Then Detail manhole "WH1" ditampilkan | Gagal di langkah 'Then Detail manhole "WH1" ditampilkan' -> org.opentest4j.AssertionFailedError: expected: <true> but was: <false> | FAIL |
| 17 | User membuka detail manhole melalui kode manhole | Then Popup detail manhole ditampilkan | Sesuai harapan - seluruh langkah lulus. | PASS |

## 3. Daftar Bug Ditemukan

Ditemukan **1** skenario gagal - kandidat bug:

| Bug ID | Test Case | Ringkasan Hasil Aktual |
|---|---|---|
| BUG-LGV-01 | User mencari wilayah yang tersedia | Gagal di langkah 'Then Detail manhole "WH1" ditampilkan' -> org.opentest4j.AssertionFailedError: expected: <true> but was: <false> |

### Detail Bug

#### BUG-LGV-01 - User mencari wilayah yang tersedia

| Field | Isi |
|---|---|
| **Bug ID** | BUG-LGV-01 |
| **Judul** | User mencari wilayah yang tersedia |
| **Severity** | _perlu ditinjau (Critical/Major/Minor/Trivial)_ |
| **Priority** | _perlu ditinjau (High/Medium/Low)_ |
| **Komponen** | Login BPALPJK + Visualisasi Peta Jaringan/Manhole (/ipal/map) |
| **Environment** | Chrome (Selenium headless), Windows 11, https://bpalpjk.madanateknologi.web.id |
| **Hasil Diharapkan** | Then Detail manhole "WH1" ditampilkan |
| **Hasil Aktual** | Gagal di langkah 'Then Detail manhole "WH1" ditampilkan' -> org.opentest4j.AssertionFailedError: expected: <true> but was: <false> |
| **Status** | Open |

**Langkah Reproduksi:**
```gherkin
1. When User mengisi pencarian peta dengan "Gondokusuman"
2. And User menekan Enter pada pencarian peta
3. Then Detail manhole "WH1" ditampilkan
```

**Detail Error:**
```
org.opentest4j.AssertionFailedError: expected: <true> but was: <false>
	at org.junit.jupiter.api.AssertionFailureBuilder.build(AssertionFailureBuilder.java:151)
	at org.junit.jupiter.api.AssertionFailureBuilder.buildAndThrow(AssertionFailureBuilder.java:132)
	at org.junit.jupiter.api.AssertTrue.failNotTrue(AssertTrue.java:63)
	at org.junit.jupiter.api.AssertTrue.assertTrue(AssertTrue.java:36)
	at org.junit.jupiter.api.AssertTrue.assertTrue(AssertTrue.java:31)
	at org.junit.jupiter.api.Assertions.assertTrue(Assertions.java:183)
	at org.example.steps.VisualisasiPetaSteps.detailManholeDitampilkan(VisualisasiPetaSteps.java:95)
	at ✽.Detail manhole "WH1" ditampilkan(classpath:features/visitor-map.feature:42)
```

> Tinjau tiap entri: bila merupakan cacat aplikasi (bukan masalah environment/
> locator), tetapkan Severity & Priority lalu laporkan resmi ke tim pengembang.

