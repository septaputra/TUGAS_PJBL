#!/bin/bash

# 🎨 Script Instalasi Filament untuk Aplikasi Monitoring Kelas
# Kompatibel dengan Laravel 12

echo "=========================================="
echo "🚀 Instalasi Filament Admin Panel"
echo "=========================================="
echo ""

# Check if composer is installed
if ! command -v composer &> /dev/null
then
    echo "❌ Composer tidak ditemukan. Install composer terlebih dahulu."
    exit 1
fi

# Check if we're in Laravel project
if [ ! -f "artisan" ]; then
    echo "❌ File artisan tidak ditemukan. Pastikan Anda berada di root folder Laravel."
    exit 1
fi

echo "📦 Step 1: Installing Filament package..."
composer require filament/filament:"^3.2" -W

if [ $? -ne 0 ]; then
    echo "❌ Gagal menginstall Filament package"
    exit 1
fi

echo ""
echo "✅ Filament package berhasil diinstall!"
echo ""

echo "🔧 Step 2: Installing Filament panel..."
php artisan filament:install --panels

if [ $? -ne 0 ]; then
    echo "❌ Gagal menginstall Filament panel"
    exit 1
fi

echo ""
echo "✅ Filament panel berhasil diinstall!"
echo ""

echo "💾 Step 3: Running migrations..."
php artisan migrate

if [ $? -ne 0 ]; then
    echo "⚠️  Warning: Migration gagal. Mungkin sudah dijalankan sebelumnya."
fi

echo ""
echo "🧹 Step 4: Clearing cache..."
php artisan optimize:clear
php artisan config:cache

echo ""
echo "=========================================="
echo "✅ Instalasi Filament Selesai!"
echo "=========================================="
echo ""
echo "📝 Langkah Selanjutnya:"
echo ""
echo "1. Buat admin user dengan perintah:"
echo "   php artisan make:filament-user"
echo ""
echo "2. Generate resources dengan perintah:"
echo "   php artisan make:filament-resource User --generate"
echo "   php artisan make:filament-resource Schedule --generate"
echo "   php artisan make:filament-resource Monitoring --generate"
echo "   php artisan make:filament-resource TeacherAttendance --generate"
echo "   php artisan make:filament-resource GuruPengganti --generate"
echo ""
echo "3. Jalankan development server:"
echo "   php artisan serve"
echo ""
echo "4. Akses admin panel di:"
echo "   http://localhost:8000/admin"
echo ""
echo "📚 Dokumentasi lengkap: Lihat file FILAMENT_SETUP.md"
echo ""
