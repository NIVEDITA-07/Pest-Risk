<#
.SYNOPSIS
    PestRisk - Optional Demonstration Data Seeding Script (Explicitly Invoked)

.DESCRIPTION
    Seeds fictional demonstration waste reports across various risk levels (LOW, MODERATE, HIGH, CRITICAL)
    and lifecycle statuses (REPORTED, VERIFIED, IN_PROGRESS, RESOLVED) for demonstration and evaluation purposes.
    
    SAFETY & INTEGRITY GUARDS:
    - Never runs automatically at application startup.
    - Idempotent: Checks for existing demo reports to prevent duplicates on repeated runs.
    - Uses purely fictional ward names and labels all records as [DEMO DATA ONLY].
    - Does not claim actual municipal adoption or fabricate real public health incidents.

.PARAMETER BaseUrl
    Base URL for the PestRisk backend. Defaults to http://localhost:8080.
#>

param(
    [string]$BaseUrl = "http://localhost:8080"
)

$ErrorActionPreference = "Stop"

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "  PestRisk Demo Data Seeder (Fictional Simulation Data)  " -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

# 1. Check Backend Connectivity
try {
    $health = Invoke-RestMethod -Uri "$BaseUrl/api/health" -TimeoutSec 5
    if ($health.status -ne "UP") {
        Write-Error "Backend at $BaseUrl reported non-UP status."
        exit 1
    }
    Write-Host "[+] Connected to PestRisk backend (Status: UP)" -ForegroundColor Green
} catch {
    Write-Error "Could not connect to PestRisk backend at $BaseUrl. Please ensure the backend is running first."
    exit 1
}

# 2. Staff Authentication (to inspect and manage demo reports)
$staffUser = "STAFF-101"
$staffPass = "StaffPassword123"

try {
    $loginRes = Invoke-RestMethod -Uri "$BaseUrl/api/auth/login" -Method Post `
        -Body (@{ username = $staffUser; password = $staffPass } | ConvertTo-Json) `
        -ContentType "application/json"
    $headers = @{ Authorization = "Bearer $($loginRes.token)" }
    Write-Host "[+] Authenticated as $staffUser for demo verification" -ForegroundColor Green
} catch {
    # Fallback to staff_kavita if STAFF-101 not present
    try {
        $loginRes = Invoke-RestMethod -Uri "$BaseUrl/api/auth/login" -Method Post `
            -Body (@{ username = "staff_kavita"; password = "StaffPassword123" } | ConvertTo-Json) `
            -ContentType "application/json"
        $headers = @{ Authorization = "Bearer $($loginRes.token)" }
        Write-Host "[+] Authenticated as staff_kavita for demo verification" -ForegroundColor Green
    } catch {
        Write-Warning "Could not authenticate as staff. Seeding will proceed via public reporting API only."
        $headers = $null
    }
}

# 3. Check for existing demo reports to ensure idempotency
if ($headers) {
    $existingReports = Invoke-RestMethod -Uri "$BaseUrl/api/reports" -Headers $headers
    $existingDemoWards = $existingReports | Where-Object { $_.ward -like "Demo Ward *" } | Select-Object -ExpandProperty ward
} else {
    $existingDemoWards = @()
}

# 4. Fictional Demo Datasets
$demoItems = @(
    @{
        ward = "Demo Ward 1 - Fictional Riverside"
        locationDescription = "[DEMO SIMULATION] Fictional organic compost heap near community garden lane"
        wasteQuantity = "LOW"
        wasteDurationDays = 1
        stagnantWater = $false
        mosquitoSightings = $false
        rodentSightings = $false
        strayAnimalSightings = $false
        unsegregatedWaste = $false
        targetStatus = "REPORTED"
    },
    @{
        ward = "Demo Ward 2 - Fictional Hillside"
        locationDescription = "[DEMO SIMULATION] Fictional dry leaf and carton pile behind community center"
        wasteQuantity = "MEDIUM"
        wasteDurationDays = 3
        stagnantWater = $false
        mosquitoSightings = $false
        rodentSightings = $true
        strayAnimalSightings = $false
        unsegregatedWaste = $true
        targetStatus = "VERIFIED"
        verifyRemarks = "Site inspected by demo sanitation supervisor."
    },
    @{
        ward = "Demo Ward 3 - Fictional Market"
        locationDescription = "[DEMO SIMULATION] Fictional damp vegetable waste near storm channel culvert"
        wasteQuantity = "MEDIUM"
        wasteDurationDays = 4
        stagnantWater = $true
        mosquitoSightings = $true
        rodentSightings = $false
        strayAnimalSightings = $true
        unsegregatedWaste = $true
        targetStatus = "IN_PROGRESS"
        verifyRemarks = "Site inspected and verified for cleanup."
        progressRemarks = "Demonstration cleanup crew dispatched."
    },
    @{
        ward = "Demo Ward 4 - Fictional Station"
        locationDescription = "[DEMO SIMULATION] Fictional prolonged mixed accumulation near old grain storage"
        wasteQuantity = "HIGH"
        wasteDurationDays = 6
        stagnantWater = $true
        mosquitoSightings = $true
        rodentSightings = $true
        strayAnimalSightings = $true
        unsegregatedWaste = $true
        targetStatus = "RESOLVED"
        verifyRemarks = "Site verified as critical hotspot."
        progressRemarks = "Remediation team deployed."
        resolveRemarks = "Waste removed, disinfectant applied, site secured."
    }
)

# 5. Seed Missing Demo Reports
$addedCount = 0
foreach ($item in $demoItems) {
    if ($existingDemoWards -contains $item.ward) {
        Write-Host "[-] Skipping '$($item.ward)' (Already seeded)" -ForegroundColor Yellow
        continue
    }

    $payload = @{
        ward = $item.ward
        locationDescription = $item.locationDescription
        wasteQuantity = $item.wasteQuantity
        wasteDurationDays = $item.wasteDurationDays
        stagnantWater = $item.stagnantWater
        mosquitoSightings = $item.mosquitoSightings
        rodentSightings = $item.rodentSightings
        strayAnimalSightings = $item.strayAnimalSightings
        unsegregatedWaste = $item.unsegregatedWaste
    } | ConvertTo-Json

    try {
        $created = Invoke-RestMethod -Uri "$BaseUrl/api/reports" -Method Post -Body $payload -ContentType "application/json"
        Write-Host "[+] Created Report #$($created.id) for $($item.ward) [Score: $($created.riskScore), Level: $($created.riskLevel)]" -ForegroundColor Green
        $addedCount++

        # Advance status if requested and authorized
        if ($headers -and $item.targetStatus -ne "REPORTED") {
            $id = $created.id
            if ($item.targetStatus -in @("VERIFIED", "IN_PROGRESS", "RESOLVED")) {
                $patchBody = @{ status = "VERIFIED"; remarks = $item.verifyRemarks } | ConvertTo-Json
                Invoke-RestMethod -Uri "$BaseUrl/api/reports/$id/status" -Method Patch -Body $patchBody -ContentType "application/json" -Headers $headers | Out-Null
            }
            if ($item.targetStatus -in @("IN_PROGRESS", "RESOLVED")) {
                $patchBody = @{ status = "IN_PROGRESS"; remarks = $item.progressRemarks } | ConvertTo-Json
                Invoke-RestMethod -Uri "$BaseUrl/api/reports/$id/status" -Method Patch -Body $patchBody -ContentType "application/json" -Headers $headers | Out-Null
            }
            if ($item.targetStatus -eq "RESOLVED") {
                $patchBody = @{ status = "RESOLVED"; remarks = $item.resolveRemarks } | ConvertTo-Json
                Invoke-RestMethod -Uri "$BaseUrl/api/reports/$id/status" -Method Patch -Body $patchBody -ContentType "application/json" -Headers $headers | Out-Null
            }
            Write-Host "    -> Status transitioned to $($item.targetStatus)" -ForegroundColor Gray
        }
    } catch {
        Write-Warning "Failed to create demo report for $($item.ward): $_"
    }
}

Write-Host "----------------------------------------------------------" -ForegroundColor Cyan
Write-Host "Demo data seeding complete. New items added: $addedCount" -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan
