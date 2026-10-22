@echo off
title PestRisk - Village Waste Hotspot Prioritization Platform
cd /d "%~dp0"

echo ==========================================================
echo       PestRisk Full Application Quick Launcher
echo   (Universal Human Values Project - Vadama Village)
echo ==========================================================
echo.
echo Launching Spring Boot Backend (Port 8080)...
echo Launching React + Vite Frontend (Port 5173)...
echo.

powershell -NoProfile -ExecutionPolicy Bypass -File ".\start_all.ps1"

echo.
echo ==========================================================
echo Both servers have been launched in separate consoles!
echo Access the application in your browser:
echo   Resident Portal:  http://localhost:5173/#resident
echo   Staff Dashboard:  http://localhost:5173/#staff
echo ==========================================================
pause
