@upload
Feature: Upload Jaringan (GeoJSON) pada Modul IPAL
  Sebagai admin BPAL PJK, saya ingin mengunggah data jaringan (Jalur Pipa / Manhole)
  dalam format GeoJSON, sehingga data jaringan IPAL dapat diperbarui.
  Aturan validasi: tipe berkas .geojson atau .json, ukuran maksimum 50 MB,
  dan isi berkas harus berupa GeoJSON FeatureCollection yang valid.

  Background:
    Given admin sudah login dan berada di halaman Data Jaringan

  # ----------------------------------------------------------------------
  # Equivalence Partitioning + Boundary Value Analysis
  # Setiap baris Examples = satu test case (lihat docs/test-cases.md).
  # "berhasil" => muncul pesan sukses; "gagal" => unggahan ditolak.
  # ----------------------------------------------------------------------
  @ep @bva
  Scenario Outline: Unggah berkas <fileCase> untuk dataset <dataset>
    When admin memilih dataset "<dataset>"
    And admin memilih berkas dengan kasus "<fileCase>"
    And admin menekan tombol Unggah
    Then hasil unggah seharusnya "<hasil>"

    Examples: Jalur Pipa
      | dataset    | fileCase             | hasil    |
      | Jalur Pipa | valid_pipe_geojson   | berhasil |
      | Jalur Pipa | valid_pipe_json      | berhasil |
      | Jalur Pipa | not_featurecollection| gagal    |
      | Jalur Pipa | malformed_json       | gagal    |
      | Jalur Pipa | empty_file           | gagal    |
      | Jalur Pipa | wrong_ext_txt        | gagal    |
      | Jalur Pipa | wrong_ext_csv        | gagal    |
      | Jalur Pipa | wrong_ext_png        | gagal    |
      | Jalur Pipa | oversized_geojson    | gagal    |

    Examples: Manhole
      | dataset | fileCase             | hasil    |
      | Manhole | valid_manhole_geojson| berhasil |
      | Manhole | not_featurecollection| gagal    |
      | Manhole | malformed_json       | gagal    |
      | Manhole | empty_file           | gagal    |
      | Manhole | wrong_ext_txt        | gagal    |
      | Manhole | oversized_geojson    | gagal    |

  # Memastikan pesan validasi spesifik yang terkonfirmasi muncul.
  @ep
  Scenario: Menampilkan pesan validasi ketika berkas bukan FeatureCollection
    When admin memilih dataset "Jalur Pipa"
    And admin memilih berkas dengan kasus "not_featurecollection"
    And admin menekan tombol Unggah
    Then pesan hasil memuat teks "FeatureCollection"

  # Boundary: tidak ada berkas yang dipilih sebelum menekan Unggah.
  @bva
  Scenario: Menekan Unggah tanpa memilih berkas
    When admin memilih dataset "Jalur Pipa"
    And admin menekan tombol Unggah tanpa memilih berkas
    Then unggahan seharusnya gagal
