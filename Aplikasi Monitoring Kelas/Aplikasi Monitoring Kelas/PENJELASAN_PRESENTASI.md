# 📱 APLIKASI MONITORING KELAS

## 🎯 Deskripsi
Sistem monitoring kehadiran guru dan deteksi kelas kosong berbasis **Android (Jetpack Compose)** dan **Web (Laravel + Filament)**.

**Tujuan**: Memantau kehadiran guru, mendeteksi kelas kosong real-time, dan mengelola penggantian guru.

---

## 🏗️ Teknologi

| Layer | Stack |
|-------|-------|
| **Backend** | Laravel 11 + MySQL + Filament 3.2 |
| **Mobile** | Kotlin + Jetpack Compose + Retrofit |
| **Auth** | Laravel Sanctum (Token-based) |

---

## 👥 Role Pengguna

| Role | Akses |
|------|-------|
| **Admin** | Kelola semua data & user |
| **Kepala Sekolah** | Lihat laporan & statistik |
| **Kurikulum** | Kelola jadwal & tugaskan pengganti |
| **Guru** | Absen & lihat jadwal |
| **Siswa** | Laporkan kelas kosong |

---

## 🔥 Fitur Utama

✅ **Autentikasi** - Login/Register multi-role  
✅ **Absensi Guru** - Status: Hadir, Tidak Hadir, Diganti  
✅ **Deteksi Kelas Kosong** - Auto-detect & laporan siswa  
✅ **Penggantian Guru** - Assign & tracking pengganti  
✅ **Manajemen Jadwal** - CRUD jadwal + validasi bentrok  
✅ **Dashboard Statistik** - Grafik & laporan real-time  

---

## 📂 File Penting Backend

### Controllers (`app/Http/Controllers/`)
- `AuthController.php` - Login & User Management
- `TeacherAttendanceController.php` - Absensi Guru
- `MonitoringController.php` - Kelas Kosong
- `ScheduleController.php` - Jadwal

### Models (`app/Models/`)
- `User.php` - Data user
- `TeacherAttendance.php` - Absensi
- `Monitoring.php` - Laporan kelas kosong
- `Schedule.php` - Jadwal pelajaran

---

## 🔌 API Endpoints

```
POST /api/login                              → Login
GET  /api/teacher-attendance/today           → Absensi hari ini
POST /api/teacher-attendance                 → Create absensi
POST /api/monitoring/store                   → Lapor kelas kosong
GET  /api/jadwal                             → List jadwal
```

---

## 🚀 Cara Menjalankan

### Backend
```bash
composer install
cp .env.example .env
php artisan key:generate
php artisan migrate
php artisan serve
```

### Admin Panel
```
URL: http://localhost:8000/admin
Login: zupa.admin@sekolah.com / password123
```

### Mobile
1. Buka di Android Studio
2. Sync Gradle
3. Update Base URL
4. Run

---

## 📊 Alur Kerja

**Siswa Lapor Kelas Kosong**  
Siswa → Lapor → Sistem notif Kurikulum → Assign pengganti

**Guru Absen**  
Guru → Pilih jadwal → Absen → Update real-time

**Penggantian**  
Kurikulum → Lihat kelas kosong → Pilih pengganti → Notifikasi

---

## 🎯 Keunggulan

✅ Real-time monitoring  
✅ Multi-role dengan hak akses berbeda  
✅ UI modern (Jetpack Compose)  
✅ Secure (token-based auth)  
✅ Dashboard statistik lengkap  

---

**Status**: ✅ Production Ready  
**Dibuat oleh**: Kelas XI - 2025
