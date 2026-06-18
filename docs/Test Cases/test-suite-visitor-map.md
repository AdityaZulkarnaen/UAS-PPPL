# Test Suite — Visualisasi Peta Jaringan dan Manhole

**Aplikasi:** BPAL PJK IPAL — `https://bpalpjk.madanateknologi.web.id`
**Fitur diuji:** Visualisasi Peta Jaringan dan Manhole — halaman `/ipal/map`
**Teknik desain test:** Equivalence Partitioning (EP) & Boundary Value Analysis (BVA)


## Aturan / Spesifikasi yang Diuji

| Aspek              | Aturan                                                     |
| ------------------ | ---------------------------------------------------------- |
| Halaman peta       | Peta jaringan harus berhasil ditampilkan                   |
| Filter jaringan    | Filter status jaringan harus tersedia                      |
| Search bar         | Search bar dan tombol Cari Data harus tersedia             |
| Filter status      | Status Baik, Perbaikan, dan Rusak dapat diubah             |
| Filter fungsi pipa | Dropdown fungsi pipa tersedia                              |
| Pencarian valid    | Wilayah/kode manhole yang tersedia dapat diproses          |
| Pencarian invalid  | Data yang tidak tersedia menampilkan pesan tidak ditemukan |
| Detail manhole     | Pencarian valid menampilkan popup detail manhole           |

## Equivalence Partitioning

### Partisi Halaman Peta

| Kelas                                  | Contoh      | Valid? |
| -------------------------------------- | ----------- | ------ |
| EP1 — halaman public map dapat diakses | `/ipal/map` | Valid  |

### Partisi Filter Status

| Kelas                         | Contoh                | Valid? |
| ----------------------------- | --------------------- | ------ |
| EP2 — filter status Baik      | Klik tombol Baik      | Valid  |
| EP3 — filter status Perbaikan | Klik tombol Perbaikan | Valid  |
| EP4 — filter status Rusak     | Klik tombol Rusak     | Valid  |

### Partisi Filter Fungsi Pipa

| Kelas                               | Contoh                  | Valid? |
| ----------------------------------- | ----------------------- | ------ |
| EP5 — dropdown fungsi pipa tersedia | Dropdown `Semua Fungsi` | Valid  |

### Partisi Pencarian

| Kelas                              | Contoh         | Valid?  |
| ---------------------------------- | -------------- | ------- |
| EP6 — pencarian wilayah valid      | `Gondokusuman` | Valid   |
| EP7 — pencarian kode manhole valid | `WH1`          | Valid   |
| EP8 — pencarian tidak valid        | `kota salah`   | Invalid |


## Boundary Value Analysis — Input Pencarian

| Nilai Batas                | Contoh                 | Diharapkan                                     |
| -------------------------- | ---------------------- | ---------------------------------------------- |
| Input kosong               | `""`                   | Tidak membuka detail data                      |
| 1 karakter                 | `G`                    | Sistem mulai memproses pencarian               |
| Nama wilayah valid         | `Gondokusuman`         | Popup detail manhole tampil                    |
| Kode manhole minimum valid | `WH1`                  | Popup detail manhole tampil                    |
| String tidak ditemukan     | `kota salah`           | Pesan data tidak ditemukan tampil              |
| String panjang tidak valid | `Gondokusumanxxxxxxxx` | Data tidak ditemukan atau tidak membuka detail |


## Daftar Test Case

| ID        | Teknik    | Input                     | Prasyarat              | Hasil Diharapkan                    | Scenario Gherkin                                 |
| --------- | --------- | ------------------------- | ---------------------- | ----------------------------------- | ------------------------------------------------ |
| TC-MAP-01 | EP1       | Akses `/ipal/map`         | Aplikasi dapat diakses | Peta, filter, dan search bar tampil | Halaman visualisasi peta berhasil ditampilkan    |
| TC-MAP-02 | EP2       | Klik filter Baik          | Halaman peta terbuka   | Status filter Baik berubah          | User mengubah filter status Baik                 |
| TC-MAP-03 | EP3       | Klik filter Perbaikan     | Halaman peta terbuka   | Status filter Perbaikan berubah     | User mengubah filter status Perbaikan            |
| TC-MAP-04 | EP4       | Klik filter Rusak         | Halaman peta terbuka   | Status filter Rusak berubah         | User mengubah filter status Rusak                |
| TC-MAP-05 | EP5       | Buka dropdown fungsi pipa | Halaman peta terbuka   | Dropdown fungsi pipa tersedia       | User membuka filter fungsi pipa                  |
| TC-MAP-06 | EP8 + BVA | `kota salah`              | Search bar tersedia    | Pesan data tidak ditemukan tampil   | User mencari data yang tidak tersedia            |
| TC-MAP-07 | EP6 + BVA | `Gondokusuman`            | Search bar tersedia    | Popup detail manhole tampil         | User mencari wilayah yang tersedia               |
| TC-MAP-08 | EP7 + BVA | `WH1`                     | Search bar tersedia    | Popup detail manhole tampil         | User membuka detail manhole melalui kode manhole |


## Mapping ke Automation

| Test Case | Feature File          | Tag                      |
| --------- | --------------------- | ------------------------ |
| TC-MAP-01 | `visitor-map.feature` | `@map @smoke`            |
| TC-MAP-02 | `visitor-map.feature` | `@map @filter`           |
| TC-MAP-03 | `visitor-map.feature` | `@map @filter`           |
| TC-MAP-04 | `visitor-map.feature` | `@map @filter`           |
| TC-MAP-05 | `visitor-map.feature` | `@map @filter`           |
| TC-MAP-06 | `visitor-map.feature` | `@map @search @negative` |
| TC-MAP-07 | `visitor-map.feature` | `@map @search @positive` |
| TC-MAP-08 | `visitor-map.feature` | `@map @search @positive` |
