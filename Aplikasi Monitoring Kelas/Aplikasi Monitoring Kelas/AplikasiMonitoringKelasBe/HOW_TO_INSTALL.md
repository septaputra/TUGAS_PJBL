# 🚀 CARA INSTALL FILAMENT - PILIH SALAH SATU

## ⚡ CARA TERCEPAT (Recommended)

### Windows:
1. Buka PowerShell atau Command Prompt
2. Masuk ke folder backend:
   ```cmd
   cd "d:\KELAS-XI\Tugas Video\PROJECT PELATIHAN JETPACK COMPOSE\Aplikasi Monitoring Kelas\AplikasiMonitoringKelasBe"
   ```
3. Jalankan installer:
   ```cmd
   install-filament.bat
   ```
4. Buat admin user:
   ```cmd
   php artisan make:filament-user
   ```
   - Name: **Admin Sekolah**
   - Email: **admin@sekolah.com**
   - Password: **password** (atau sesuai keinginan)

5. Jalankan server:
   ```cmd
   php artisan serve
   ```

6. Buka browser: **http://localhost:8000/admin**

---

## 📦 CARA MANUAL (Step by Step)

Jika script installer tidak jalan, ikuti langkah manual:

### Step 1: Install Filament Package
```bash
composer require filament/filament:"^3.2" -W
```

### Step 2: Install Filament Panel
```bash
php artisan filament:install --panels
```
- Pilih panel name: **admin** (tekan Enter)
- Domain: (kosongkan, tekan Enter)

### Step 3: Run Migrations
```bash
php artisan migrate
```

### Step 4: Clear Cache
```bash
php artisan optimize:clear
php artisan config:cache
```

### Step 5: Buat Admin User
```bash
php artisan make:filament-user
```
- Name: **Admin Sekolah**
- Email: **admin@sekolah.com**
- Password: **password**

### Step 6: Jalankan Server
```bash
php artisan serve
```

### Step 7: Akses Admin Panel
Buka browser: **http://localhost:8000/admin**

---

## 🎯 SETELAH INSTALL

### Generate Resources Tambahan (Opsional)

Setelah Filament terinstall, Anda bisa generate admin interface untuk model lain:

```bash
# Schedule
php artisan make:filament-resource Schedule --generate

# Monitoring
php artisan make:filament-resource Monitoring --generate

# Teacher Attendance
php artisan make:filament-resource TeacherAttendance --generate

# Guru Pengganti
php artisan make:filament-resource GuruPengganti --generate

# Assignment
php artisan make:filament-resource Assignment --generate

# Assignment Submission
php artisan make:filament-resource AssignmentSubmission --generate

# Grade
php artisan make:filament-resource Grade --generate
```

Flag `--generate` akan otomatis membuat form dan table!

---

## ✅ CHECKLIST INSTALASI

- [ ] Composer terinstall
- [ ] PHP 8.2+ terinstall
- [ ] MySQL running
- [ ] Database `monitoring_kelas` sudah dibuat
- [ ] File `.env` sudah dikonfigurasi
- [ ] Migrations sudah dijalankan
- [ ] Filament package terinstall
- [ ] Filament panel terinstall
- [ ] Admin user sudah dibuat
- [ ] Server Laravel berjalan
- [ ] Bisa akses http://localhost:8000/admin

---

## 🆘 TROUBLESHOOTING

### Error: "composer: command not found"
Install Composer dari: https://getcomposer.org/download/

### Error: "Class not found"
```bash
composer dump-autoload
php artisan optimize:clear
```

### Error: "Permission denied"
```bash
chmod -R 775 storage bootstrap/cache
```

### Error: "SQLSTATE connection refused"
- Pastikan MySQL running
- Cek konfigurasi `.env`
- Pastikan database `monitoring_kelas` sudah dibuat

### Error: "419 Page Expired" saat login
```bash
php artisan optimize:clear
php artisan config:cache
```

---

## 📚 DOKUMENTASI

Lihat dokumentasi lengkap:

1. **QUICK_START.md** - Panduan cepat mulai
2. **FILAMENT_SETUP.md** - Panduan detail instalasi
3. **README_FILAMENT.md** - Overview lengkap aplikasi
4. **INTEGRATION_SUMMARY.md** - Summary integrasi

---

## 🎉 SELESAI!

Setelah instalasi berhasil, Anda bisa:

✨ **Manage Users** - Tambah, edit, delete, ban/unban users
📅 **Manage Schedules** - Kelola jadwal pelajaran
📊 **View Monitoring** - Lihat monitoring kehadiran
👥 **Assign Teachers** - Tugaskan guru pengganti
📈 **View Statistics** - Dashboard dengan statistik
🔍 **Global Search** - Cari data dengan cepat (Ctrl+K)
🌙 **Dark Mode** - Toggle theme gelap/terang

---

**Need Help?**
- Check troubleshooting section above
- Read documentation files
- Contact: admin@sekolah.com

**Happy Coding! 🚀**
