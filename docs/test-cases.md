# Test Suite — Fitur Upload Jaringan (GeoJSON)

**Aplikasi:** BPAL PJK IPAL — `https://bpalpjk.madanateknologi.web.id`
**Fitur diuji:** Upload Jaringan Pipa / Manhole (GeoJSON) — halaman `/ipal/upload`
**Teknik desain test:** Equivalence Partitioning (EP) & Boundary Value Analysis (BVA)

## Aturan / Spesifikasi yang Diuji
| Aspek | Aturan |
|---|---|
| Tipe berkas diterima | `.geojson`, `.json` |
| Ukuran berkas maksimum | 50 MB |
| Isi berkas | Harus GeoJSON **FeatureCollection** yang valid |
| Pesan gagal (contoh) | `GeoJSON validation failed: GeoJSON must be a FeatureCollection.` |
| Pesan sukses (contoh) | `Successfully imported N features from GeoJSON (<file>).` |

## Equivalence Partitioning

### Partisi: Tipe Berkas
| Kelas | Contoh | Valid? |
|---|---|---|
| EP1 — ekstensi diizinkan | `.geojson`, `.json` | Valid |
| EP2 — ekstensi tidak diizinkan | `.txt`, `.csv`, `.png`, `.pdf` | Invalid |

### Partisi: Isi Berkas
| Kelas | Contoh | Valid? |
|---|---|---|
| EP3 — FeatureCollection valid | `{"type":"FeatureCollection","features":[...]}` | Valid |
| EP4 — JSON valid tapi bukan FeatureCollection | `{"type":"Feature",...}` | Invalid |
| EP5 — JSON rusak / tidak terparse | `{ "type": "FeatureCollection", "features": [` | Invalid |
| EP6 — berkas kosong | `` (0 byte) | Invalid |

## Boundary Value Analysis — Ukuran Berkas (batas 50 MB)
| Nilai batas | Diharapkan |
|---|---|
| 0 byte (berkas kosong) | Ditolak (EP6) |
| 1 byte | Ditolak (bukan FeatureCollection) |
| tepat di bawah 50 MB | Diterima (jika isi valid) |
| tepat 50 MB | Diterima (jika isi valid) |
| tepat di atas 50 MB (~51 MB) | Ditolak (melebihi batas) |

> Catatan: kasus uji otomatis fokus pada batas atas (>50 MB) sebagai representasi BVA
> sisi-invalid; batas bawah diwakili berkas kosong (0 byte).

## Daftar Test Case (dipetakan ke `upload_jaringan.feature`)

| ID | Teknik | Dataset | Input (kasus berkas) | Prasyarat | Hasil diharapkan | Baris Gherkin (`fileCase`) |
|---|---|---|---|---|---|---|
| TC-01 | EP1+EP3 | Jalur Pipa | FeatureCollection LineString `.geojson` | Login admin, di halaman upload | **Berhasil** (pesan sukses) | `valid_pipe_geojson` |
| TC-02 | EP1+EP3 | Jalur Pipa | FeatureCollection `.json` | idem | **Berhasil** | `valid_pipe_json` |
| TC-03 | EP4 | Jalur Pipa | JSON valid, `type:Feature` | idem | **Gagal** — "must be a FeatureCollection" | `not_featurecollection` |
| TC-04 | EP5 | Jalur Pipa | JSON rusak | idem | **Gagal** | `malformed_json` |
| TC-05 | EP6/BVA(0B) | Jalur Pipa | Berkas kosong | idem | **Gagal** | `empty_file` |
| TC-06 | EP2 | Jalur Pipa | Ekstensi `.txt` | idem | **Gagal** (tipe tidak didukung) | `wrong_ext_txt` |
| TC-07 | EP2 | Jalur Pipa | Ekstensi `.csv` | idem | **Gagal** | `wrong_ext_csv` |
| TC-08 | EP2 | Jalur Pipa | Ekstensi `.png` | idem | **Gagal** | `wrong_ext_png` |
| TC-09 | BVA(>50MB) | Jalur Pipa | `.geojson` ~51 MB | idem | **Gagal** (melebihi 50 MB) | `oversized_geojson` |
| TC-10 | EP1+EP3 | Manhole | FeatureCollection Point `.geojson` | idem | **Berhasil** | `valid_manhole_geojson` |
| TC-11 | EP4 | Manhole | JSON valid, bukan FeatureCollection | idem | **Gagal** | `not_featurecollection` |
| TC-12 | EP5 | Manhole | JSON rusak | idem | **Gagal** | `malformed_json` |
| TC-13 | EP6/BVA(0B) | Manhole | Berkas kosong | idem | **Gagal** | `empty_file` |
| TC-14 | EP2 | Manhole | Ekstensi `.txt` | idem | **Gagal** | `wrong_ext_txt` |
| TC-15 | BVA(>50MB) | Manhole | `.geojson` ~51 MB | idem | **Gagal** | `oversized_geojson` |
| TC-16 | EP4 (asersi pesan) | Jalur Pipa | JSON valid, bukan FeatureCollection | idem | Pesan memuat "FeatureCollection" | Scenario khusus |
| TC-17 | BVA (tanpa input) | Jalur Pipa | Tidak memilih berkas | idem | **Gagal** (tombol nonaktif / ditolak) | Scenario khusus |

**Total: 17 test case** (15 baris Scenario Outline + 2 Scenario khusus).
