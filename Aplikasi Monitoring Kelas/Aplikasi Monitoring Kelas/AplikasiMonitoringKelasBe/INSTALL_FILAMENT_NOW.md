# ⚡ INSTALASI FILAMENT - LANGSUNG JALAN!

## 🎯 Copy-Paste Command Ini Satu Per Satu

Buka PowerShell/CMD di folder backend dan jalankan:

```powershell
cd "D:\KELAS-XI\Tugas Video\PROJECT PELATIHAN JETPACK COMPOSE\Aplikasi Monitoring Kelas\AplikasiMonitoringKelasBe"
```

### 1. Install Filament
```powershell
composer require filament/filament:"^3.2" -W --no-interaction
```

### 2. Publish Vendor
```powershell
php artisan vendor:publish --tag=filament-assets --force
php artisan vendor:publish --tag=filament-config
php artisan filament:install --panels --no-interaction
```

Jika command `filament:install` error, skip saja dan lanjut ke step 3.

### 3. Clear Cache
```powershell
php artisan optimize:clear
php artisan config:cache
composer dump-autoload
```

### 4. Buat Admin Panel Provider
Jika belum ada, buat manual file: `app/Providers/Filament/AdminPanelProvider.php`

Atau jalankan:
```powershell
php artisan make:filament-panel admin
```

### 5. Buat User Admin
```powershell
php artisan make:filament-user
```

Input:
- Name: **Admin Sekolah**
- Email: **admin@sekolah.com**
- Password: **password**

### 6. Jalankan Server
```powershell
php artisan serve
```

### 7. Akses Admin
Buka browser: **http://localhost:8000/admin**

---

## ✅ JIKA SUKSES

Anda akan melihat halaman login Filament yang cantik!
Login dengan email dan password yang dibuat tadi.

##  🆘 JIKA GAGAL

### Error: "Class not found"
```powershell
composer dump-autoload
php artisan optimize:clear
```

### Error: "filament:install command not found"
Install ulang Filament:
```powershell
composer remove filament/filament
composer require filament/filament:"^3.2" -W
```

### Error: "Route [filament.admin.auth.login] not defined"
```powershell
php artisan route:clear
php artisan config:clear
php artisan cache:clear
php artisan view:clear
```

### Masih Error?
Cek file `config/app.php`, pastikan ada di providers:
```php
App\Providers\Filament\AdminPanelProvider::class,
```

---

## 🎨 ALTERNATIF: Install Filament Secara Manual

Jika semua cara di atas gagal, buat panel secara manual:

1. **Create Admin Panel Provider**

File: `app/Providers/Filament/AdminPanelProvider.php`

```php
<?php

namespace App\Providers\Filament;

use Filament\Http\Middleware\Authenticate;
use Filament\Http\Middleware\DisableBladeIconComponents;
use Filament\Http\Middleware\DispatchServingFilamentEvent;
use Filament\Pages;
use Filament\Panel;
use Filament\PanelProvider;
use Filament\Support\Colors\Color;
use Filament\Widgets;
use Illuminate\Cookie\Middleware\AddQueuedCookiesToResponse;
use Illuminate\Cookie\Middleware\EncryptCookies;
use Illuminate\Foundation\Http\Middleware\VerifyCsrfToken;
use Illuminate\Routing\Middleware\SubstituteBindings;
use Illuminate\Session\Middleware\AuthenticateSession;
use Illuminate\Session\Middleware\StartSession;
use Illuminate\View\Middleware\ShareErrorsFromSession;

class AdminPanelProvider extends PanelProvider
{
    public function panel(Panel $panel): Panel
    {
        return $panel
            ->default()
            ->id('admin')
            ->path('admin')
            ->login()
            ->colors([
                'primary' => Color::Amber,
            ])
            ->discoverResources(in: app_path('Filament/Resources'), for: 'App\\Filament\\Resources')
            ->discoverPages(in: app_path('Filament/Pages'), for: 'App\\Filament\\Pages')
            ->pages([
                Pages\Dashboard::class,
            ])
            ->discoverWidgets(in: app_path('Filament/Widgets'), for: 'App\\Filament\\Widgets')
            ->widgets([
                Widgets\AccountWidget::class,
                Widgets\FilamentInfoWidget::class,
            ])
            ->middleware([
                EncryptCookies::class,
                AddQueuedCookiesToResponse::class,
                StartSession::class,
                AuthenticateSession::class,
                ShareErrorsFromSession::class,
                VerifyCsrfToken::class,
                SubstituteBindings::class,
                DisableBladeIconComponents::class,
                DispatchServingFilamentEvent::class,
            ])
            ->authMiddleware([
                Authenticate::class,
            ]);
    }
}
```

2. **Register Provider**

Edit `config/app.php`, tambahkan di array `providers`:

```php
'providers' => ServiceProvider::defaultProviders()->merge([
    // ... providers lain
    App\Providers\Filament\AdminPanelProvider::class,
])->toArray(),
```

3. **Clear Cache**
```powershell
php artisan optimize:clear
php artisan config:cache
```

4. **Buat User Admin Manual**

Jalankan PHP tinker:
```powershell
php artisan tinker
```

Lalu ketik:
```php
\App\Models\User::create([
    'name' => 'Admin Sekolah',
    'email' => 'admin@sekolah.com',
    'password' => bcrypt('password'),
    'role' => 'admin',
]);
```

5. **Jalankan Server**
```powershell
php artisan serve
```

6. **Akses**
http://localhost:8000/admin

---

**Selamat! Filament siap digunakan! 🎉**
