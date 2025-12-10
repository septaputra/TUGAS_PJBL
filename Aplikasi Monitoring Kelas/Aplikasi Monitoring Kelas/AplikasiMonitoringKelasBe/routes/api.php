<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\AuthController;
use App\Http\Controllers\ScheduleController;
use App\Http\Controllers\TeacherController;
use App\Http\Controllers\MonitoringController;
use App\Http\Controllers\GuruPenggantiController;
use App\Http\Controllers\TeacherAttendanceController;
use App\Http\Controllers\TeacherReplacementController;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
*/

// Public routes
Route::post('/register', [AuthController::class, 'register']);
Route::post('/login', [AuthController::class, 'login']);

// Protected routes - Semua user yang sudah login
Route::middleware('auth:sanctum')->group(function () {
    // User Profile
    Route::post('/logout', [AuthController::class, 'logout']);
    Route::get('/user', [AuthController::class, 'user']);
    Route::put('/user', [AuthController::class, 'updateProfile']);

    // Jadwal Pelajaran - Bisa diakses semua role
    Route::get('/jadwal', [ScheduleController::class, 'index']);

    // Teacher Attendance - Bisa diakses semua role yang sudah login
    Route::get('/teacher-attendance/today-schedules', [TeacherAttendanceController::class, 'todaySchedules']);
    Route::get('/teacher-attendance/all-schedules', [TeacherAttendanceController::class, 'allSchedules']);
    Route::get('/teacher-attendance/today', [TeacherAttendanceController::class, 'today']);
    Route::get('/teacher-attendance/statistics', [TeacherAttendanceController::class, 'statistics']);
    Route::get('/teacher-attendance', [TeacherAttendanceController::class, 'index']);
    Route::post('/teacher-attendance', [TeacherAttendanceController::class, 'store']);
    Route::get('/teacher-attendance/{id}', [TeacherAttendanceController::class, 'show']);
    Route::put('/teacher-attendance/{id}', [TeacherAttendanceController::class, 'update']);
    Route::delete('/teacher-attendance/{id}', [TeacherAttendanceController::class, 'destroy']);
});

// Routes untuk SISWA - Mencatat monitoring
Route::middleware(['auth:sanctum', 'role:siswa'])->group(function () {
    Route::post('/monitoring/store', [MonitoringController::class, 'store']);
    Route::get('/monitoring/my-reports', [MonitoringController::class, 'myReports']); // Laporan yang dibuat siswa
});
Route::get('/monitoring/kelas-kosong-attendance', [MonitoringController::class, 'getKelasKosongFromAttendance']); 

// Routes untuk KURIKULUM, GURU, KEPALA SEKOLAH, dan ADMIN - Cek kelas kosong, lihat laporan monitoring, dan lihat guru pengganti
Route::middleware(['auth:sanctum', 'role:kurikulum,guru,kepala_sekolah,admin'])->group(function () {
    Route::get('/monitoring', [MonitoringController::class, 'index']);
    Route::get('/monitoring/kelas-kosong', [MonitoringController::class, 'kelasKosong']);

    // Endpoints for replacement UI: get form data and submit replacement
    Route::get('/teacher-attendance/{id}/replacement-form', [MonitoringController::class, 'replacementForm']);
    Route::post('/teacher-attendance/{id}/replace', [MonitoringController::class, 'replaceTeacher']);

    Route::get('/guru-pengganti', [GuruPenggantiController::class, 'index']);

    // Teacher Replacement - Lihat penggantian guru
    Route::get('/teacher-replacement', [TeacherReplacementController::class, 'index']);
});

// Routes untuk KEPALA SEKOLAH dan ADMIN - Lihat semua laporan kelas kosong dari siswa
Route::middleware(['auth:sanctum', 'role:kepala_sekolah,admin'])->group(function () {
    Route::get('/monitoring/empty-class-reports', [MonitoringController::class, 'getAllEmptyClassReports']);
    Route::get('/monitoring/empty-classes-only', [MonitoringController::class, 'getEmptyClassOnly']);
});

// Routes khusus untuk KURIKULUM dan GURU - Kelola guru pengganti (create, update, delete)
Route::middleware(['auth:sanctum', 'role:kurikulum,guru'])->group(function () {
    Route::post('/guru-pengganti', [GuruPenggantiController::class, 'store']);
    Route::put('/guru-pengganti/{id}', [GuruPenggantiController::class, 'update']);
    Route::delete('/guru-pengganti/{id}', [GuruPenggantiController::class, 'destroy']);

    // Kurikulum dan Guru bisa melihat daftar guru untuk keperluan penugasan
    Route::get('/users/guru', [AuthController::class, 'getGuruList']);

    // Teacher Replacement - Tugaskan dan batalkan guru pengganti
    Route::post('/teacher-replacement/assign', [TeacherReplacementController::class, 'assignReplacement']);
    Route::post('/teacher-replacement/{id}/cancel', [TeacherReplacementController::class, 'cancelReplacement']);
});

// Routes untuk KURIKULUM, GURU, dan KEPALA SEKOLAH - Melihat daftar guru
Route::middleware(['auth:sanctum', 'role:kurikulum,guru,kepala_sekolah'])->group(function () {
    Route::get('/teachers', [TeacherController::class, 'index']);
    Route::get('/teachers/{id}', [TeacherController::class, 'show']);
});

// Routes untuk ADMIN dan KEPALA SEKOLAH - Lihat daftar semua users (untuk statistik)
Route::middleware(['auth:sanctum', 'role:admin,kepala_sekolah'])->group(function () {
    Route::get('/users', [AuthController::class, 'getAllUsers']);
});

// Routes untuk ADMIN - User Management (sensitive operations)
Route::middleware(['auth:sanctum', 'role:admin'])->group(function () {
    // User Management - Admin only can manage users
    Route::post('/users', [AuthController::class, 'createUser']);
    Route::put('/users/{id}/role', [AuthController::class, 'updateUserRole']);
    Route::put('/users/{id}/ban', [AuthController::class, 'banUser']);
    Route::put('/users/{id}/unban', [AuthController::class, 'unbanUser']);
    Route::delete('/users/{id}', [AuthController::class, 'deleteUser']);
});

// Routes untuk ADMIN dan KEPALA SEKOLAH - Manajemen jadwal
Route::middleware(['auth:sanctum', 'role:admin,kepala_sekolah'])->group(function () {
    // Admin dan Kepala Sekolah bisa mengelola jadwal
    Route::post('/jadwal', [ScheduleController::class, 'store']);
    Route::put('/jadwal/{id}', [ScheduleController::class, 'update']);
    Route::delete('/jadwal/{id}', [ScheduleController::class, 'destroy']);
});



