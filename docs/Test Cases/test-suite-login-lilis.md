# Test Suite — Fitur Login BPALPJK

**Aplikasi:** BPAL PJK IPAL — `https://bpalpjk.madanateknologi.web.id`
**Fitur diuji:** Login BPALPJK — halaman `/login`
**Teknik desain test:** Equivalence Partitioning (EP) & Boundary Value Analysis (BVA)


## Aturan / Spesifikasi yang Diuji

| Aspek                 | Aturan                                            |
| --------------------- | ------------------------------------------------- |
| Login WhatsApp        | Nomor WhatsApp dapat diketik pada field nomor     |
| Format nomor WhatsApp | Sistem menambahkan prefix `62` secara otomatis    |
| OTP WhatsApp          | OTP tidak dapat dikirim tanpa nomor WhatsApp      |
| Login Email           | User dapat memilih tab Login dengan Email         |
| Kredensial valid      | Email dan password valid dapat masuk ke dashboard |
| Kredensial invalid    | Email/password salah ditolak                      |
| Field kosong          | Email dan password kosong menampilkan validasi    |
| Lupa kata sandi       | Link lupa kata sandi dapat dibuka                 |


## Equivalence Partitioning

### Partisi Input Nomor WhatsApp

| Kelas                              | Contoh | Valid?  |
| ---------------------------------- | ------ | ------- |
| EP1 — input diawali angka selain 8 | `5`    | Valid   |
| EP2 — input diawali angka 8        | `8`    | Valid   |
| EP3 — input kosong                 | `""`   | Invalid |

### Partisi Login Email

| Kelas                           | Contoh                         | Valid?  |
| ------------------------------- | ------------------------------ | ------- |
| EP4 — email dan password valid  | `admin@gmail.com` / `password` | Valid   |
| EP5 — email salah               | `salah@test.com` / `password`  | Invalid |
| EP6 — password salah            | `admin@gmail.com` / `salah123` | Invalid |
| EP7 — email dan password kosong | `""` / `""`                    | Invalid |

### Partisi Navigasi Login

| Kelas                         | Contoh                      | Valid? |
| ----------------------------- | --------------------------- | ------ |
| EP8 — membuka tab email       | Klik tab Login dengan Email | Valid  |
| EP9 — membuka lupa kata sandi | Klik link Lupa kata sandi   | Valid  |


## Boundary Value Analysis — Input Login

| Nilai Batas              | Contoh                         | Diharapkan                     |
| ------------------------ | ------------------------------ | ------------------------------ |
| Nomor kosong             | `""`                           | Validasi nomor WhatsApp muncul |
| 1 digit diawali selain 8 | `5`                            | Field menjadi `625`            |
| 1 digit diawali 8        | `8`                            | Field menjadi `628`            |
| Email/password kosong    | `""` / `""`                    | Validasi login muncul          |
| Kredensial valid         | `admin@gmail.com` / `password` | Redirect ke dashboard          |
| Kredensial invalid       | `admin@gmail.com` / `salah123` | Login gagal                    |


## Daftar Test Case

| ID          | Teknik    | Input                          | Prasyarat             | Hasil Diharapkan                | Scenario Gherkin                                                     |
| ----------- | --------- | ------------------------------ | --------------------- | ------------------------------- | -------------------------------------------------------------------- |
| TC-LOGIN-01 | EP1 + BVA | Nomor WhatsApp `5`             | Halaman login terbuka | Field menampilkan `625`         | Nomor WhatsApp otomatis diawali 62 saat user mengetik angka selain 8 |
| TC-LOGIN-02 | EP2 + BVA | Nomor WhatsApp `8`             | Halaman login terbuka | Field menampilkan `628`         | Nomor WhatsApp tetap valid saat user mengetik angka 8                |
| TC-LOGIN-03 | EP3 + BVA | Nomor kosong                   | Halaman login terbuka | Validasi nomor WhatsApp muncul  | User mengirim OTP tanpa mengisi nomor WhatsApp                       |
| TC-LOGIN-04 | EP8       | Klik tab email                 | Halaman login terbuka | Form email dan password tampil  | User membuka form login email                                        |
| TC-LOGIN-05 | EP4 + BVA | `admin@gmail.com` / `password` | Form email terbuka    | User masuk dashboard            | User berhasil login menggunakan email valid                          |
| TC-LOGIN-06 | EP7 + BVA | Email dan password kosong      | Form email terbuka    | Validasi email/password muncul  | Login email tanpa mengisi data                                       |
| TC-LOGIN-07 | EP5       | `salah@test.com` / `password`  | Form email terbuka    | Pesan login gagal tampil        | Login email menggunakan kredensial tidak valid                       |
| TC-LOGIN-08 | EP6       | `admin@gmail.com` / `salah123` | Form email terbuka    | Pesan login gagal tampil        | Login email menggunakan kredensial tidak valid                       |
| TC-LOGIN-09 | EP9       | Klik Lupa kata sandi           | Form email terbuka    | Halaman lupa kata sandi terbuka | User membuka halaman lupa kata sandi                                 |


## Mapping ke Automation

| Test Case   | Feature File    | Tag                          |
| ----------- | --------------- | ---------------------------- |
| TC-LOGIN-01 | `login.feature` | `@login @whatsapp @positive` |
| TC-LOGIN-02 | `login.feature` | `@login @whatsapp @positive` |
| TC-LOGIN-03 | `login.feature` | `@login @whatsapp @negative` |
| TC-LOGIN-04 | `login.feature` | `@login @email @positive`    |
| TC-LOGIN-05 | `login.feature` | `@login @email @positive`    |
| TC-LOGIN-06 | `login.feature` | `@login @email @negative`    |
| TC-LOGIN-07 | `login.feature` | `@login @email @negative`    |
| TC-LOGIN-08 | `login.feature` | `@login @email @negative`    |
| TC-LOGIN-09 | `login.feature` | `@login @email @positive`    |
