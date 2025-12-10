# 🎨 Panduan Instalasi Filament untuk Aplikasi Monitoring Kelas

Filament adalah admin panel yang powerful dan modern untuk Laravel. Versi 3.2+ kompatibel dengan Laravel 12.

## 📋 Langkah-Langkah Instalasi

### 1. Install Filament Package

Jalankan perintah berikut di terminal (di folder backend):

```bash
composer require filament/filament:"^3.2" -W
```

### 2. Install Filament Panel

```bash
php artisan filament:install --panels
```

Ketika ditanya, pilih:
- Panel name: `admin` (default)
- Domain: (kosongkan untuk menggunakan domain default)

### 3. Buat User Admin

```bash
php artisan make:filament-user
```

Masukkan:
- Name: Admin Sekolah
- Email: admin@sekolah.com
- Password: password (atau sesuai keinginan)

### 4. Jalankan Migrasi (jika ada migrasi baru)

```bash
php artisan migrate
```

### 5. Akses Admin Panel

Buka browser dan akses:
```
http://localhost:8000/admin
```

Login menggunakan kredensial yang dibuat di langkah 3.

## 🔧 Konfigurasi

### Update User Model

File `app/Models/User.php` sudah compatible dengan Filament karena menggunakan Sanctum dan struktur standar Laravel.

## 📊 Membuat Resources untuk Filament

Setelah instalasi berhasil, jalankan perintah berikut untuk membuat resources:

### 1. User Resource

```bash
php artisan make:filament-resource User --generate
```

### 2. Schedule Resource

```bash
php artisan make:filament-resource Schedule --generate
```

### 3. Monitoring Resource

```bash
php artisan make:filament-resource Monitoring --generate
```

### 4. TeacherAttendance Resource

```bash
php artisan make:filament-resource TeacherAttendance --generate
```

### 5. GuruPengganti Resource

```bash
php artisan make:filament-resource GuruPengganti --generate
```

### 6. Assignment Resource

```bash
php artisan make:filament-resource Assignment --generate
```

### 7. AssignmentSubmission Resource

```bash
php artisan make:filament-resource AssignmentSubmission --generate
```

### 8. Grade Resource

```bash
php artisan make:filament-resource Grade --generate
```

## 🎨 Customisasi Resource (Opsional)

Setelah resources dibuat, Anda dapat mengeditnya di folder:
```
app/Filament/Resources/
```

Setiap resource memiliki:
- `{Model}Resource.php` - Konfigurasi utama
- `Pages/` - Halaman List, Create, Edit
- Form dan Table schema yang bisa disesuaikan

## 🔐 Middleware dan Akses

Filament secara otomatis menggunakan middleware:
- `auth` - Hanya user yang login
- `verified` - (opsional) Email verification

Untuk membatasi akses berdasarkan role, tambahkan di `app/Filament/Pages/Dashboard.php`:

```php
public static function canAccess(): bool
{
    return auth()->user()->role === 'admin';
}
```

## 📱 Widget Dashboard (Opsional)

Buat widget untuk dashboard:

```bash
php artisan make:filament-widget StatsOverview
```

Edit file di `app/Filament/Widgets/StatsOverview.php` untuk menampilkan statistik.

## 🎯 Fitur Filament yang Tersedia

1. **CRUD Interface** - Otomatis untuk semua model
2. **Search & Filter** - Pencarian dan filter data
3. **Bulk Actions** - Aksi massal untuk data
4. **Export/Import** - Export data ke Excel/CSV
5. **Charts & Widgets** - Grafik dan statistik
6. **Role & Permissions** - Manajemen akses
7. **Dark Mode** - Theme gelap/terang
8. **Notification** - Notifikasi real-time
9. **Global Search** - Pencarian global di semua resource

## 🚀 Perintah Berguna

```bash
# Generate semua resources sekaligus
php artisan make:filament-resource User --generate
php artisan make:filament-resource Schedule --generate
php artisan make:filament-resource Monitoring --generate
php artisan make:filament-resource TeacherAttendance --generate
php artisan make:filament-resource GuruPengganti --generate

# Clear cache
php artisan filament:cache-components
php artisan optimize:clear

# Update Filament
composer update filament/filament
php artisan filament:upgrade
```

## 📚 Dokumentasi

- Filament Docs: https://filamentphp.com/docs/3.x/panels/installation
- Laravel Docs: https://laravel.com/docs/12.x

## ⚡ Tips

1. **Performance**: Gunakan eager loading di resources untuk optimasi query
2. **Security**: Jangan lupa set `APP_ENV=production` di server
3. **Backup**: Selalu backup database sebelum modifikasi
4. **Testing**: Test semua fitur di development environment dulu

## 🎉 Selesai!

Setelah mengikuti langkah-langkah di atas, admin panel Filament siap digunakan untuk mengelola:
- Users (Admin, Guru, Siswa, Kurikulum, Kepala Sekolah)
- Jadwal Pelajaran
- Monitoring Kehadiran Guru
- Teacher Attendance
- Guru Pengganti
- Assignments & Submissions
- Grades

Akses di: **http://localhost:8000/admin**
