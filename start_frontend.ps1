<#
.SYNOPSIS
    PestRisk - Frontend Dev Server Launcher (Windows PowerShell)

.DESCRIPTION
    Starts the React + Vite development server on port 5173.
    
    SAFETY & VALIDATION:
    - Verifies Node.js and npm are present.
    - Validates frontend dependencies (node_modules).
    - Starts the Vite development server.
#>

$ErrorActionPreference = "Stop"

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "          PestRisk Frontend Dev Server Launcher          " -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

# 1. Check Node.js and npm
$nodeCmd = Get-Command node -ErrorAction SilentlyContinue
$npmCmd = Get-Command npm -ErrorAction SilentlyContinue

if (-not $nodeCmd -or -not $npmCmd) {
    Write-Error "Node.js and npm are required to run the frontend. Please ensure Node.js is installed and in your PATH."
    exit 1
}

$nodeVersion = node -v
Write-Host "[+] Node.js detected: $nodeVersion" -ForegroundColor Green

# 2. Check node_modules in frontend directory
$frontendDir = "D:\uhv\frontend"
if (-not (Test-Path "$frontendDir\node_modules")) {
    Write-Warning "node_modules not found in $frontendDir. Please run 'npm install' in $frontendDir first."
    exit 1
}

Set-Location $frontendDir

Write-Host "----------------------------------------------------------" -ForegroundColor Cyan
Write-Host "Starting Vite dev server on http://localhost:5173..." -ForegroundColor Cyan
Write-Host "Press Ctrl+C to stop the frontend." -ForegroundColor Gray
Write-Host "----------------------------------------------------------" -ForegroundColor Cyan

npm run dev
