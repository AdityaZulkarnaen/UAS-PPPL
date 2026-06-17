# Bug Report - Suite Kirim Aduan (Lapor Masalah Manhole)

> **Dibuat otomatis** oleh `scripts/generate-bug-report.ps1` - jangan diedit manual.
> Sumber data: `target/cucumber-reports/cucumber.json` | Waktu generate: 2026-06-17 21:39:23

| Info | Nilai |
|---|---|
| **Aplikasi** | BPAL PJK IPAL - https://bpalpjk.madanateknologi.web.id |
| **Komponen** | Lapor Masalah Aset Manhole / Pengaduan Publik (/ipal/map) |
| **Environment** | Chrome (Selenium headless), Windows 11 |

## 1. Ringkasan Hasil Pengujian

| Metrik | Nilai |
|---|---|
| Total Test Case | 12 |
| Passed | 9 |
| Failed | 3 |
| Pass Rate | 75% |

## 2. Daftar Hasil Pengujian (Expected vs Actual)

| No | Test Case (Skenario) | Hasil Diharapkan (Expected) | Hasil Aktual (Actual) | Status |
|---|---|---|---|---|
| 1 | Visitor membuka form Lapor Masalah dari popup manhole pada peta | Then Popup manhole menampilkan tombol Lapor Masalah; And Visitor mengklik tombol Lapor Masalah pada popup; Then Halaman Lapor Masalah berhasil ditampilkan dengan data aset terisi otomatis | Sesuai harapan - seluruh langkah lulus. | PASS |
| 2 | Visitor berhasil mengirim laporan masalah manhole dengan data valid | Then Laporan berhasil terkirim dengan nomor tiket | Sesuai harapan - seluruh langkah lulus. | PASS |
| 3 | Visitor mengirim laporan tanpa mengisi deskripsi | Then Sistem menampilkan validasi deskripsi wajib diisi; And Laporan gagal terkirim | Sesuai harapan - seluruh langkah lulus. | PASS |
| 4 | BVA deskripsi laporan sepanjang 1 karakter | Then Laporan berhasil terkirim dengan nomor tiket | Sesuai harapan - seluruh langkah lulus. | PASS |
| 5 | BVA deskripsi laporan sepanjang 5000 karakter | Then Laporan berhasil terkirim dengan nomor tiket | Sesuai harapan - seluruh langkah lulus. | PASS |
| 6 | BVA deskripsi laporan mencoba melebihi batas 5000 karakter | Then Sistem menampilkan validasi deskripsi tidak boleh lebih dari 5000 karakter; And Laporan gagal terkirim | Sesuai harapan - seluruh langkah lulus. | PASS |
| 7 | Visitor mengirim laporan tanpa mengunggah foto | Then Sistem menampilkan validasi foto wajib diunggah; And Laporan gagal terkirim | Sesuai harapan - seluruh langkah lulus. | PASS |
| 8 | Unggah foto dokumentasi dengan format PNG valid | Then Laporan berhasil terkirim dengan nomor tiket | Sesuai harapan - seluruh langkah lulus. | PASS |
| 9 | Unggah foto dengan ekstensi yang tidak didukung (.pdf) | Then Sistem menolak atau tidak memproses berkas foto berekstensi tidak didukung | Sesuai harapan - seluruh langkah lulus. | PASS |
| 10 | Visitor mengirim laporan dengan jawaban captcha yang salah | Then Sistem menampilkan validasi captcha salah; And Laporan gagal terkirim | Gagal di langkah 'Then Sistem menampilkan validasi captcha salah' -> org.opentest4j.AssertionFailedError: Pesan validasi captcha tidak sesuai. Actual: 'Terlalu banyak pengiriman. Silakan coba lagi dalam beberapa menit.' ==> expected: <true> but was: <false> | FAIL |
| 11 | Visitor mengganti soal captcha sebelum menjawab | Then Laporan berhasil terkirim dengan nomor tiket | Gagal di langkah 'Then Laporan berhasil terkirim dengan nomor tiket' -> org.opentest4j.AssertionFailedError: Sistem tidak redirect ke /ipal/map dan tidak menampilkan pesan sukses. URL saat ini: https://bpalpjk.madanateknologi.web.id/ipal/lapor-masalah?type=manhole&id=19&kode=4.2_J1.MH-25&coord=-7.800597%2C%20110.362002&wilayah=. GlobalError: Terlalu banyak pengiriman. Silakan coba lagi dalam beberapa menit. ==> expected: <true> but was: <false> | FAIL |
| 12 | Laporan masalah manhole baru muncul dan dapat dibuka admin di daftar aduan | Then Laporan berhasil terkirim dengan nomor tiket; Then Aduan manhole baru muncul pada daftar aduan admin; And Admin membuka detail aduan manhole tersebut; Then Detail aduan menampilkan status awal "masuk" | Gagal di langkah 'Then Laporan berhasil terkirim dengan nomor tiket' -> org.opentest4j.AssertionFailedError: Sistem tidak redirect ke /ipal/map dan tidak menampilkan pesan sukses. URL saat ini: https://bpalpjk.madanateknologi.web.id/ipal/lapor-masalah?type=manhole&id=19&kode=4.2_J1.MH-25&coord=-7.800597%2C%20110.362002&wilayah=. GlobalError: Terlalu banyak pengiriman. Silakan coba lagi dalam beberapa menit. ==> expected: <true> but was: <false> | FAIL |

## 3. Daftar Bug Ditemukan

Ditemukan **3** skenario gagal - kandidat bug:

| Bug ID | Test Case | Ringkasan Hasil Aktual |
|---|---|---|
| BUG-ADU-01 | Visitor mengirim laporan dengan jawaban captcha yang salah | Gagal di langkah 'Then Sistem menampilkan validasi captcha salah' -> org.opentest4j.AssertionFailedError: Pesan validasi captcha tidak sesuai. Actual: 'Terlalu banyak pengiriman. Silakan coba lagi dalam beberapa menit.' ==> expected: <true> but was: <false> |
| BUG-ADU-02 | Visitor mengganti soal captcha sebelum menjawab | Gagal di langkah 'Then Laporan berhasil terkirim dengan nomor tiket' -> org.opentest4j.AssertionFailedError: Sistem tidak redirect ke /ipal/map dan tidak menampilkan pesan sukses. URL saat ini: https://bpalpjk.madanateknologi.web.id/ipal/lapor-masalah?type=manhole&id=19&kode=4.2_J1.MH-25&coord=-7.800597%2C%20110.362002&wilayah=. GlobalError: Terlalu banyak pengiriman. Silakan coba lagi dalam beberapa menit. ==> expected: <true> but was: <false> |
| BUG-ADU-03 | Laporan masalah manhole baru muncul dan dapat dibuka admin di daftar aduan | Gagal di langkah 'Then Laporan berhasil terkirim dengan nomor tiket' -> org.opentest4j.AssertionFailedError: Sistem tidak redirect ke /ipal/map dan tidak menampilkan pesan sukses. URL saat ini: https://bpalpjk.madanateknologi.web.id/ipal/lapor-masalah?type=manhole&id=19&kode=4.2_J1.MH-25&coord=-7.800597%2C%20110.362002&wilayah=. GlobalError: Terlalu banyak pengiriman. Silakan coba lagi dalam beberapa menit. ==> expected: <true> but was: <false> |

### Detail Bug

#### BUG-ADU-01 - Visitor mengirim laporan dengan jawaban captcha yang salah

| Field | Isi |
|---|---|
| **Bug ID** | BUG-ADU-01 |
| **Judul** | Visitor mengirim laporan dengan jawaban captcha yang salah |
| **Severity** | _perlu ditinjau (Critical/Major/Minor/Trivial)_ |
| **Priority** | _perlu ditinjau (High/Medium/Low)_ |
| **Komponen** | Lapor Masalah Aset Manhole / Pengaduan Publik (/ipal/map) |
| **Environment** | Chrome (Selenium headless), Windows 11, https://bpalpjk.madanateknologi.web.id |
| **Hasil Diharapkan** | Then Sistem menampilkan validasi captcha salah; And Laporan gagal terkirim |
| **Hasil Aktual** | Gagal di langkah 'Then Sistem menampilkan validasi captcha salah' -> org.opentest4j.AssertionFailedError: Pesan validasi captcha tidak sesuai. Actual: 'Terlalu banyak pengiriman. Silakan coba lagi dalam beberapa menit.' ==> expected: <true> but was: <false> |
| **Status** | Open |

**Langkah Reproduksi:**
```gherkin
1. Given Visitor membuka halaman Lapor Masalah untuk manhole "4.2_J1.MH-25"
2. And Visitor mengisi deskripsi laporan dengan teks "Manhole tergenang air saat hujan."
3. And Visitor mengunggah foto dengan kasus "valid_photo_png"
4. And Visitor menjawab captcha dengan salah
5. And Visitor menekan tombol Kirim Laporan
6. Then Sistem menampilkan validasi captcha salah
7. And Laporan gagal terkirim
```

**Detail Error:**
```
org.opentest4j.AssertionFailedError: Pesan validasi captcha tidak sesuai. Actual: 'Terlalu banyak pengiriman. Silakan coba lagi dalam beberapa menit.' ==> expected: <true> but was: <false>
	at org.junit.jupiter.api.AssertionFailureBuilder.build(AssertionFailureBuilder.java:151)
	at org.junit.jupiter.api.AssertionFailureBuilder.buildAndThrow(AssertionFailureBuilder.java:132)
	at org.junit.jupiter.api.AssertTrue.failNotTrue(AssertTrue.java:63)
	at org.junit.jupiter.api.AssertTrue.assertTrue(AssertTrue.java:36)
	at org.junit.jupiter.api.Assertions.assertTrue(Assertions.java:214)
	at org.example.steps.LaporMasalahManholeSteps.sistemMenampilkanValidasiCaptchaSalah(LaporMasalahManholeSteps.java:286)
	at ✽.Sistem menampilkan validasi captcha salah(classpath:features/lapor_masalah_manhole.feature:127)
```

#### BUG-ADU-02 - Visitor mengganti soal captcha sebelum menjawab

| Field | Isi |
|---|---|
| **Bug ID** | BUG-ADU-02 |
| **Judul** | Visitor mengganti soal captcha sebelum menjawab |
| **Severity** | _perlu ditinjau (Critical/Major/Minor/Trivial)_ |
| **Priority** | _perlu ditinjau (High/Medium/Low)_ |
| **Komponen** | Lapor Masalah Aset Manhole / Pengaduan Publik (/ipal/map) |
| **Environment** | Chrome (Selenium headless), Windows 11, https://bpalpjk.madanateknologi.web.id |
| **Hasil Diharapkan** | Then Laporan berhasil terkirim dengan nomor tiket |
| **Hasil Aktual** | Gagal di langkah 'Then Laporan berhasil terkirim dengan nomor tiket' -> org.opentest4j.AssertionFailedError: Sistem tidak redirect ke /ipal/map dan tidak menampilkan pesan sukses. URL saat ini: https://bpalpjk.madanateknologi.web.id/ipal/lapor-masalah?type=manhole&id=19&kode=4.2_J1.MH-25&coord=-7.800597%2C%20110.362002&wilayah=. GlobalError: Terlalu banyak pengiriman. Silakan coba lagi dalam beberapa menit. ==> expected: <true> but was: <false> |
| **Status** | Open |

**Langkah Reproduksi:**
```gherkin
1. Given Visitor membuka halaman Lapor Masalah untuk manhole "4.2_J1.MH-25"
2. And Visitor mengisi deskripsi laporan dengan teks "Penutup manhole hilang, berisiko membahayakan pejalan kaki."
3. And Visitor mengunggah foto dengan kasus "valid_photo_png"
4. And Visitor meminta soal captcha baru
5. And Visitor menjawab captcha dengan benar
6. And Visitor menekan tombol Kirim Laporan
7. Then Laporan berhasil terkirim dengan nomor tiket
```

**Detail Error:**
```
org.opentest4j.AssertionFailedError: Sistem tidak redirect ke /ipal/map dan tidak menampilkan pesan sukses. URL saat ini: https://bpalpjk.madanateknologi.web.id/ipal/lapor-masalah?type=manhole&id=19&kode=4.2_J1.MH-25&coord=-7.800597%2C%20110.362002&wilayah=. GlobalError: Terlalu banyak pengiriman. Silakan coba lagi dalam beberapa menit. ==> expected: <true> but was: <false>
	at org.junit.jupiter.api.AssertionFailureBuilder.build(AssertionFailureBuilder.java:151)
	at org.junit.jupiter.api.AssertionFailureBuilder.buildAndThrow(AssertionFailureBuilder.java:132)
	at org.junit.jupiter.api.AssertTrue.failNotTrue(AssertTrue.java:63)
	at org.junit.jupiter.api.AssertTrue.assertTrue(AssertTrue.java:36)
	at org.junit.jupiter.api.Assertions.assertTrue(Assertions.java:214)
	at org.example.steps.LaporMasalahManholeSteps.laporanBerhasilTerkirim(LaporMasalahManholeSteps.java:212)
	at ✽.Laporan berhasil terkirim dengan nomor tiket(classpath:features/lapor_masalah_manhole.feature:141)
```

#### BUG-ADU-03 - Laporan masalah manhole baru muncul dan dapat dibuka admin di daftar aduan

| Field | Isi |
|---|---|
| **Bug ID** | BUG-ADU-03 |
| **Judul** | Laporan masalah manhole baru muncul dan dapat dibuka admin di daftar aduan |
| **Severity** | _perlu ditinjau (Critical/Major/Minor/Trivial)_ |
| **Priority** | _perlu ditinjau (High/Medium/Low)_ |
| **Komponen** | Lapor Masalah Aset Manhole / Pengaduan Publik (/ipal/map) |
| **Environment** | Chrome (Selenium headless), Windows 11, https://bpalpjk.madanateknologi.web.id |
| **Hasil Diharapkan** | Then Laporan berhasil terkirim dengan nomor tiket; Then Aduan manhole baru muncul pada daftar aduan admin; And Admin membuka detail aduan manhole tersebut; Then Detail aduan menampilkan status awal "masuk" |
| **Hasil Aktual** | Gagal di langkah 'Then Laporan berhasil terkirim dengan nomor tiket' -> org.opentest4j.AssertionFailedError: Sistem tidak redirect ke /ipal/map dan tidak menampilkan pesan sukses. URL saat ini: https://bpalpjk.madanateknologi.web.id/ipal/lapor-masalah?type=manhole&id=19&kode=4.2_J1.MH-25&coord=-7.800597%2C%20110.362002&wilayah=. GlobalError: Terlalu banyak pengiriman. Silakan coba lagi dalam beberapa menit. ==> expected: <true> but was: <false> |
| **Status** | Open |

**Langkah Reproduksi:**
```gherkin
1. Given Visitor membuka halaman Lapor Masalah untuk manhole "4.2_J1.MH-25"
2. And Visitor mengisi deskripsi laporan dengan teks "QA-E2E: verifikasi aduan manhole tampil di sisi admin."
3. And Visitor mengunggah foto dengan kasus "valid_photo_png"
4. And Visitor menjawab captcha dengan benar
5. And Visitor menekan tombol Kirim Laporan
6. Then Laporan berhasil terkirim dengan nomor tiket
7. Given Admin sudah login ke aplikasi Simlab-BPJK untuk verifikasi aduan
8. When Admin mencari aduan berdasarkan kode manhole "4.2_J1.MH-25"
9. Then Aduan manhole baru muncul pada daftar aduan admin
10. And Admin membuka detail aduan manhole tersebut
11. Then Detail aduan menampilkan status awal "masuk"
```

**Detail Error:**
```
org.opentest4j.AssertionFailedError: Sistem tidak redirect ke /ipal/map dan tidak menampilkan pesan sukses. URL saat ini: https://bpalpjk.madanateknologi.web.id/ipal/lapor-masalah?type=manhole&id=19&kode=4.2_J1.MH-25&coord=-7.800597%2C%20110.362002&wilayah=. GlobalError: Terlalu banyak pengiriman. Silakan coba lagi dalam beberapa menit. ==> expected: <true> but was: <false>
	at org.junit.jupiter.api.AssertionFailureBuilder.build(AssertionFailureBuilder.java:151)
	at org.junit.jupiter.api.AssertionFailureBuilder.buildAndThrow(AssertionFailureBuilder.java:132)
	at org.junit.jupiter.api.AssertTrue.failNotTrue(AssertTrue.java:63)
	at org.junit.jupiter.api.AssertTrue.assertTrue(AssertTrue.java:36)
	at org.junit.jupiter.api.Assertions.assertTrue(Assertions.java:214)
	at org.example.steps.LaporMasalahManholeSteps.laporanBerhasilTerkirim(LaporMasalahManholeSteps.java:212)
	at ✽.Laporan berhasil terkirim dengan nomor tiket(classpath:features/lapor_masalah_manhole.feature:153)
```

> Tinjau tiap entri: bila merupakan cacat aplikasi (bukan masalah environment/
> locator), tetapkan Severity & Priority lalu laporkan resmi ke tim pengembang.

