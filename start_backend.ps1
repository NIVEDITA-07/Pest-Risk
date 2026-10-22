<#
.SYNOPSIS
    PestRisk - Backend Startup Script (Windows PowerShell)

.DESCRIPTION
    Launches the Spring Boot backend server on port 8080 with persistent SQLite storage.
    
    SECURITY & CONFIGURATION:
    - Checks for PESTRISK_JWT_SECRET environment variable (minimum 32 characters / 256 bits).
    - If not set in the environment, generates a secure random secret in memory for this session.
    - Never writes or logs the JWT secret.
    - Preserves D:\uhv\data\pestrisk.db.
#>

$ErrorActionPreference = "Stop"

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "          PestRisk Backend Server Launcher               " -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

# 1. Locate Java 21
if (-not $env:JAVA_HOME -or -not (Test-Path "$env:JAVA_HOME\bin\java.exe")) {
    if (Test-Path "D:\tools\jdk-21\bin\java.exe") {
        $env:JAVA_HOME = "D:\tools\jdk-21"
        $env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
        Write-Host "[+] Using JDK 21 at: $env:JAVA_HOME" -ForegroundColor Green
    } else {
        $systemJava = Get-Command java -ErrorAction SilentlyContinue
        if ($systemJava) {
            Write-Host "[+] Using system Java from: $($systemJava.Source)" -ForegroundColor Green
        } else {
            Write-Error "Java 21 is required but not found. Please set JAVA_HOME or ensure Java 21 is in your PATH."
            exit 1
        }
    }
} else {
    Write-Host "[+] JAVA_HOME detected: $env:JAVA_HOME" -ForegroundColor Green
}

# 2. Add Maven to PATH if available in D:\tools\maven\bin
if (Test-Path "D:\tools\maven\bin") {
    $env:PATH = "D:\tools\maven\bin;$env:PATH"
}

# 3. Ensure data directory exists (preserves existing pestrisk.db)
$dataDir = "D:\uhv\data"
if (-not (Test-Path $dataDir)) {
    New-Item -ItemType Directory -Path $dataDir -Force | Out-Null
    Write-Host "[+] Created data directory at: $dataDir" -ForegroundColor Green
} else {
    Write-Host "[+] Persistent data directory found at: $dataDir" -ForegroundColor Green
}

# 4. Secure JWT Secret Validation & In-Memory Generation
if (-not $env:PESTRISK_JWT_SECRET -or $env:PESTRISK_JWT_SECRET.Trim().Length -lt 32) {
    Write-Host "[!] PESTRISK_JWT_SECRET not provided via environment." -ForegroundColor Yellow
    Write-Host "[+] Generating cryptographically secure 256-bit secret in memory for this session..." -ForegroundColor Yellow
    $bytes = New-Object byte[] 32
    [System.Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
    $env:PESTRISK_JWT_SECRET = [System.BitConverter]::ToString($bytes).Replace("-", "").ToLower()
    Write-Host "[+] Secure secret loaded in memory (value suppressed)." -ForegroundColor Green
    Write-Host "    Tip: To persist across sessions, set `$env:PESTRISK_JWT_SECRET in your environment." -ForegroundColor Gray
} else {
    Write-Host "[+] PESTRISK_JWT_SECRET loaded from environment (length: $($env:PESTRISK_JWT_SECRET.Length) chars)." -ForegroundColor Green
}

# 5. Launch Backend via Maven Wrapper
$backendDir = "D:\uhv\backend"
Set-Location $backendDir

Write-Host "----------------------------------------------------------" -ForegroundColor Cyan
Write-Host "Starting Spring Boot application on http://localhost:8080..." -ForegroundColor Cyan
Write-Host "Press Ctrl+C to stop the backend." -ForegroundColor Gray
Write-Host "----------------------------------------------------------" -ForegroundColor Cyan

& ".\mvnw.cmd" spring-boot:run
