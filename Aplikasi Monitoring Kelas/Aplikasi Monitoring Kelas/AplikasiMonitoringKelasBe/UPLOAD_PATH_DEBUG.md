# Debug Upload Path - Filament Import

## Direktori Storage yang Tersedia

Sistem akan mencari file yang diupload di lokasi berikut (berurutan):

1. `storage/app/{filePath}` - Path utama (contoh: `storage/app/imports/file.csv`)
2. `storage/app/public/{filePath}` - Path public storage
3. `storage/app/livewire-tmp/{filePath}` - Temporary upload Livewire
4. `storage/app/filament-tmp/{filePath}` - Temporary upload Filament
5. `public/storage/{filePath}` - Symlinked public storage

## Direktori yang Sudah Dibuat

✅ `storage/app/imports/` - Direktori untuk file import
✅ `storage/app/livewire-tmp/` - Temporary Livewire uploads
✅ `storage/app/filament-tmp/` - Temporary Filament uploads

## Cara Mengecek Path Upload

Jika terjadi error "Uploaded file not found", pesan error sekarang akan menampilkan:
```
Uploaded file not found. Path checked: imports/file.csv. Please try uploading again.
```

## Troubleshooting

### Error: "Uploaded file not found"

**Langkah 1: Cek direktori storage**
```powershell
Get-ChildItem -Path "storage\app" -Recurse -File | Where-Object {$_.Extension -eq ".csv"}
```

**Langkah 2: Pastikan permission folder**
```powershell
# Windows - buka Properties folder storage dan pastikan "Read & Write" dicentang
```

**Langkah 3: Clear cache Filament**
```bash
php artisan filament:cache-clear
php artisan cache:clear
```

**Langkah 4: Test upload ulang**
- Upload file lagi
- Jika masih error, catat path yang ditampilkan di error message
- File seharusnya ada di salah satu dari 5 lokasi di atas

## Format File CSV yang Benar

### Teachers CSV
```csv
name,email,mata_pelajaran,is_banned,password
John Doe,john@example.com,Mathematics,0,password123
Jane Smith,jane@example.com,English,0,
```

### Subjects CSV
```csv
nama,kode
Mathematics,MTK
English,ENG
```

### Classes CSV
```csv
nama_kelas,kode_kelas
X RPL 1,X-RPL-1
XI TKJ 2,XI-TKJ-2
```

## Encoding yang Didukung

✅ UTF-8 (with atau without BOM)
✅ UTF-16, UTF-16LE, UTF-16BE
✅ ISO-8859-1
✅ Windows-1252

File akan **otomatis dikonversi** ke UTF-8 saat diproses.
