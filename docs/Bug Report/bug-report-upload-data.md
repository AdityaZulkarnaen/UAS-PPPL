# Bug Report - Suite Upload Data Jaringan (GeoJSON)

> **Dibuat otomatis** oleh `scripts/generate-bug-report.ps1` - jangan diedit manual.
> Sumber data: `target/cucumber-reports/cucumber.json` | Waktu generate: 2026-06-17 21:39:23

| Info | Nilai |
|---|---|
| **Aplikasi** | BPAL PJK IPAL - https://bpalpjk.madanateknologi.web.id |
| **Komponen** | Upload Jaringan IPAL (/ipal/upload) |
| **Environment** | Chrome (Selenium headless), Windows 11 |

## 1. Ringkasan Hasil Pengujian

| Metrik | Nilai |
|---|---|
| Total Test Case | 17 |
| Passed | 17 |
| Failed | 0 |
| Pass Rate | 100% |

## 2. Daftar Hasil Pengujian (Expected vs Actual)

| No | Test Case (Skenario) | Hasil Diharapkan (Expected) | Hasil Aktual (Actual) | Status |
|---|---|---|---|---|
| 1 | Unggah berkas valid_pipe_geojson untuk dataset Jalur Pipa | Then hasil unggah seharusnya "berhasil" | Sesuai harapan - seluruh langkah lulus. | PASS |
| 2 | Unggah berkas valid_pipe_json untuk dataset Jalur Pipa | Then hasil unggah seharusnya "berhasil" | Sesuai harapan - seluruh langkah lulus. | PASS |
| 3 | Unggah berkas not_featurecollection untuk dataset Jalur Pipa | Then hasil unggah seharusnya "gagal" | Sesuai harapan - seluruh langkah lulus. | PASS |
| 4 | Unggah berkas malformed_json untuk dataset Jalur Pipa | Then hasil unggah seharusnya "gagal" | Sesuai harapan - seluruh langkah lulus. | PASS |
| 5 | Unggah berkas empty_file untuk dataset Jalur Pipa | Then hasil unggah seharusnya "gagal" | Sesuai harapan - seluruh langkah lulus. | PASS |
| 6 | Unggah berkas wrong_ext_txt untuk dataset Jalur Pipa | Then hasil unggah seharusnya "gagal" | Sesuai harapan - seluruh langkah lulus. | PASS |
| 7 | Unggah berkas wrong_ext_csv untuk dataset Jalur Pipa | Then hasil unggah seharusnya "gagal" | Sesuai harapan - seluruh langkah lulus. | PASS |
| 8 | Unggah berkas wrong_ext_png untuk dataset Jalur Pipa | Then hasil unggah seharusnya "gagal" | Sesuai harapan - seluruh langkah lulus. | PASS |
| 9 | Unggah berkas oversized_geojson untuk dataset Jalur Pipa | Then hasil unggah seharusnya "gagal" | Sesuai harapan - seluruh langkah lulus. | PASS |
| 10 | Unggah berkas valid_manhole_geojson untuk dataset Manhole | Then hasil unggah seharusnya "berhasil" | Sesuai harapan - seluruh langkah lulus. | PASS |
| 11 | Unggah berkas not_featurecollection untuk dataset Manhole | Then hasil unggah seharusnya "gagal" | Sesuai harapan - seluruh langkah lulus. | PASS |
| 12 | Unggah berkas malformed_json untuk dataset Manhole | Then hasil unggah seharusnya "gagal" | Sesuai harapan - seluruh langkah lulus. | PASS |
| 13 | Unggah berkas empty_file untuk dataset Manhole | Then hasil unggah seharusnya "gagal" | Sesuai harapan - seluruh langkah lulus. | PASS |
| 14 | Unggah berkas wrong_ext_txt untuk dataset Manhole | Then hasil unggah seharusnya "gagal" | Sesuai harapan - seluruh langkah lulus. | PASS |
| 15 | Unggah berkas oversized_geojson untuk dataset Manhole | Then hasil unggah seharusnya "gagal" | Sesuai harapan - seluruh langkah lulus. | PASS |
| 16 | Menampilkan pesan validasi ketika berkas bukan FeatureCollection | Then pesan hasil memuat teks "FeatureCollection" | Sesuai harapan - seluruh langkah lulus. | PASS |
| 17 | Menekan Unggah tanpa memilih berkas | Then unggahan seharusnya gagal | Sesuai harapan - seluruh langkah lulus. | PASS |

## 3. Daftar Bug Ditemukan

Tidak ada skenario yang gagal. Tidak ada bug ditemukan pada suite ini.


