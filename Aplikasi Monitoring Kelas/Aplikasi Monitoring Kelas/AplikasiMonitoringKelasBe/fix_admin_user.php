<?php

/**
 * Script untuk memperbaiki user admin
 * Jalankan dengan: php fix_admin_user.php
 */

require __DIR__.'/vendor/autoload.php';

$app = require_once __DIR__.'/bootstrap/app.php';
$app->make('Illuminate\Contracts\Console\Kernel')->bootstrap();

use App\Models\User;
use Illuminate\Support\Facades\Hash;

echo "🔧 Memperbaiki user admin...\n\n";

// Email admin yang ingin diperbaiki
$adminEmail = 'zupa.admin@sekolah.com';

// Cari user
$user = User::where('email', $adminEmail)->first();

if (!$user) {
    echo "❌ User dengan email {$adminEmail} tidak ditemukan!\n";
    echo "📝 Membuat user baru...\n\n";

    $user = User::create([
        'name' => 'Admin Sekolah',
        'email' => $adminEmail,
        'password' => Hash::make('password123'),
        'role' => 'admin',
        'email_verified_at' => now(),
        'is_banned' => false,
    ]);

    echo "✅ User baru berhasil dibuat!\n";
} else {
    echo "✅ User ditemukan: {$user->name}\n\n";
    echo "📝 Memperbarui user...\n";

    // Update user
    $user->update([
        'password' => Hash::make('password123'),
        'email_verified_at' => now(),
        'is_banned' => false,
        'role' => 'admin',
    ]);

    echo "✅ User berhasil diperbarui!\n";
}

echo "\n";
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n";
echo "📋 Informasi Login:\n";
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n";
echo "🌐 URL       : http://localhost:8000/admin\n";
echo "📧 Email     : {$adminEmail}\n";
echo "🔑 Password  : password123\n";
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n";
echo "\n✨ Selesai! Silakan coba login.\n";
