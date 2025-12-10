# Perbaikan Import CSV UTF-8 di Filament

## Masalah yang Diperbaiki
Error "Invalid file upload. Please make sure you've selected a valid file." terjadi ketika mengupload file CSV dengan encoding UTF-8 melalui fitur import di Filament.

## Akar Masalah
1. **Filament FileUpload Behavior**: Filament tidak mengembalikan objek `UploadedFile` secara langsung, melainkan mengembalikan path string dari file yang telah disimpan di storage.
2. **Path Storage**: File disimpan di `storage/app/` bukan di `storage/app/public/`.
3. **MIME Type Detection**: Perlu menangani berbagai variasi MIME type untuk CSV.
4. **Encoding Detection**: CSV dengan encoding UTF-8, UTF-16, atau encoding lain perlu dideteksi dan dikonversi.

## Solusi yang Diterapkan

### 1. Perbaikan Handler Upload di Filament Resources
**File yang diubah:**
- `app/Filament/Resources/TeacherResource/Pages/ListTeachers.php`
- `app/Filament/Resources/SubjectResource/Pages/ListSubjects.php`
- `app/Filament/Resources/KelasResource/Pages/ListKelas.php`

**Perubahan:**
```php
// Mengambil path file dari data upload
$filePath = $data['upload'];

// Menggunakan storage path yang benar (app/ bukan app/public/)
$fullPath = storage_path('app/' . $filePath);

// Mendeteksi MIME type dengan fallback
$mimeType = mime_content_type($fullPath);
if (!$mimeType) {
    $extension = strtolower(pathinfo($fullPath, PATHINFO_EXTENSION));
    $mimeType = $extension === 'csv' ? 'text/csv' : 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
}

// Membuat instance UploadedFile dari file yang tersimpan
$uploadedFile = new \Illuminate\Http\UploadedFile(
    $fullPath,
    basename($filePath),
    $mimeType,
    null,
    true
);
```

### 2. Peningkatan FileUpload Configuration
**Perubahan:**
```php
FileUpload::make('upload')
    ->label('Spreadsheet File')
    ->acceptedFileTypes([
        'text/csv', 
        'text/plain', 
        'application/vnd.ms-excel', 
        'application/csv',
        'application/x-csv',
        'text/x-csv',
        'text/comma-separated-values',
        'text/x-comma-separated-values',
        'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    ])
    ->required()
    ->disk('local')
    ->directory('imports')
    ->preserveFilenames()
```

**Keuntungan:**
- Mendukung berbagai variasi MIME type CSV
- Menyimpan file di direktori `imports` untuk organisasi lebih baik
- Menggunakan disk `local` untuk keamanan lebih baik

### 3. Perbaikan CSV Reader untuk Encoding UTF-8
**File yang diubah:** `app/Utils/CsvReader.php`

**Fitur baru:**
```php
// Deteksi encoding otomatis
$content = file_get_contents($filePath);
$encoding = mb_detect_encoding($content, ['UTF-8', 'UTF-16', 'UTF-16LE', 'UTF-16BE', 'ISO-8859-1', 'Windows-1252'], true);

// Konversi ke UTF-8 jika diperlukan
if ($encoding && $encoding !== 'UTF-8') {
    $content = mb_convert_encoding($content, 'UTF-8', $encoding);
    $tempFile = tempnam(sys_get_temp_dir(), 'csv_');
    file_put_contents($tempFile, $content);
    $filePath = $tempFile;
}

// Menghapus BOM (Byte Order Mark) jika ada
if (isset($header[0])) {
    $header[0] = preg_replace('/^\x{FEFF}/u', '', $header[0]);
}
```

**Keuntungan:**
- Otomatis mendeteksi encoding (UTF-8, UTF-16, ISO-8859-1, Windows-1252)
- Konversi otomatis ke UTF-8
- Menangani BOM yang sering muncul di file CSV UTF-8

## Cara Menggunakan

### 1. Format File CSV
Pastikan file CSV Anda memiliki header yang sesuai:

**Teachers CSV:**
```csv
name,email,mata_pelajaran,is_banned,password
John Doe,john@example.com,Mathematics,0,password123
Jane Smith,jane@example.com,English,0,
```

**Subjects CSV:**
```csv
nama,kode
Mathematics,MTK
English,ENG
```

**Classes CSV:**
```csv
nama_kelas,kode_kelas
X RPL 1,X-RPL-1
XI TKJ 2,XI-TKJ-2
```

### 2. Encoding File
- **Direkomendasikan**: UTF-8 (with atau without BOM)
- **Didukung**: UTF-16, ISO-8859-1, Windows-1252
- File akan otomatis dikonversi ke UTF-8

### 3. Import via Filament
1. Buka halaman list (Teachers/Subjects/Classes)
2. Klik tombol "Import Teachers/Subjects/Classes"
3. Pilih file CSV atau XLSX
4. Klik "Submit"

## Testing

### Test Case 1: CSV UTF-8 with BOM
✅ File berhasil diimport tanpa error

### Test Case 2: CSV UTF-8 without BOM
✅ File berhasil diimport tanpa error

### Test Case 3: CSV Windows-1252
✅ File otomatis dikonversi dan berhasil diimport

### Test Case 4: XLSX File
✅ File berhasil diimport

## Troubleshooting

### Error: "Uploaded file not found"
**Solusi:** Pastikan direktori `storage/app/imports` dapat ditulis:
```bash
mkdir -p storage/app/imports
chmod 775 storage/app/imports
```

### Error: "Unable to detect encoding"
**Solusi:** Pastikan file CSV valid dan tidak corrupt. Coba buka dengan text editor dan save ulang dengan encoding UTF-8.

### Import berhasil tapi data kosong
**Solusi:** Pastikan header CSV sesuai dengan yang diharapkan (case-insensitive, spasi/underscore/dash akan dinormalisasi).

## File yang Dimodifikasi

1. ✅ `app/Filament/Resources/TeacherResource/Pages/ListTeachers.php`
2. ✅ `app/Filament/Resources/SubjectResource/Pages/ListSubjects.php`
3. ✅ `app/Filament/Resources/KelasResource/Pages/ListKelas.php`
4. ✅ `app/Utils/CsvReader.php`

## Catatan Penting

- File upload disimpan sementara di `storage/app/imports/`
- File akan otomatis dihapus setelah import selesai (untuk file yang dikonversi)
- Validasi tetap dilakukan sesuai rules yang sudah ditentukan
- Error handling lebih informatif dengan pesan error yang jelas
