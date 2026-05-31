<#
.SYNOPSIS
  Generates a Markdown summary of FAILED Cucumber scenarios from cucumber.json
  and injects it into docs/bug-report.md between the AUTO-SUMMARY markers.

.DESCRIPTION
  Part of the "automate generation of report" requirement. Run after `mvn verify`:
      powershell -ExecutionPolicy Bypass -File scripts\generate-bug-summary.ps1
#>

$ErrorActionPreference = "Stop"

$root        = Split-Path -Parent $PSScriptRoot
$jsonPath    = Join-Path $root "target\cucumber-reports\cucumber.json"
$reportPath  = Join-Path $root "docs\bug-report.md"
$startMarker = "<!-- AUTO-SUMMARY:START -->"
$endMarker   = "<!-- AUTO-SUMMARY:END -->"

if (-not (Test-Path $jsonPath)) {
    Write-Error "cucumber.json not found at $jsonPath. Run 'mvn clean verify' first."
}

$features = Get-Content $jsonPath -Raw | ConvertFrom-Json

$total  = 0
$failed = 0
$rows   = New-Object System.Collections.Generic.List[string]
$details = New-Object System.Collections.Generic.List[string]

foreach ($feature in $features) {
    foreach ($element in $feature.elements) {
        # Only count scenarios (skip backgrounds).
        if ($element.type -ne "scenario") { continue }
        $total++

        $errorMsg = $null
        foreach ($step in $element.steps) {
            if ($step.result.status -eq "failed") {
                $errorMsg = $step.result.error_message
                break
            }
        }

        if ($errorMsg) {
            $failed++
            $name = $element.name
            # First line of the error for the table; full error in details.
            $firstLine = ($errorMsg -split "`n")[0].Trim()
            $rows.Add("| BUG-AUTO-$failed | $($feature.name) | $name | $firstLine |")
            $details.Add("#### BUG-AUTO-$failed — $name")
            $details.Add("**Feature:** $($feature.name)")
            $details.Add('```')
            $details.Add($errorMsg.Trim())
            $details.Add('```')
            $details.Add("")
        }
    }
}

$timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
$sb = New-Object System.Text.StringBuilder
[void]$sb.AppendLine($startMarker)
[void]$sb.AppendLine("")
[void]$sb.AppendLine("_Dibuat otomatis: $timestamp_")
[void]$sb.AppendLine("")
[void]$sb.AppendLine("**Total skenario:** $total | **Gagal:** $failed | **Lulus:** $($total - $failed)")
[void]$sb.AppendLine("")

if ($failed -eq 0) {
    [void]$sb.AppendLine("Tidak ada skenario yang gagal. Tidak ada kandidat bug otomatis.")
} else {
    [void]$sb.AppendLine("| Bug ID | Feature | Skenario Gagal | Pesan Error (ringkas) |")
    [void]$sb.AppendLine("|---|---|---|---|")
    foreach ($r in $rows) { [void]$sb.AppendLine($r) }
    [void]$sb.AppendLine("")
    [void]$sb.AppendLine("### Detail Error")
    [void]$sb.AppendLine("")
    foreach ($d in $details) { [void]$sb.AppendLine($d) }
    [void]$sb.AppendLine("")
    [void]$sb.AppendLine("> Tinjau tiap entri di atas; yang merupakan cacat aplikasi (bukan masalah")
    [void]$sb.AppendLine("> environment/locator) dokumentasikan sebagai bug resmi memakai template di atas.")
}
[void]$sb.AppendLine("")
[void]$sb.Append($endMarker)

$content = Get-Content $reportPath -Raw
$pattern = "(?s)" + [regex]::Escape($startMarker) + ".*?" + [regex]::Escape($endMarker)
$updated = [regex]::Replace($content, $pattern, [System.Text.RegularExpressions.MatchEvaluator]{ param($m) $sb.ToString() })

Set-Content -Path $reportPath -Value $updated -Encoding utf8

Write-Host "Bug summary updated: $reportPath"
Write-Host "Scenarios: $total total, $failed failed."
