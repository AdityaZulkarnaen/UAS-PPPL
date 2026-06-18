<#
.SYNOPSIS
  Automate generation of report (bug reporting) PER SUITE FITUR.

.DESCRIPTION
  Membaca hasil eksekusi Cucumber (target/cucumber-reports/cucumber.json) lalu
  menghasilkan 3 laporan terpisah - satu per suite fitur:

    1. Upload Data                        -> docs/bug-report-upload-data.md
    2. Login & Visualisasi Data Jaringan  -> docs/bug-report-login-visualisasi.md
    3. Kirim Aduan (Lapor Masalah)        -> docs/bug-report-kirim-aduan.md

  Tiap laporan berisi:
    - Ringkasan output suite (total test case, passed, failed, pass rate)
    - Tabel hasil pengujian (Expected vs Actual per test case)
    - Daftar bug (hanya skenario yang gagal) memakai template bug report

  Suite yang TIDAK ada di cucumber.json pada run terkini akan dilewati (report
  lama dipertahankan), sehingga suite bisa dijalankan satu per satu.

  Cara pakai (setelah `mvn clean verify`):
      powershell -ExecutionPolicy Bypass -File scripts\generate-bug-report.ps1
#>

$ErrorActionPreference = "Stop"

$root       = Split-Path -Parent $PSScriptRoot
$jsonPath   = Join-Path $root "target\cucumber-reports\cucumber.json"
$docsDir    = Join-Path $root "docs\Bug Report"
$baseUrl    = "https://bpalpjk.madanateknologi.web.id"
$timestamp  = Get-Date -Format "yyyy-MM-dd HH:mm:ss"

if (-not (Test-Path $jsonPath)) {
    Write-Error "cucumber.json tidak ditemukan di $jsonPath. Jalankan 'mvn clean verify' terlebih dahulu."
}

$rawJson = Get-Content $jsonPath -Raw -Encoding UTF8
if ([string]::IsNullOrWhiteSpace($rawJson)) {
    Write-Error "cucumber.json kosong di $jsonPath. Jalankan 'mvn clean verify' terlebih dahulu."
}

$features = $rawJson | ConvertFrom-Json
if ($features -isnot [System.Array]) { $features = @($features) }

# ---------------------------------------------------------------------------
# Definisi 3 suite fitur. 'FilePatterns' dicocokkan terhadap nama file .feature
# pada properti uri di cucumber.json. Suite 'aduan_management' SENGAJA tidak
# disertakan karena merupakan suite terpisah yang sudah punya laporan sendiri.
# ---------------------------------------------------------------------------
$suites = @(
    [pscustomobject]@{
        Key          = "upload-data"
        Title        = "Upload Data Jaringan (GeoJSON)"
        Komponen     = "Upload Jaringan IPAL (/ipal/upload)"
        FilePatterns = @("upload_jaringan")
        OutputFile   = "bug-report-upload-data.md"
        BugPrefix    = "UPL"
    },
    [pscustomobject]@{
        Key          = "login-visualisasi"
        Title        = "Login & Visualisasi Data Jaringan"
        Komponen     = "Login BPALPJK + Visualisasi Peta Jaringan/Manhole (/ipal/map)"
        FilePatterns = @("login", "visitor-map")
        OutputFile   = "bug-report-login-visualisasi.md"
        BugPrefix    = "LGV"
    },
    [pscustomobject]@{
        Key          = "kirim-aduan"
        Title        = "Kirim Aduan (Lapor Masalah Manhole)"
        Komponen     = "Lapor Masalah Aset Manhole / Pengaduan Publik (/ipal/map)"
        FilePatterns = @("lapor_masalah_manhole")
        OutputFile   = "bug-report-kirim-aduan.md"
        BugPrefix    = "ADU"
    },
    [pscustomobject]@{
        Key          = "manajemen-aduan"
        Title        = "Manajemen Aduan IPAL"
        Komponen     = "Manajemen Aduan IPAL (/ipal/aduan dan /ipal/aduan/{id})"
        FilePatterns = @("aduan_management")
        OutputFile   = "bug-report-manajemen-aduan.md"
        BugPrefix    = "MGR"
    }
)

function Get-FeatureFileName($uri) {
    if (-not $uri) { return "" }
    $clean = $uri -replace "\\", "/"
    return ($clean -split "/")[-1]
}

# Kumpulkan teks langkah Then/And/But sebagai Hasil Diharapkan (Expected).
function Get-ExpectedText($element) {
    $expected = New-Object System.Collections.Generic.List[string]
    $inThen = $false
    foreach ($step in $element.steps) {
        $kw = ($step.keyword).Trim()
        if ($kw -eq "Then") { $inThen = $true }
        elseif ($kw -eq "Given" -or $kw -eq "When") { $inThen = $false }
        if ($inThen -and ($kw -eq "Then" -or $kw -eq "And" -or $kw -eq "But")) {
            $expected.Add(($step.keyword + $step.name).Trim())
        }
    }
    if ($expected.Count -eq 0) { return "(tidak ada langkah Then eksplisit)" }
    return ($expected -join "; ")
}

# Kumpulkan langkah sebagai Langkah Reproduksi.
function Get-StepsText($element) {
    $lines = New-Object System.Collections.Generic.List[string]
    $i = 1
    foreach ($step in $element.steps) {
        $kw = $step.keyword.Trim()
        if ($kw -eq "After" -or $kw -eq "Before") { continue }
        $lines.Add(("{0}. {1} {2}" -f $i, $kw, $step.name))
        $i++
    }
    return ($lines -join "`n")
}

# Tentukan status skenario + pesan aktual.
function Get-ScenarioResult($element) {
    $status = "passed"
    $failedStep = $null
    $errorMsg = $null
    foreach ($step in $element.steps) {
        $st = $step.result.status
        if ($st -eq "failed") {
            $status = "failed"
            $failedStep = ($step.keyword + $step.name).Trim()
            $errorMsg = $step.result.error_message
            break
        }
        elseif ($st -eq "undefined" -or $st -eq "pending" -or $st -eq "ambiguous") {
            if ($status -eq "passed") { $status = $st }
        }
    }
    return [pscustomobject]@{
        Status     = $status
        FailedStep = $failedStep
        ErrorMsg   = $errorMsg
    }
}

function Format-Cell($text) {
    if (-not $text) { return "" }
    $t = $text -replace "\|", "\|"
    $t = $t -replace "\r?\n", " "
    return $t
}

$summaryLines = New-Object System.Collections.Generic.List[string]

foreach ($suite in $suites) {
    $suiteFeatures = $features | Where-Object {
        $fname = Get-FeatureFileName $_.uri
        $match = $false
        foreach ($pat in $suite.FilePatterns) {
            if ($fname -like "*$pat*") { $match = $true }
        }
        $match
    }

    # Deteksi suite multi-feature yang datanya PARSIAL: bila sebagian feature file
    # milik suite ini tidak ada di run terkini, jangan timpa report (agar tidak
    # kehilangan data feature lain). Mis. suite Login & Visualisasi butuh
    # login.feature DAN visitor-map.feature; run hanya @map akan parsial.
    $patternsPresent = 0
    foreach ($pat in $suite.FilePatterns) {
        $hit = $features | Where-Object { (Get-FeatureFileName $_.uri) -like "*$pat*" }
        if ($hit) { $patternsPresent++ }
    }
    if ($patternsPresent -gt 0 -and $patternsPresent -lt $suite.FilePatterns.Count) {
        $tags = ($suite.FilePatterns -join " + ")
        Write-Host "Dilewati: suite '$($suite.Title)' datanya PARSIAL ($patternsPresent/$($suite.FilePatterns.Count) feature). Jalankan semua feature suite ini ($tags) agar report lengkap. Report lama dipertahankan."
        $summaryLines.Add("  - [$($suite.Title)] DILEWATI - data parsial, jalankan semua feature suite ini")
        continue
    }

    $total  = 0
    $passed = 0
    $failed = 0
    $resultRows = New-Object System.Collections.Generic.List[string]
    $bugBlocks  = New-Object System.Collections.Generic.List[string]
    $bugRows    = New-Object System.Collections.Generic.List[string]
    $no = 0
    $bugNo = 0

    foreach ($feature in $suiteFeatures) {
        foreach ($element in $feature.elements) {
            if ($element.type -ne "scenario") { continue }
            $total++
            $no++

            $result   = Get-ScenarioResult $element
            $expected = Get-ExpectedText $element
            $scenName = $element.name

            if ($result.Status -eq "passed") {
                $passed++
                $statusCell = "PASS"
                $actual = "Sesuai harapan - seluruh langkah lulus."
            } else {
                $failed++
                $statusCell = "FAIL"
                if ($result.Status -eq "failed") {
                    if ($result.ErrorMsg) {
                        $firstLine = (($result.ErrorMsg -split "`n")[0]).Trim()
                    } else {
                        $firstLine = "tidak ada pesan error"
                    }
                    $actual = "Gagal di langkah '$($result.FailedStep)' -> $firstLine"
                } else {
                    $actual = "Skenario berstatus '$($result.Status)' (step belum terdefinisi/pending)."
                }
            }

            $cName = Format-Cell $scenName
            $cExp  = Format-Cell $expected
            $cAct  = Format-Cell $actual
            $resultRows.Add("| $no | $cName | $cExp | $cAct | $statusCell |")

            if ($result.Status -ne "passed") {
                $bugNo++
                $bugId = "BUG-$($suite.BugPrefix)-{0:D2}" -f $bugNo
                $bugRows.Add("| $bugId | $cName | $cAct |")

                $stepsText = Get-StepsText $element
                if ($result.ErrorMsg) {
                    $errBlock = $result.ErrorMsg.Trim()
                } else {
                    $errBlock = "(tidak ada pesan error; status: $($result.Status))"
                }

                $bugBlocks.Add("#### $bugId - $scenName")
                $bugBlocks.Add("")
                $bugBlocks.Add("| Field | Isi |")
                $bugBlocks.Add("|---|---|")
                $bugBlocks.Add("| **Bug ID** | $bugId |")
                $bugBlocks.Add("| **Judul** | $cName |")
                $bugBlocks.Add("| **Severity** | _perlu ditinjau (Critical/Major/Minor/Trivial)_ |")
                $bugBlocks.Add("| **Priority** | _perlu ditinjau (High/Medium/Low)_ |")
                $bugBlocks.Add("| **Komponen** | $($suite.Komponen) |")
                $bugBlocks.Add("| **Environment** | Chrome (Selenium headless), Windows 11, $baseUrl |")
                $bugBlocks.Add("| **Hasil Diharapkan** | $cExp |")
                $bugBlocks.Add("| **Hasil Aktual** | $cAct |")
                $bugBlocks.Add("| **Status** | Open |")
                $bugBlocks.Add("")
                $bugBlocks.Add("**Langkah Reproduksi:**")
                $bugBlocks.Add('```gherkin')
                $bugBlocks.Add($stepsText)
                $bugBlocks.Add('```')
                $bugBlocks.Add("")
                $bugBlocks.Add("**Detail Error:**")
                $bugBlocks.Add('```')
                $bugBlocks.Add($errBlock)
                $bugBlocks.Add('```')
                $bugBlocks.Add("")
            }
        }
    }

    # Suite tidak dijalankan pada run terkini -> jangan timpa report lama.
    if ($total -eq 0) {
        $existing = Join-Path $docsDir $suite.OutputFile
        if (Test-Path $existing) { $note = "(report lama dipertahankan)" } else { $note = "(belum ada report)" }
        Write-Host "Dilewati: suite '$($suite.Title)' tidak ada di cucumber.json $note"
        $summaryLines.Add("  - [$($suite.Title)] DILEWATI - tidak dijalankan pada run ini $note")
        continue
    }

    $passRate = [math]::Round(($passed / $total) * 100, 1)

    $sb = New-Object System.Text.StringBuilder
    [void]$sb.AppendLine("# Bug Report - Suite $($suite.Title)")
    [void]$sb.AppendLine("")
    [void]$sb.AppendLine("> **Dibuat otomatis** oleh ``scripts/generate-bug-report.ps1`` - jangan diedit manual.")
    [void]$sb.AppendLine("> Sumber data: ``target/cucumber-reports/cucumber.json`` | Waktu generate: $timestamp")
    [void]$sb.AppendLine("")
    [void]$sb.AppendLine("| Info | Nilai |")
    [void]$sb.AppendLine("|---|---|")
    [void]$sb.AppendLine("| **Aplikasi** | BPAL PJK IPAL - $baseUrl |")
    [void]$sb.AppendLine("| **Komponen** | $($suite.Komponen) |")
    [void]$sb.AppendLine("| **Environment** | Chrome (Selenium headless), Windows 11 |")
    [void]$sb.AppendLine("")
    [void]$sb.AppendLine("## 1. Ringkasan Hasil Pengujian")
    [void]$sb.AppendLine("")
    [void]$sb.AppendLine("| Metrik | Nilai |")
    [void]$sb.AppendLine("|---|---|")
    [void]$sb.AppendLine("| Total Test Case | $total |")
    [void]$sb.AppendLine("| Passed | $passed |")
    [void]$sb.AppendLine("| Failed | $failed |")
    [void]$sb.AppendLine("| Pass Rate | $passRate% |")
    [void]$sb.AppendLine("")
    [void]$sb.AppendLine("## 2. Daftar Hasil Pengujian (Expected vs Actual)")
    [void]$sb.AppendLine("")
    [void]$sb.AppendLine("| No | Test Case (Skenario) | Hasil Diharapkan (Expected) | Hasil Aktual (Actual) | Status |")
    [void]$sb.AppendLine("|---|---|---|---|---|")
    foreach ($r in $resultRows) { [void]$sb.AppendLine($r) }
    [void]$sb.AppendLine("")
    [void]$sb.AppendLine("## 3. Daftar Bug Ditemukan")
    [void]$sb.AppendLine("")
    if ($failed -eq 0) {
        [void]$sb.AppendLine("Tidak ada skenario yang gagal. Tidak ada bug ditemukan pada suite ini.")
        [void]$sb.AppendLine("")
    } else {
        [void]$sb.AppendLine("Ditemukan **$failed** skenario gagal - kandidat bug:")
        [void]$sb.AppendLine("")
        [void]$sb.AppendLine("| Bug ID | Test Case | Ringkasan Hasil Aktual |")
        [void]$sb.AppendLine("|---|---|---|")
        foreach ($r in $bugRows) { [void]$sb.AppendLine($r) }
        [void]$sb.AppendLine("")
        [void]$sb.AppendLine("### Detail Bug")
        [void]$sb.AppendLine("")
        foreach ($b in $bugBlocks) { [void]$sb.AppendLine($b) }
        [void]$sb.AppendLine("> Tinjau tiap entri: bila merupakan cacat aplikasi (bukan masalah environment/")
        [void]$sb.AppendLine("> locator), tetapkan Severity & Priority lalu laporkan resmi ke tim pengembang.")
    }

    $outPath = Join-Path $docsDir $suite.OutputFile
    Set-Content -Path $outPath -Value $sb.ToString() -Encoding utf8

    $summaryLines.Add("  - [$($suite.Title)] $($suite.OutputFile): total=$total passed=$passed failed=$failed ($passRate%)")
    Write-Host "Report dibuat: docs/$($suite.OutputFile)  (total=$total, passed=$passed, failed=$failed)"
}

Write-Host ""
Write-Host "=== Ringkasan generate-bug-report ($timestamp) ==="
foreach ($l in $summaryLines) { Write-Host $l }
