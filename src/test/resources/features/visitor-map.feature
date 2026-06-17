Feature: Visualisasi Peta Jaringan dan Manhole

  Background:
    Given User membuka halaman Public Map IPAL

  @map @smoke
  Scenario: Halaman visualisasi peta berhasil ditampilkan
    Then Peta jaringan ditampilkan
    And Filter jaringan ditampilkan
    And Search bar ditampilkan

  @map @filter
  Scenario: User mengubah filter status Baik
    When User mengklik filter status Baik
    Then Filter status Baik berhasil berubah

  @map @filter
  Scenario: User mengubah filter status Perbaikan
    When User mengklik filter status Perbaikan
    Then Filter status Perbaikan berhasil berubah

  @map @filter
  Scenario: User mengubah filter status Rusak
    When User mengklik filter status Rusak
    Then Filter status Rusak berhasil berubah

  @map @filter
  Scenario: User membuka filter fungsi pipa
    When User membuka dropdown fungsi pipa
    Then Dropdown fungsi pipa tersedia

  @map @search @negative
  Scenario: User mencari data yang tidak tersedia
    When User mengisi pencarian peta dengan "kota salah"
    And User mengklik tombol Cari Data pada peta
    Then Sistem menampilkan pesan data tidak ditemukan

  @map @search @positive
  Scenario: User mencari wilayah yang tersedia
    When User mengisi pencarian peta dengan "Gondokusuman"
    And User menekan Enter pada pencarian peta
    Then Popup detail manhole ditampilkan

  @map @search @positive
  Scenario: User membuka detail manhole melalui kode manhole
    When User mengisi pencarian peta dengan "WH1"
    And User menekan Enter pada pencarian peta
    Then Popup detail manhole ditampilkan