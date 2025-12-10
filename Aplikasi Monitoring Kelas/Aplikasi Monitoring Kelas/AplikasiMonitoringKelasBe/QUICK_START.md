# ⚡ Quick Start - Instalasi Filament

Panduan cepat untuk menginstall dan menggunakan Filament Admin Panel di Aplikasi Monitoring Kelas.

## 🎯 Langkah Singkat

### 1. Jalankan Script Installer

Buka terminal/command prompt di folder backend, lalu jalankan:

**Windows (PowerShell/CMD):**
```cmd
install-filament.bat
```

**Linux/Mac:**
```bash
chmod +x install-filament.sh
./install-filament.sh
```

### 2. Buat User Admin

Setelah instalasi selesai, buat user admin:

```bash
php artisan make:filament-user
```

Masukkan:
- **Name:** Admin Sekolah
- **Email:** admin@sekolah.com  
- **Password:** password (atau password pilihan Anda)

### 3. Jalankan Server

```bash
php artisan serve
```

### 4. Akses Admin Panel

Buka browser dan kunjungi:
```
http://localhost:8000/admin
```

Login menggunakan email dan password yang dibuat di step 2.

## ✅ Selesai!

Anda sekarang bisa:
- ✨ Manage users dengan berbagai role
- 📅 Kelola jadwal pelajaran  
- 📊 Monitoring kehadiran guru
- 👥 Assign guru pengganti
- 📈 Lihat statistik dan dashboard

## 🎨 Customize Resources (Opsional)

Jika ingin customize tampilan dan fungsionalitas, edit file di:
```
app/Filament/Resources/UserResource.php
```

## 📚 Dokumentasi Lengkap

Lihat dokumentasi lengkap di:
- [FILAMENT_SETUP.md](FILAMENT_SETUP.md) - Panduan instalasi detail
- [README_FILAMENT.md](README_FILAMENT.md) - Overview aplikasi

## 🆘 Troubleshooting

### Error: Class not found
```bash
composer dump-autoload
php artisan optimize:clear
```

### Error: Migration already exists
```bash
php artisan migrate:fresh
```
⚠️ **Warning:** Ini akan menghapus semua data!

### Error: Permission denied
```bash
chmod -R 777 storage bootstrap/cache
```

## 🎯 Next Steps

Setelah Filament terinstall, Anda bisa:

1. **Generate Resources untuk Model Lain:**
   ```bash
   php artisan make:filament-resource Schedule --generate
   php artisan make:filament-resource Monitoring --generate
   php artisan make:filament-resource TeacherAttendance --generate
   ```

2. **Customize Dashboard:**
   Edit `app/Filament/Pages/Dashboard.php`

3. **Tambah Widgets:**
   ```bash
   php artisan make:filament-widget StatsOverview
   ```

4. **Setup Notifications:**
   Filament sudah support notifications out of the box!

## 💡 Tips

- **Dark Mode:** Klik icon moon di kanan atas
- **Global Search:** Tekan `Ctrl + K` atau `Cmd + K`
- **Keyboard Shortcuts:** Lihat di dokumentasi Filament
- **Responsive:** Admin panel otomatis responsive untuk mobile

---

**Happy Coding! 🚀**
