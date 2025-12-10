# 🎓 Aplikasi Monitoring Kelas - Backend

Backend API untuk Aplikasi Monitoring Kelas berbasis Laravel 12 dengan Filament Admin Panel.

## 🚀 Teknologi

- **Laravel 12** - PHP Framework
- **Filament 3.2** - Admin Panel
- **MySQL** - Database
- **Laravel Sanctum** - API Authentication
- **PHP 8.2+** - Programming Language

## 📋 Prerequisites

Sebelum instalasi, pastikan sudah terinstall:

- PHP >= 8.2
- Composer
- MySQL >= 8.0
- Node.js & NPM (untuk Vite assets)

## 🔧 Instalasi

### 1. Clone Repository (jika belum)

```bash
cd "d:\KELAS-XI\Tugas Video\PROJECT PELATIHAN JETPACK COMPOSE\Aplikasi Monitoring Kelas\AplikasiMonitoringKelasBe"
```

### 2. Install Dependencies

```bash
composer install
npm install
```

### 3. Setup Environment

```bash
# Copy file environment
copy .env.example .env

# Generate application key
php artisan key:generate
```

### 4. Konfigurasi Database

Edit file `.env` dan sesuaikan konfigurasi database:

```env
DB_CONNECTION=mysql
DB_HOST=127.0.0.1
DB_PORT=3306
DB_DATABASE=monitoring_kelas
DB_USERNAME=root
DB_PASSWORD=
```

### 5. Run Migrations

```bash
php artisan migrate
```

### 6. (Optional) Seed Data

```bash
php artisan db:seed
```

## 🎨 Instalasi Filament Admin Panel

Filament adalah admin panel yang powerful untuk mengelola data aplikasi.

### Cara 1: Menggunakan Script Otomatis (Recommended)

**Windows:**
```bash
install-filament.bat
```

**Linux/Mac:**
```bash
chmod +x install-filament.sh
./install-filament.sh
```

### Cara 2: Manual

Ikuti panduan lengkap di file [FILAMENT_SETUP.md](FILAMENT_SETUP.md)

```bash
# 1. Install Filament
composer require filament/filament:"^3.2" -W

# 2. Install Filament Panel
php artisan filament:install --panels

# 3. Buat Admin User
php artisan make:filament-user

# 4. Akses Admin Panel
# http://localhost:8000/admin
```

## 🏃 Menjalankan Aplikasi

### Development Server

```bash
php artisan serve
```

Aplikasi akan berjalan di: `http://localhost:8000`

### Build Assets (jika menggunakan Vite)

```bash
npm run dev
```

Untuk production:

```bash
npm run build
```

## 📱 API Endpoints

### Authentication

```
POST   /api/register          - Register user baru
POST   /api/login             - Login user
POST   /api/logout            - Logout user
GET    /api/user              - Get current user
```

### Users

```
GET    /api/users             - Get all users (admin only)
GET    /api/users/{id}        - Get user by ID
POST   /api/users             - Create new user (admin only)
PUT    /api/users/{id}        - Update user (admin only)
DELETE /api/users/{id}        - Delete user (admin only)
POST   /api/users/{id}/ban    - Ban user (admin only)
POST   /api/users/{id}/unban  - Unban user (admin only)
```

### Schedules

```
GET    /api/schedules         - Get all schedules
GET    /api/schedules/{id}    - Get schedule by ID
POST   /api/schedules         - Create schedule
PUT    /api/schedules/{id}    - Update schedule
DELETE /api/schedules/{id}    - Delete schedule
```

### Teacher Attendance

```
GET    /api/teacher-attendance              - Get all attendance
POST   /api/teacher-attendance              - Record attendance
GET    /api/teacher-attendance/today        - Get today attendance
GET    /api/teacher-attendance/today-schedules  - Get today schedules
GET    /api/teacher-attendance/all-schedules    - Get all schedules
PUT    /api/teacher-attendance/{id}         - Update attendance
DELETE /api/teacher-attendance/{id}         - Delete attendance
```

### Monitoring

```
GET    /api/monitoring              - Get all monitoring
POST   /api/monitoring              - Create monitoring
GET    /api/monitoring/kelas-kosong - Get empty classes
PUT    /api/monitoring/{id}         - Update monitoring
DELETE /api/monitoring/{id}         - Delete monitoring
```

### Guru Pengganti (Substitute Teacher)

```
GET    /api/guru-pengganti          - Get all replacements
POST   /api/guru-pengganti/assign   - Assign replacement
POST   /api/guru-pengganti/{id}/cancel  - Cancel replacement
```

## 🎨 Filament Admin Panel

Setelah instalasi Filament, akses admin panel di:

```
http://localhost:8000/admin
```

### Fitur Admin Panel:

✅ **User Management**
- CRUD users dengan berbagai role
- Ban/Unban users
- Filter by role dan status

✅ **Schedule Management**
- Manage jadwal pelajaran
- Filter by hari, kelas, guru

✅ **Attendance Monitoring**
- View teacher attendance
- Track empty classes
- Manage substitute teachers

✅ **Dashboard & Statistics**
- Overview statistik
- Charts dan widgets
- Real-time notifications

## 📁 Struktur Folder

```
├── app/
│   ├── Filament/              # Filament resources
│   │   └── Resources/
│   │       └── UserResource.php
│   ├── Http/
│   │   └── Controllers/       # API Controllers
│   └── Models/                # Eloquent Models
├── database/
│   ├── migrations/            # Database migrations
│   └── seeders/               # Database seeders
├── routes/
│   ├── api.php               # API routes
│   └── web.php               # Web routes
├── .env                      # Environment config
├── FILAMENT_SETUP.md        # Panduan Filament
├── install-filament.bat     # Installer Windows
└── install-filament.sh      # Installer Linux/Mac
```

## 🔐 Roles & Permissions

Aplikasi ini memiliki 5 role user:

1. **Admin** - Full access ke semua fitur
2. **Kepala Sekolah** - View all data, limited edit
3. **Kurikulum** - Manage schedules, view monitoring
4. **Guru** - Record attendance, submit grades
5. **Siswa** - View assignments, submit work

## 🧪 Testing

```bash
# Run all tests
php artisan test

# Run specific test
php artisan test --filter=UserTest

# Run with coverage
php artisan test --coverage
```

## 📊 Database Schema

### Users Table
- id, name, email, password
- role (admin, kepala_sekolah, kurikulum, guru, siswa)
- mata_pelajaran (untuk guru)
- is_banned (boolean)

### Schedules Table
- id, hari, kelas, mata_pelajaran
- guru_id, jam_mulai, jam_selesai, ruang

### Teacher Attendances Table
- id, schedule_id, guru_id
- tanggal, jam_masuk, status, keterangan

### Monitoring Table
- id, schedule_id, guru_id, pelapor_id
- tanggal, jam_laporan, status_hadir, catatan

### Guru Pengganti Table
- id, attendance_id, guru_pengganti_id
- tanggal, keterangan

## 🚀 Deployment

### Production Checklist:

- [ ] Set `APP_ENV=production` di `.env`
- [ ] Set `APP_DEBUG=false`
- [ ] Generate APP_KEY
- [ ] Konfigurasi database production
- [ ] Run migrations
- [ ] Optimize aplikasi
- [ ] Setup SSL certificate
- [ ] Configure CORS
- [ ] Setup backup automation

### Optimize untuk Production:

```bash
php artisan config:cache
php artisan route:cache
php artisan view:cache
php artisan optimize
```

## 📝 License

MIT License

## 👨‍💻 Developer

Aplikasi Monitoring Kelas - SMK Negeri 1
Developed with ❤️ using Laravel & Filament

## 📞 Support

Untuk bantuan dan pertanyaan:
- Email: admin@sekolah.com
- Documentation: [FILAMENT_SETUP.md](FILAMENT_SETUP.md)
