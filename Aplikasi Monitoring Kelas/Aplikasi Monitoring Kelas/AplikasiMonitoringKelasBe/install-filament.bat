@echo off
REM 🎨 Script Instalasi Filament untuk Aplikasi Monitoring Kelas (Windows)
REM Kompatibel dengan Laravel 12

echo ==========================================
echo 🚀 Instalasi Filament Admin Panel
echo ==========================================
echo.

REM Check if composer is installed
where composer >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Composer tidak ditemukan. Install composer terlebih dahulu.
    pause
    exit /b 1
)

REM Check if we're in Laravel project
if not exist "artisan" (
    echo ❌ File artisan tidak ditemukan. Pastikan Anda berada di root folder Laravel.
    pause
    exit /b 1
)

echo 📦 Step 1: Installing Filament package...
call composer require filament/filament:"^3.2" -W

if %ERRORLEVEL% NEQ 0 (
    echo ❌ Gagal menginstall Filament package
    pause
    exit /b 1
)

echo.
echo ✅ Filament package berhasil diinstall!
echo.

echo 🔧 Step 2: Installing Filament panel...
call php artisan filament:install --panels

if %ERRORLEVEL% NEQ 0 (
    echo ❌ Gagal menginstall Filament panel
    pause
    exit /b 1
)

echo.
echo ✅ Filament panel berhasil diinstall!
echo.

echo 💾 Step 3: Running migrations...
call php artisan migrate

if %ERRORLEVEL% NEQ 0 (
    echo ⚠️  Warning: Migration gagal. Mungkin sudah dijalankan sebelumnya.
)

echo.
echo 🧹 Step 4: Clearing cache...
call php artisan optimize:clear
call php artisan config:cache

echo.
echo ==========================================
echo ✅ Instalasi Filament Selesai!
echo ==========================================
echo.
echo 📝 Langkah Selanjutnya:
echo.
echo 1. Buat admin user dengan perintah:
echo    php artisan make:filament-user
echo.
echo 2. Generate resources dengan perintah:
echo    php artisan make:filament-resource User --generate
echo    php artisan make:filament-resource Schedule --generate
echo    php artisan make:filament-resource Monitoring --generate
echo    php artisan make:filament-resource TeacherAttendance --generate
echo    php artisan make:filament-resource GuruPengganti --generate
echo.
echo 3. Jalankan development server:
echo    php artisan serve
echo.
echo 4. Akses admin panel di:
echo    http://localhost:8000/admin
echo.
echo 📚 Dokumentasi lengkap: Lihat file FILAMENT_SETUP.md
echo.
pause
