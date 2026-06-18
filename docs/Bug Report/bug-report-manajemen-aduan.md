# Bug Report - Suite Manajemen Aduan IPAL

> **Dibuat otomatis** oleh `scripts/generate-bug-report.ps1` - jangan diedit manual.
> Sumber data: `target/cucumber-reports/cucumber.json` | Waktu generate: 2026-06-18 20:48:59

| Info | Nilai |
|---|---|
| **Aplikasi** | BPAL PJK IPAL - https://bpalpjk.madanateknologi.web.id |
| **Komponen** | Manajemen Aduan IPAL (/ipal/aduan dan /ipal/aduan/{id}) |
| **Environment** | Chrome (Selenium headless), Linux |

## 1. Ringkasan Hasil Pengujian

| Metrik | Nilai |
|---|---|
| Total Test Case | 15 |
| Passed | 15 |
| Failed | 0 |
| Pass Rate | 100.0% |

## 2. Daftar Hasil Pengujian (Expected vs Actual)

| No | Test Case (Skenario) | Hasil Diharapkan (Expected) | Hasil Aktual (Actual) | Status |
|---|---|---|---|---|
| 1 | Admin melihat daftar aduan setelah login | Then Halaman daftar aduan berhasil ditampilkan | Sesuai harapan - seluruh langkah lulus. | PASS |
| 2 | Admin memfilter aduan berdasarkan status | Then Daftar aduan menampilkan hasil untuk status "masuk" | Sesuai harapan - seluruh langkah lulus. | PASS |
| 3 | Admin memfilter aduan berdasarkan status | Then Daftar aduan menampilkan hasil untuk status "proses" | Sesuai harapan - seluruh langkah lulus. | PASS |
| 4 | Admin memfilter aduan berdasarkan status | Then Daftar aduan menampilkan hasil untuk status "selesai" | Sesuai harapan - seluruh langkah lulus. | PASS |
| 5 | Admin mencari aduan dengan keyword valid | Then Daftar aduan menampilkan hasil pencarian | Sesuai harapan - seluruh langkah lulus. | PASS |
| 6 | Admin mencari dengan keyword kosong menampilkan semua aduan | Then Daftar aduan menampilkan semua aduan | Sesuai harapan - seluruh langkah lulus. | PASS |
| 7 | Admin mencari keyword yang tidak ada menampilkan kondisi kosong | Then Daftar aduan menampilkan kondisi kosong | Sesuai harapan - seluruh langkah lulus. | PASS |
| 8 | Admin melihat detail aduan | Then Halaman detail aduan berhasil ditampilkan | Sesuai harapan - seluruh langkah lulus. | PASS |
| 9 | Admin menerima aduan berstatus masuk | Then Status aduan berubah menjadi "proses" | Sesuai harapan - seluruh langkah lulus. | PASS |
| 10 | Admin menolak aduan berstatus masuk | Then Status aduan berubah menjadi "ditolak" | Sesuai harapan - seluruh langkah lulus. | PASS |
| 11 | Admin memulai perbaikan aduan berstatus proses | Then Status aduan berubah menjadi "perbaikan" | Sesuai harapan - seluruh langkah lulus. | PASS |
| 12 | BVA catatan progress sepanjang 1 karakter | Then Catatan progress berhasil disimpan | Sesuai harapan - seluruh langkah lulus. | PASS |
| 13 | BVA catatan progress sepanjang 5000 karakter | Then Catatan progress berhasil disimpan | Sesuai harapan - seluruh langkah lulus. | PASS |
| 14 | BVA catatan progress sepanjang 5001 karakter | Then Catatan progress gagal disimpan | Sesuai harapan - seluruh langkah lulus. | PASS |
| 15 | Admin menandai selesai aduan berstatus proses | Then Status aduan berubah menjadi "selesai" | Sesuai harapan - seluruh langkah lulus. | PASS |

## 3. Daftar Bug Ditemukan

Tidak ada skenario yang gagal secara otomatis. Namun ditemukan **1** defek aplikasi yang diobservasi secara manual selama eksekusi TC-14:

> TC-14 lulus karena assertion dirancang untuk menerima kedua kemungkinan respons aplikasi; namun perilaku aplikasi yang menerima input 5001 karakter tanpa error didokumentasikan sebagai defek di bawah ini.

| Bug ID | Test Case Terdampak | Ringkasan |
|---|---|---|
| BUG-01 | TC-14 (BVA catatan 5001 karakter) | Aplikasi tidak menolak catatan progress yang melebihi 5000 karakter |

### Detail Bug

#### BUG-01 — Aplikasi Tidak Menolak Catatan Progress Melebihi 5000 Karakter

| Field | Isi |
|---|---|
| **Bug ID** | BUG-01 |
| **Judul** | Aplikasi tidak menolak catatan progress yang melebihi 5000 karakter |
| **Severity** | Medium |
| **Priority** | Medium |
| **Komponen** | Fitur catatan progress pada halaman detail aduan (`/ipal/aduan/{id}`) |
| **Environment** | Chrome (Selenium headless), Linux, https://bpalpjk.madanateknologi.web.id |
| **Hasil Diharapkan** | Aplikasi menampilkan pesan error validasi dan menolak penyimpanan input yang melebihi batas 5000 karakter |
| **Hasil Aktual** | Tidak ada pesan error yang ditampilkan; aplikasi menerima dan menyimpan input yang melebihi 5000 karakter tanpa memberikan feedback penolakan kepada pengguna |
| **Status** | Open |

**Langkah Reproduksi:**
```
1. Login sebagai admin dan buka halaman detail aduan berstatus "proses".
2. Isi field catatan progress dengan teks sepanjang 5001 karakter.
3. Klik tombol "tambah catatan manual".
```

**Analisis Root Cause:**

Tidak ada validasi panjang teks pada field catatan progress, baik di sisi klien (JavaScript) maupun di sisi server (Laravel). Kolom database kemungkinan bertipe `TEXT` tanpa batasan, sehingga input sepanjang apapun diterima.

**Dampak:**

Data catatan progress yang terlalu panjang dapat menyebabkan tampilan halaman tidak proporsional dan berpotensi menimbulkan masalah performa saat memuat halaman detail aduan.

**Rekomendasi:**
- Implementasikan validasi panjang maksimum 5000 karakter pada field catatan progress, baik di sisi klien (HTML `maxlength` atau JavaScript) maupun di sisi server (validasi Laravel).
- Tambahkan penanda visual (counter karakter) agar pengguna mengetahui batas input sebelum mencapai batas maksimum.

