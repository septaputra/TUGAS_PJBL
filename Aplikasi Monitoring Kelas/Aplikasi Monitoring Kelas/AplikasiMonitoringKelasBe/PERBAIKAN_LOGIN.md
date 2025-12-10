# 🔐 PERBAIKAN MASALAH LOGIN FILAMENT

## ❌ Masalah yang Terjadi

Saat mencoba login dengan email yang ada di database (`zupa.admin@sekolah.com`), sistem tidak dapat mengenali kredensial yang benar.

## 🔍 Penyebab Masalah

1. **Password tidak di-hash dengan benar** - Password di database mungkin tidak menggunakan algoritma hash Laravel yang benar
2. **Email tidak terverifikasi** - Kolom `email_verified_at` bernilai `NULL`
3. **User mungkin di-banned** - Kolom `is_banned` mungkin bernilai `true`
4. **Role tidak sesuai** - Kolom `role` mungkin tidak tepat

## ✅ Solusi yang Telah Diterapkan

Telah dibuat script `fix_admin_user.php` yang akan:
- ✅ Mencari user berdasarkan email
- ✅ Mengupdate password dengan hash yang benar menggunakan `Hash::make()`
- ✅ Set `email_verified_at` ke waktu sekarang
- ✅ Set `is_banned` ke `false`
- ✅ Memastikan `role` adalah `admin`

## 🚀 Cara Menggunakan

### 1. Jalankan Script Perbaikan
```powershell
cd "D:\KELAS-XI\Tugas Video\PROJECT PELATIHAN JETPACK COMPOSE\Aplikasi Monitoring Kelas\AplikasiMonitoringKelasBe"
php fix_admin_user.php
```

### 2. Jalankan Server
```powershell
php artisan serve
```

### 3. Login ke Admin Panel
- **URL**: http://localhost:8000/admin
- **Email**: `zupa.admin@sekolah.com`
- **Password**: `password123`

## 🔧 Cara Membuat User Admin Baru

### Metode 1: Menggunakan Artisan Command
```powershell
php artisan make:filament-user
```

Input:
- **Name**: Nama Admin
- **Email**: email@domain.com
- **Password**: password_anda

### Metode 2: Menggunakan Tinker
```powershell
php artisan tinker
```

Kemudian:
```php
\App\Models\User::create([
    'name' => 'Admin Baru',
    'email' => 'admin@sekolah.com',
    'password' => bcrypt('password'),
    'role' => 'admin',
    'email_verified_at' => now(),
    'is_banned' => false,
]);
```

### Metode 3: Edit Script fix_admin_user.php
Edit file `fix_admin_user.php` dan ubah:
```php
$adminEmail = 'email_baru@sekolah.com'; // Ganti dengan email yang diinginkan
```

Lalu jalankan:
```powershell
php fix_admin_user.php
```

## 📝 Kolom Penting dalam Tabel Users

| Kolom | Tipe | Keterangan |
|-------|------|------------|
| `email` | string | Harus unique |
| `password` | string | **HARUS di-hash dengan `Hash::make()` atau `bcrypt()`** |
| `email_verified_at` | timestamp | Jika `NULL`, user mungkin tidak bisa login |
| `is_banned` | boolean | Jika `true`, user tidak bisa login |
| `role` | string | 'admin', 'guru', atau 'siswa' |

## ⚠️ Catatan Penting

### ❌ JANGAN Lakukan Ini:
```php
// ❌ Password tidak di-hash
User::create([
    'password' => 'password123', // SALAH!
]);
```

### ✅ LAKUKAN Ini:
```php
// ✅ Password di-hash dengan benar
User::create([
    'password' => Hash::make('password123'), // BENAR!
    // atau
    'password' => bcrypt('password123'), // BENAR!
]);
```

## 🐛 Troubleshooting

### Login Masih Gagal?
1. **Clear cache Laravel**:
   ```powershell
   php artisan config:clear
   php artisan cache:clear
   php artisan route:clear
   ```

2. **Cek di database** apakah `email_verified_at` tidak NULL:
   ```sql
   SELECT id, name, email, email_verified_at, is_banned, role 
   FROM users 
   WHERE email = 'zupa.admin@sekolah.com';
   ```

3. **Reset ulang password**:
   ```powershell
   php fix_admin_user.php
   ```

### Error "Too Many Attempts"
Jika terlalu banyak percobaan login gagal, tunggu beberapa menit atau clear cache:
```powershell
php artisan cache:clear
```

## 📚 Referensi

- [Laravel Authentication](https://laravel.com/docs/11.x/authentication)
- [Filament Admin Panel](https://filamentphp.com/docs/3.x/panels/installation)
- [Laravel Hashing](https://laravel.com/docs/11.x/hashing)

---

**Status**: ✅ **DIPERBAIKI**  
**Password Baru**: `password123`  
**Email**: `zupa.admin@sekolah.com`
