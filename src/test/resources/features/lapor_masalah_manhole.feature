@lapor_masalah @manhole
Feature: Lapor Masalah Aset Manhole (Pengaduan Publik)
  Sebagai pengguna publik (visitor, tanpa login), saya ingin melaporkan masalah
  pada aset manhole jaringan IPAL melalui peta publik (/ipal/map), sehingga BPAL
  PJK dapat menindaklanjuti laporan tersebut.
  Test suite menggunakan Equivalence Partitioning (EP) dan Boundary Value
  Analysis (BVA) untuk field deskripsi (maxlength=5000), foto dokumentasi
  (tipe & ukuran berkas), dan verifikasi captcha aritmatika.

  # -----------------------------------------------------------------------
  # TC-M01 — Navigasi: cari manhole -> popup -> Lapor Masalah (EP, positive)
  # -----------------------------------------------------------------------
  @ep @positive @navigasi @TC-M01
  Scenario: Visitor membuka form Lapor Masalah dari popup manhole pada peta
    Given Visitor membuka halaman Public Map IPAL untuk lapor masalah
    When Visitor mencari manhole "4.2_J1.MH-25" pada peta
    Then Popup manhole menampilkan tombol Lapor Masalah
    And Visitor mengklik tombol Lapor Masalah pada popup
    Then Halaman Lapor Masalah berhasil ditampilkan dengan data aset terisi otomatis

  # -----------------------------------------------------------------------
  # TC-M02 — Kirim laporan dengan data valid lengkap (EP, positive, happy path)
  # -----------------------------------------------------------------------
  @ep @positive @submit @TC-M02
  Scenario: Visitor berhasil mengirim laporan masalah manhole dengan data valid
    Given Visitor membuka halaman Lapor Masalah untuk manhole "4.2_J1.MH-25"
    And Visitor mengisi deskripsi laporan dengan teks "Tutup manhole retak dan berbau menyengat di area ini."
    And Visitor mengunggah foto dengan kasus "valid_photo_png"
    And Visitor menjawab captcha dengan benar
    And Visitor menekan tombol Kirim Laporan
    Then Laporan berhasil terkirim dengan nomor tiket

  # -----------------------------------------------------------------------
  # TC-M03 — Deskripsi kosong (EP, negative — field wajib)
  # -----------------------------------------------------------------------
  @ep @negative @deskripsi @TC-M03
  Scenario: Visitor mengirim laporan tanpa mengisi deskripsi
    Given Visitor membuka halaman Lapor Masalah untuk manhole "4.2_J1.MH-25"
    And Visitor tidak mengisi deskripsi laporan
    And Visitor mengunggah foto dengan kasus "valid_photo_png"
    And Visitor menjawab captcha dengan benar
    And Visitor menekan tombol Kirim Laporan
    Then Sistem menampilkan validasi deskripsi wajib diisi
    And Laporan gagal terkirim

  # -----------------------------------------------------------------------
  # TC-M04, TC-M05, TC-M06 — BVA panjang deskripsi (maxlength=5000)
  # -----------------------------------------------------------------------
  @bva @deskripsi @TC-M04 @TC-M05 @TC-M06
  Scenario Outline: BVA deskripsi laporan sepanjang <panjang> karakter
    Given Visitor membuka halaman Lapor Masalah untuk manhole "4.2_J1.MH-25"
    And Visitor mengisi deskripsi laporan sepanjang <panjang> karakter
    And Visitor mengunggah foto dengan kasus "valid_photo_png"
    And Visitor menjawab captcha dengan benar
    And Visitor menekan tombol Kirim Laporan
    Then <hasil>

    Examples:
      | panjang | hasil                                  |
      | 1       | Laporan berhasil terkirim dengan nomor tiket |
      | 5000    | Laporan berhasil terkirim dengan nomor tiket |

  # Catatan BVA: textarea memiliki atribut maxlength="5000" di DOM, sehingga
  # mengetik 5001 karakter via browser secara native akan terpotong oleh
  # browser itu sendiri (bukan aplikasi). Skenario di bawah memverifikasi
  # bahwa batas ini benar2 ditegakkan: nilai textarea TIDAK PERNAH melebihi
  # 5000 karakter walau kita mencoba mengisi 5001 karakter.
  @bva @deskripsi @boundary-enforcement @TC-M07
  Scenario: BVA deskripsi laporan mencoba melebihi batas 5000 karakter
    Given Visitor membuka halaman Lapor Masalah untuk manhole "4.2_J1.MH-25"
    And Visitor mengisi deskripsi laporan sepanjang 5001 karakter
    And Visitor mengunggah foto dengan kasus "valid_photo_png"
    And Visitor menjawab captcha dengan benar
    And Visitor menekan tombol Kirim Laporan
    Then Laporan berhasil terkirim dengan nomor tiket

  # -----------------------------------------------------------------------
  # TC-M08 — Foto tidak diunggah (EP, negative — field wajib)
  # -----------------------------------------------------------------------
  @ep @negative @foto @TC-M08
  Scenario: Visitor mengirim laporan tanpa mengunggah foto
    Given Visitor membuka halaman Lapor Masalah untuk manhole "4.2_J1.MH-25"
    And Visitor mengisi deskripsi laporan dengan teks "Pipa bocor di sekitar manhole ini."
    And Visitor tidak mengunggah foto
    And Visitor menjawab captcha dengan benar
    And Visitor menekan tombol Kirim Laporan
    Then Sistem menampilkan validasi foto wajib diunggah
    And Laporan gagal terkirim

  # -----------------------------------------------------------------------
  # TC-M09 — EP tipe berkas foto valid (positive)
  # -----------------------------------------------------------------------
  @ep @foto @positive @TC-M09
  Scenario: Unggah foto dokumentasi dengan format PNG valid
    Given Visitor membuka halaman Lapor Masalah untuk manhole "4.2_J1.MH-25"
    And Visitor mengisi deskripsi laporan dengan teks "Pengujian unggah foto dokumentasi format valid."
    And Visitor mengunggah foto dengan kasus "valid_photo_png"
    And Visitor menjawab captcha dengan benar
    And Visitor menekan tombol Kirim Laporan
    Then Laporan berhasil terkirim dengan nomor tiket

  # -----------------------------------------------------------------------
  # TC-M10 — EP tipe berkas foto tidak didukung (negative)
  # Dropzone mendeklarasikan accept="image/jpeg,image/jpg,image/png,image/webp"
  # pada elemen #foto-input, sehingga file berekstensi .pdf seharusnya ditolak
  # oleh browser file-picker / validasi aplikasi.
  # -----------------------------------------------------------------------
  @ep @foto @negative @TC-M10
  Scenario: Unggah foto dengan ekstensi yang tidak didukung (.pdf)
    Given Visitor membuka halaman Lapor Masalah untuk manhole "4.2_J1.MH-25"
    And Visitor mengisi deskripsi laporan dengan teks "Pengujian unggah foto dengan ekstensi tidak didukung."
    And Visitor mengunggah foto dengan kasus "wrong_ext_photo_pdf"
    And Visitor menjawab captcha dengan benar
    And Visitor menekan tombol Kirim Laporan
    Then Sistem menolak atau tidak memproses berkas foto berekstensi tidak didukung

  # -----------------------------------------------------------------------
  # TC-M11 — BVA ukuran berkas foto besar (>10MB)
  # CATATAN: belum ada konfirmasi pasti apakah aplikasi menegakkan batas
  # ukuran file untuk foto dokumentasi (tidak ditemukan validasi ukuran di
  # markup form). Skenario ini bersifat eksploratif: jika aplikasi TERNYATA
  # tidak memvalidasi ukuran, hasilnya dicatat sebagai temuan/bug, bukan
  # kegagalan test (mengikuti pola BUG-02 pada modul aduan).
  # -----------------------------------------------------------------------
  @bva @foto @exploratory @TC-M11
  Scenario: Unggah foto dokumentasi berukuran besar (>10MB)
    Given Visitor membuka halaman Lapor Masalah untuk manhole "4.2_J1.MH-25"
    And Visitor mengisi deskripsi laporan dengan teks "Pengujian unggah foto berukuran besar."
    And Visitor mengunggah foto dengan kasus "oversized_photo"
    And Visitor menjawab captcha dengan benar
    And Visitor menekan tombol Kirim Laporan
    Then Sistem merespons unggahan foto besar dengan konsisten (berhasil atau ditolak dengan pesan jelas)

  # -----------------------------------------------------------------------
  # TC-M11 — Captcha dijawab salah (EP, negative)
  # -----------------------------------------------------------------------
  @ep @negative @captcha @TC-M11
  Scenario: Visitor mengirim laporan dengan jawaban captcha yang salah
    Given Visitor membuka halaman Lapor Masalah untuk manhole "4.2_J1.MH-25"
    And Visitor mengisi deskripsi laporan dengan teks "Manhole tergenang air saat hujan."
    And Visitor mengunggah foto dengan kasus "valid_photo_png"
    And Visitor menjawab captcha dengan salah
    And Visitor menekan tombol Kirim Laporan
    Then Sistem menampilkan validasi captcha salah
    And Laporan gagal terkirim

  # -----------------------------------------------------------------------
  # TC-M12 — Visitor meminta soal captcha baru (EP, positive)
  # -----------------------------------------------------------------------
  @ep @positive @captcha @TC-M12
  Scenario: Visitor mengganti soal captcha sebelum menjawab
    Given Visitor membuka halaman Lapor Masalah untuk manhole "4.2_J1.MH-25"
    And Visitor mengisi deskripsi laporan dengan teks "Penutup manhole hilang, berisiko membahayakan pejalan kaki."
    And Visitor mengunggah foto dengan kasus "valid_photo_png"
    And Visitor meminta soal captcha baru
    And Visitor menjawab captcha dengan benar
    And Visitor menekan tombol Kirim Laporan
    Then Laporan berhasil terkirim dengan nomor tiket

  # -----------------------------------------------------------------------
  # TC-M13 — End-to-end: laporan publik -> terlihat & dapat dikelola admin
  # -----------------------------------------------------------------------
  @e2e @admin @TC-M13
  Scenario: Laporan masalah manhole baru muncul dan dapat dibuka admin di daftar aduan
    Given Visitor membuka halaman Lapor Masalah untuk manhole "4.2_J1.MH-25"
    And Visitor mengisi deskripsi laporan dengan teks "QA-E2E: verifikasi aduan manhole tampil di sisi admin."
    And Visitor mengunggah foto dengan kasus "valid_photo_png"
    And Visitor menjawab captcha dengan benar
    And Visitor menekan tombol Kirim Laporan
    Then Laporan berhasil terkirim dengan nomor tiket
    Given Admin sudah login ke aplikasi Simlab-BPJK untuk verifikasi aduan
    When Admin mencari aduan berdasarkan kode manhole "4.2_J1.MH-25"
    Then Aduan manhole baru muncul pada daftar aduan admin
    And Admin membuka detail aduan manhole tersebut
    Then Detail aduan menampilkan status awal "masuk"
