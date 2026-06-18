# Test Suite — Manajemen Aduan IPAL

**Aplikasi:** BPAL PJK IPAL — `https://bpalpjk.madanateknologi.web.id`
**Fitur diuji:** Manajemen Aduan IPAL — halaman `/ipal/aduan` dan `/ipal/aduan/{id}`
**Teknik desain test:** Equivalence Partitioning (EP) & Boundary Value Analysis (BVA)


## Aturan / Spesifikasi yang Diuji

| Aspek | Aturan |
|---|---|
| Tampil daftar aduan | Halaman `/ipal/aduan` menampilkan seluruh aduan yang masuk |
| Filter status | Daftar dapat difilter berdasarkan status: `masuk`, `proses`, `selesai` |
| Pencarian keyword | Daftar dapat dicari berdasarkan kata kunci; string kosong menampilkan semua aduan |
| Keyword tidak ditemukan | Pencarian keyword yang tidak ada menampilkan kondisi data kosong |
| Navigasi detail | Admin dapat membuka halaman detail aduan `/ipal/aduan/{id}` |
| Aksi Terima | Aduan berstatus `masuk` dapat diterima → status berubah menjadi `proses` |
| Aksi Tolak | Aduan berstatus `masuk` dapat ditolak → status berubah menjadi `ditolak` |
| Aksi Mulai Perbaikan | Aduan berstatus `proses` dapat dimulai perbaikannya → status berubah menjadi `perbaikan` |
| Aksi Tandai Selesai | Aduan berstatus `proses` dapat ditandai selesai → status berubah menjadi `selesai` |
| Catatan progress | Admin dapat menambahkan catatan progress dengan panjang maksimum 5000 karakter |


## Equivalence Partitioning

### Partisi: Filter Status

| Kelas | Contoh | Valid? |
|---|---|---|
| EP1 — status dikenal: masuk | `"masuk"` | Valid |
| EP2 — status dikenal: proses | `"proses"` | Valid |
| EP3 — status dikenal: selesai | `"selesai"` | Valid |
| EP4 — status tidak dikenal | `"tidak_ada"` | Invalid (di luar scope pengujian) |

### Partisi: Pencarian Keyword

| Kelas | Contoh | Valid? |
|---|---|---|
| EP5 — keyword terdapat dalam data | `"ADU-"` | Valid |
| EP6 — keyword kosong (string kosong) | `""` | Valid (menampilkan semua aduan) |
| EP7 — keyword tidak ada dalam data | `"ZZZZNOTFOUND"` | Valid (hasil kosong) |

### Partisi: Aksi Workflow

| Kelas | Contoh | Valid? |
|---|---|---|
| EP8 — aksi sesuai status aduan saat ini | Terima pada aduan `masuk` | Valid |
| EP9 — aksi tidak tersedia untuk status aduan | Tandai Selesai pada aduan `masuk` | Invalid (di luar scope pengujian) |


## Boundary Value Analysis — Catatan Progress (batas 5000 karakter)

| Nilai BVA | Karakter | Hasil Diharapkan |
|---|---|---|
| Minimum | 1 | Diterima (tersimpan) |
| Maksimum valid | 5000 | Diterima (tersimpan) |
| Melewati batas | 5001 | Ditolak (error validasi) |


## Daftar Test Case

| ID | Teknik | Input / Kondisi | Prasyarat | Hasil Diharapkan | Skenario Gherkin |
|---|---|---|---|---|---|
| TC-01 | EP | Login admin valid, buka `/ipal/aduan` | Admin sudah login ke Simlab-BPJK | Halaman daftar aduan berhasil tampil | Melihat daftar aduan |
| TC-02 | EP1 | Pilih filter `"masuk"` | Halaman daftar aduan terbuka | Daftar menampilkan aduan berstatus masuk | Filter status masuk |
| TC-03 | EP2 | Pilih filter `"proses"` | Halaman daftar aduan terbuka | Daftar menampilkan aduan berstatus proses | Filter status proses |
| TC-04 | EP3 | Pilih filter `"selesai"` | Halaman daftar aduan terbuka | Daftar menampilkan aduan berstatus selesai | Filter status selesai |
| TC-05 | EP5 | Keyword `"ADU-"` | Halaman daftar aduan terbuka | Hasil pencarian tampil (minimal 1 baris) | Cari keyword valid |
| TC-06 | EP6 | Keyword `""` (string kosong) | Halaman daftar aduan terbuka | Semua aduan tampil tanpa filter | Cari keyword kosong |
| TC-07 | EP7 | Keyword `"ZZZZNOTFOUND"` | Halaman daftar aduan terbuka | Kondisi data kosong tampil | Cari keyword tidak ada |
| TC-08 | EP | Klik tombol detail aduan pertama | Halaman daftar aduan terbuka | Halaman `/ipal/aduan/{id}` tampil | Lihat detail aduan |
| TC-09 | EP8 | Aduan berstatus `masuk`, klik "Terima" | Halaman detail aduan berstatus masuk | Status aduan berubah menjadi `proses` | Terima aduan |
| TC-10 | EP8 | Aduan berstatus `masuk`, klik "Tolak" | Halaman detail aduan berstatus masuk | Status aduan berubah menjadi `ditolak` | Tolak aduan |
| TC-11 | EP8 | Aduan berstatus `proses`, klik "Mulai Perbaikan" | Halaman detail aduan berstatus proses | Status berubah menjadi `perbaikan` | Mulai perbaikan |
| TC-12 | BVA | Input catatan progress 1 karakter `"A"` | Halaman detail aduan berstatus proses | Catatan berhasil disimpan | BVA catatan 1 karakter |
| TC-13 | BVA | Input catatan progress 5000 karakter | Halaman detail aduan berstatus proses | Catatan berhasil disimpan | BVA catatan 5000 karakter |
| TC-14 | BVA | Input catatan progress 5001 karakter | Halaman detail aduan berstatus proses | Error validasi; input ditolak | BVA catatan 5001 karakter |
| TC-15 | EP8 | Aduan berstatus `proses`, klik "Tandai Selesai" | Halaman detail aduan berstatus proses | Status berubah menjadi `selesai` | Tandai selesai |

**Total: 15 test case** (11 EP + 3 BVA + 1 EP navigasi dasar)


## Mapping ke Automation

| Test Case | Feature File | Tag |
|---|---|---|
| TC-01 | `aduan_management.feature` | `@aduan @daftar @positive` |
| TC-02 | `aduan_management.feature` | `@aduan @filter @positive` |
| TC-03 | `aduan_management.feature` | `@aduan @filter @positive` |
| TC-04 | `aduan_management.feature` | `@aduan @filter @positive` |
| TC-05 | `aduan_management.feature` | `@aduan @pencarian @positive` |
| TC-06 | `aduan_management.feature` | `@aduan @pencarian @positive` |
| TC-07 | `aduan_management.feature` | `@aduan @pencarian @negative` |
| TC-08 | `aduan_management.feature` | `@aduan @detail @positive` |
| TC-09 | `aduan_management.feature` | `@aduan @workflow @positive` |
| TC-10 | `aduan_management.feature` | `@aduan @workflow @positive` |
| TC-11 | `aduan_management.feature` | `@aduan @workflow @positive` |
| TC-12 | `aduan_management.feature` | `@aduan @bva @positive` |
| TC-13 | `aduan_management.feature` | `@aduan @bva @positive` |
| TC-14 | `aduan_management.feature` | `@aduan @bva @negative` |
| TC-15 | `aduan_management.feature` | `@aduan @workflow @positive` |
