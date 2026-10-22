<#
.SYNOPSIS
    PestRisk - Dual Process Launcher (Windows PowerShell)

.DESCRIPTION
    Convenience root launcher that opens separate PowerShell windows for:
    1. Spring Boot Backend (http://localhost:8080)
    2. React + Vite Frontend (http://localhost:5173)
#>

$ErrorActionPreference = "Stop"

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "         PestRisk Full Application Launcher              " -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

$baseDir = "D:\uhv"

# 1. Launch Backend in separate window
$backendScript = "$baseDir\start_backend.ps1"
if (Test-Path $backendScript) {
    Write-Host "[+] Launching Backend Server in new console..." -ForegroundColor Green
    Start-Process powershell -ArgumentList "-NoExit", "-ExecutionPolicy", "Bypass", "-File", "`"$backendScript`""
} else {
    Write-Error "Backend launch script not found at $backendScript"
    exit 1
}

# 2. Launch Frontend in separate window
$frontendScript = "$baseDir\start_frontend.ps1"
if (Test-Path $frontendScript) {
    Write-Host "[+] Launching Frontend Server in new console..." -ForegroundColor Green
    Start-Process powershell -ArgumentList "-NoExit", "-ExecutionPolicy", "Bypass", "-File", "`"$frontendScript`""
} else {
    Write-Error "Frontend launch script not found at $frontendScript"
    exit 1
}

Write-Host "----------------------------------------------------------" -ForegroundColor Cyan
Write-Host "Processes launched:" -ForegroundColor Cyan
Write-Host "  • Backend API:      http://localhost:8080/api/health" -ForegroundColor Yellow
Write-Host "  • Resident Portal:  http://localhost:5173/#resident" -ForegroundColor Yellow
Write-Host "  • Staff Operations: http://localhost:5173/#staff" -ForegroundColor Yellow
Write-Host "==========================================================" -ForegroundColor Cyan
