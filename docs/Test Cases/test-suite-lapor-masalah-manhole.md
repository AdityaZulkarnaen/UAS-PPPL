# Test Suite — Fitur Lapor Masalah Aset Manhole (Pengaduan Publik)

**Aplikasi:** BPAL PJK IPAL — `https://bpalpjk.madanateknologi.web.id`
**Fitur diuji:** Lapor Masalah Aset Manhole — halaman publik `/ipal/map` (popup) dan `/ipal/lapor-masalah`
**Teknik desain test:** Equivalence Partitioning (EP) & Boundary Value Analysis (BVA)


## Aturan / Spesifikasi yang Diuji

| Aspek                    | Aturan                                                              |
| ------------------------ | --------------------------------------------------------------------- |
| Akses fitur               | Tanpa login (visitor publik), diakses dari popup manhole pada peta    |
| Data aset                | ID IPAL & koordinat terisi otomatis dari manhole yang dipilih di peta |
| Deskripsi laporan         | Wajib diisi, maksimum 5000 karakter                                   |
| Foto dokumentasi          | Wajib diunggah, tipe diterima: jpg/jpeg/png/webp                      |
| Captcha                   | Soal aritmatika sederhana (a + b / a - b / a x b), wajib dijawab benar |
| Ganti soal captcha        | Visitor dapat meminta soal baru sebelum submit                        |
| Hasil sukses              | Banner sukses + nomor tiket, lalu redirect ke `/ipal/map`             |
| Hasil gagal               | Pesan validasi spesifik per field, laporan tidak terkirim             |
| Integrasi sisi admin      | Laporan baru harus muncul di daftar aduan admin dengan status "masuk" |


## Equivalence Partitioning

### Partisi: Navigasi ke Form

| Kelas                                          | Contoh                                  | Valid? |
| ----------------------------------------------- | ---------------------------------------- | ------ |
| EP1 — buka form via popup manhole di peta        | Cari manhole → klik "Lapor Masalah"      | Valid  |
| EP2 — buka form langsung (tanpa lewat popup)      | Navigasi langsung ke `/ipal/lapor-masalah` dengan parameter manhole | Valid  |

### Partisi: Deskripsi Laporan

| Kelas                                  | Contoh                                  | Valid?  |
| ---------------------------------------- | ------------------------------------------ | ------- |
| EP3 — deskripsi terisi (1–5000 karakter) | `"Manhole tergenang air saat hujan."`      | Valid   |
| EP4 — deskripsi kosong                   | `""`                                       | Invalid |
| EP5 — deskripsi melebihi batas maksimum  | 5001 karakter                              | Invalid |

### Partisi: Foto Dokumentasi

| Kelas                                | Contoh                  | Valid?  |
| --------------------------------------- | ------------------------ | ------- |
| EP6 — tipe berkas didukung (image)      | `.png`, `.jpg`, `.webp`  | Valid   |
| EP7 — tipe berkas tidak didukung        | `.pdf`                   | Invalid |
| EP8 — tidak ada foto diunggah           | (tidak memilih berkas)   | Invalid |

### Partisi: Captcha

| Kelas                                | Contoh                       | Valid?  |
| --------------------------------------- | ------------------------------- | ------- |
| EP9 — jawaban captcha benar             | Hasil hitung sesuai soal        | Valid   |
| EP10 — jawaban captcha salah            | Hasil hitung tidak sesuai soal  | Invalid |
| EP11 — meminta soal captcha baru         | Klik "Ganti" sebelum menjawab    | Valid   |

### Partisi: Verifikasi Sisi Admin (E2E)

| Kelas                                                  | Contoh                                            | Valid? |
| --------------------------------------------------------- | ---------------------------------------------------- | ------ |
| EP12 — laporan baru terlihat & dapat dibuka admin           | Cari kode manhole di daftar aduan admin               | Valid  |


## Boundary Value Analysis — Panjang Deskripsi (batas 5000 karakter)

| Nilai Batas                     | Diharapkan                                                  |
| ---------------------------------- | --------------------------------------------------------------- |
| 1 karakter                          | Laporan berhasil terkirim (batas bawah valid)                   |
| 5000 karakter (tepat di batas atas) | Laporan berhasil terkirim                                       |
| 5001 karakter (melebihi batas atas) | Ditolak — validasi "tidak boleh lebih dari 5000 karakter"        |
| 0 karakter (kosong)                 | Ditolak — validasi "deskripsi wajib diisi"                       |

> Catatan: batas bawah BVA diwakili oleh kasus deskripsi kosong (EP4), karena
> "1 karakter" sudah cukup mewakili kelas valid minimum tanpa perlu kasus
> terpisah "0 karakter non-kosong".


## Daftar Test Case

| ID      | Teknik     | Input / Aksi Utama                                              | Prasyarat                                  | Hasil Diharapkan                                                                                          | Scenario Gherkin                                                          |
| ------- | ---------- | ------------------------------------------------------------------ | --------------------------------------------- | -------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------ |
| TC-M01  | EP1        | Cari manhole `4.2_J1.MH-25` di peta, klik "Lapor Masalah" pada popup | Halaman Public Map terbuka                     | Popup menampilkan tombol Lapor Masalah; form tampil dengan ID IPAL & koordinat terisi otomatis                  | Visitor membuka form Lapor Masalah dari popup manhole pada peta                |
| TC-M02  | EP3+EP6+EP9 | Deskripsi valid + foto PNG valid + captcha benar                   | Form Lapor Masalah terbuka (manhole `4.2_J1.MH-25`) | Laporan berhasil terkirim dengan nomor tiket                                                                     | Visitor berhasil mengirim laporan masalah manhole dengan data valid             |
| TC-M03  | EP4        | Deskripsi tidak diisi + foto valid + captcha benar                 | idem                                            | Validasi "deskripsi wajib diisi" tampil; laporan gagal terkirim                                                  | Visitor mengirim laporan tanpa mengisi deskripsi                                |
| TC-M04  | BVA (1)    | Deskripsi 1 karakter + foto valid + captcha benar                  | idem                                            | Laporan berhasil terkirim dengan nomor tiket                                                                     | BVA deskripsi laporan sepanjang 1 karakter                                      |
| TC-M05  | BVA (5000) | Deskripsi 5000 karakter + foto valid + captcha benar                | idem                                            | Laporan berhasil terkirim dengan nomor tiket                                                                     | BVA deskripsi laporan sepanjang 5000 karakter                                  |
| TC-M06  | EP5+BVA (5001) | Deskripsi 5001 karakter + foto valid + captcha benar             | idem                                            | Validasi "tidak boleh lebih dari 5000 karakter" tampil; laporan gagal terkirim                                   | BVA deskripsi laporan mencoba melebihi batas 5000 karakter                     |
| TC-M07  | EP8        | Deskripsi valid + foto TIDAK diunggah + captcha benar               | idem                                            | Validasi "foto wajib diunggah" tampil; laporan gagal terkirim                                                    | Visitor mengirim laporan tanpa mengunggah foto                                  |
| TC-M08  | EP6        | Deskripsi valid + foto `.png` valid + captcha benar                 | idem                                            | Laporan berhasil terkirim dengan nomor tiket                                                                     | Unggah foto dokumentasi dengan format PNG valid                                |
| TC-M09  | EP7        | Deskripsi valid + foto berekstensi `.pdf` (isi bukan gambar) + captcha benar | idem                                  | Sistem menolak / tidak memproses berkas foto berekstensi tidak didukung                                          | Unggah foto dengan ekstensi yang tidak didukung (.pdf)                         |
| TC-M10  | EP10       | Deskripsi valid + foto valid + captcha dijawab SALAH                | idem                                            | Validasi "captcha salah" tampil; laporan gagal terkirim                                                          | Visitor mengirim laporan dengan jawaban captcha yang salah                     |
| TC-M11  | EP11       | Klik "Ganti" soal captcha, lalu jawab dengan benar                  | idem                                            | Laporan berhasil terkirim dengan nomor tiket                                                                     | Visitor mengganti soal captcha sebelum menjawab                                |
| TC-M12  | EP12 (E2E) | Submit laporan valid → admin login → cari kode manhole di daftar aduan | Laporan berhasil terkirim sebelumnya; Admin punya akses Simlab-BPJK | Aduan manhole baru muncul di daftar aduan admin; detail menampilkan status awal "masuk"                          | Laporan masalah manhole baru muncul dan dapat dibuka admin di daftar aduan      |

> **Catatan penomoran:** tabel ini memetakan 12 baris uji konseptual ke **13 Scenario**
> pada `lapor_masalah_manhole.feature` (TC-M01–TC-M13), karena TC-M06 pada feature file
> berisi pengujian boundary-enforcement terpisah (TC-M07 di feature = baris TC-M06 di
> tabel ini), dan baris TC-M12 di atas berkorespondensi dengan **TC-M13** di feature file
> (skenario E2E). Lihat kolom "Mapping ke Automation" di bawah untuk korespondensi 1:1
> yang presisi.


## Mapping ke Automation

| Test Case (tabel ini) | Test Case (feature file) | Feature File                     | Tag                                        |
| ------------------------- | ---------------------------- | ------------------------------------- | ----------------------------------------------- |
| TC-M01                    | TC-M01                       | `lapor_masalah_manhole.feature`       | `@ep @positive @navigasi @TC-M01`               |
| TC-M02                    | TC-M02                       | `lapor_masalah_manhole.feature`       | `@ep @positive @submit @TC-M02`                 |
| TC-M03                    | TC-M03                       | `lapor_masalah_manhole.feature`       | `@ep @negative @deskripsi @TC-M03`              |
| TC-M04                    | TC-M04                       | `lapor_masalah_manhole.feature`       | `@bva @deskripsi @TC-M04 @TC-M05 @TC-M06`       |
| TC-M05                    | TC-M05                       | `lapor_masalah_manhole.feature`       | `@bva @deskripsi @TC-M04 @TC-M05 @TC-M06`       |
| TC-M06                    | TC-M07                       | `lapor_masalah_manhole.feature`       | `@bva @deskripsi @boundary-enforcement @TC-M07` |
| TC-M07                    | TC-M08                       | `lapor_masalah_manhole.feature`       | `@ep @negative @foto @TC-M08`                   |
| TC-M08                    | TC-M09                       | `lapor_masalah_manhole.feature`       | `@ep @foto @positive @TC-M09`                   |
| TC-M09                    | TC-M10                       | `lapor_masalah_manhole.feature`       | `@ep @foto @negative @TC-M10`                   |
| TC-M10                    | TC-M11                       | `lapor_masalah_manhole.feature`       | `@ep @negative @captcha @TC-M11`                |
| TC-M11                    | TC-M12                       | `lapor_masalah_manhole.feature`       | `@ep @positive @captcha @TC-M12`                |
| TC-M12                    | TC-M13                       | `lapor_masalah_manhole.feature`       | `@e2e @admin @TC-M13`                           |

**Total: 13 Scenario** pada `lapor_masalah_manhole.feature`
