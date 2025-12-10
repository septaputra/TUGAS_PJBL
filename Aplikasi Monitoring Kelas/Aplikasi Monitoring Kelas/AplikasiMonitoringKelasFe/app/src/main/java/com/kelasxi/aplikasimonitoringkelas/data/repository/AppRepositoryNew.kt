package com.kelasxi.aplikasimonitoringkelas.data.repository

import com.kelasxi.aplikasimonitoringkelas.data.api.ApiService
import com.kelasxi.aplikasimonitoringkelas.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppRepositoryNew(private val apiService: ApiService) {
    
    // ==================== JADWAL PELAJARAN ====================
    
    suspend fun getSchedules(token: String, hari: String? = null, kelas: String? = null): Result<ScheduleResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getSchedules("Bearer $token", hari, kelas)
                if (response.isSuccessful && response.body() != null) {
                    val scheduleResponse = response.body()!!
                    if (scheduleResponse.success) {
                        Result.success(scheduleResponse)
                    } else {
                        Result.failure(Exception(scheduleResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal mengambil jadwal: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    // ==================== ADMIN: USER MANAGEMENT ====================
    
    suspend fun getUsers(token: String): Result<UsersResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getUsers("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    val usersResponse = response.body()!!
                    if (usersResponse.success) {
                        Result.success(usersResponse)
                    } else {
                        Result.failure(Exception(usersResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal mengambil data users: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    suspend fun getGuruList(token: String): Result<UsersResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getGuruList("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    val usersResponse = response.body()!!
                    if (usersResponse.success) {
                        Result.success(usersResponse)
                    } else {
                        Result.failure(Exception(usersResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal mengambil data guru: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    // New method to fetch from teachers table
    suspend fun getTeachers(token: String): Result<UsersResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getTeachers("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    val usersResponse = response.body()!!
                    if (usersResponse.success) {
                        Result.success(usersResponse)
                    } else {
                        Result.failure(Exception(usersResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal mengambil data guru dari tabel teachers: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    suspend fun createUser(token: String, request: CreateUserRequest): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.createUser("Bearer $token", request)
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.success && apiResponse.data != null) {
                        Result.success(apiResponse.data)
                    } else {
                        Result.failure(Exception(apiResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal membuat user: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    suspend fun updateUserRole(token: String, userId: Int, role: String): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updateUserRole("Bearer $token", userId, UpdateRoleRequest(role))
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.success && apiResponse.data != null) {
                        Result.success(apiResponse.data)
                    } else {
                        Result.failure(Exception(apiResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal mengupdate role: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    suspend fun banUser(token: String, userId: Int): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.banUser("Bearer $token", userId)
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.success && apiResponse.data != null) {
                        Result.success(apiResponse.data)
                    } else {
                        Result.failure(Exception(apiResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal ban user: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    suspend fun unbanUser(token: String, userId: Int): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.unbanUser("Bearer $token", userId)
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.success && apiResponse.data != null) {
                        Result.success(apiResponse.data)
                    } else {
                        Result.failure(Exception(apiResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal unban user: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    suspend fun deleteUser(token: String, userId: Int): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.deleteUser("Bearer $token", userId)
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.success) {
                        Result.success(true)
                    } else {
                        Result.failure(Exception(apiResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal menghapus user: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    // ==================== SISWA: MONITORING ====================
    
    suspend fun storeMonitoring(token: String, request: MonitoringRequest): Result<MonitoringResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.storeMonitoring("Bearer $token", request)
                if (response.isSuccessful && response.body() != null) {
                    val monitoringResponse = response.body()!!
                    if (monitoringResponse.success) {
                        Result.success(monitoringResponse)
                    } else {
                        Result.failure(Exception(monitoringResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal menyimpan monitoring: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    suspend fun getMyReports(token: String, tanggal: String? = null): Result<MonitoringListResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getMyReports("Bearer $token", tanggal)
                if (response.isSuccessful && response.body() != null) {
                    val monitoringResponse = response.body()!!
                    if (monitoringResponse.success) {
                        Result.success(monitoringResponse)
                    } else {
                        Result.failure(Exception(monitoringResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal mengambil laporan: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    // ==================== KURIKULUM & KEPALA SEKOLAH: MONITORING ====================
    
    suspend fun getMonitoring(token: String, tanggal: String? = null, kelas: String? = null, guruId: Int? = null): Result<MonitoringListResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getMonitoring("Bearer $token", tanggal, kelas, guruId)
                if (response.isSuccessful && response.body() != null) {
                    val monitoringResponse = response.body()!!
                    if (monitoringResponse.success) {
                        Result.success(monitoringResponse)
                    } else {
                        Result.failure(Exception(monitoringResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = "Gagal mengambil data monitoring: ${response.code()} - $errorBody"
                    
                    // Check if it's a 403 Forbidden error related to role permissions
                    if (response.code() == 403 && errorBody?.contains("Forbidden") == true) {
                        Result.failure(Exception("Akses ditolak: Anda tidak memiliki izin untuk mengakses data monitoring. Hubungi administrator sistem."))
                    } else {
                        Result.failure(Exception(errorMessage))
                    }
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    suspend fun getEmptyClassReports(token: String, tanggal: String? = null, kelas: String? = null, guruId: Int? = null): Result<MonitoringListResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getEmptyClassReports("Bearer $token", tanggal, kelas, guruId)
                if (response.isSuccessful && response.body() != null) {
                    val monitoringResponse = response.body()!!
                    if (monitoringResponse.success) {
                        Result.success(monitoringResponse)
                    } else {
                        Result.failure(Exception(monitoringResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = "Gagal mengambil laporan kelas kosong: ${response.code()} - $errorBody"
                    
                    // Check if it's a 403 Forbidden error related to role permissions
                    if (response.code() == 403 && errorBody?.contains("Forbidden") == true) {
                        Result.failure(Exception("Akses ditolak: Anda tidak memiliki izin untuk mengakses laporan kelas kosong. Hubungi administrator sistem."))
                    } else {
                        Result.failure(Exception(errorMessage))
                    }
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    suspend fun getEmptyClassesOnly(token: String, tanggal: String? = null, kelas: String? = null, guruId: Int? = null): Result<MonitoringListResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getEmptyClassesOnly("Bearer $token", tanggal, kelas, guruId)
                if (response.isSuccessful && response.body() != null) {
                    val monitoringResponse = response.body()!!
                    if (monitoringResponse.success) {
                        Result.success(monitoringResponse)
                    } else {
                        Result.failure(Exception(monitoringResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = "Gagal mengambil data kelas kosong: ${response.code()} - $errorBody"
                    
                    // Check if it's a 403 Forbidden error related to role permissions
                    if (response.code() == 403 && errorBody?.contains("Forbidden") == true) {
                        Result.failure(Exception("Akses ditolak: Anda tidak memiliki izin untuk mengakses data kelas kosong. Hubungi administrator sistem."))
                    } else {
                        Result.failure(Exception(errorMessage))
                    }
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    suspend fun getKelasKosong(token: String, tanggal: String? = null): Result<KelasKosongResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getKelasKosong("Bearer $token", tanggal)
                if (response.isSuccessful && response.body() != null) {
                    val kelasKosongResponse = response.body()!!
                    if (kelasKosongResponse.success) {
                        Result.success(kelasKosongResponse)
                    } else {
                        Result.failure(Exception(kelasKosongResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = "Gagal mengambil kelas kosong: ${response.code()} - $errorBody"
                    
                    // Check if it's a 403 Forbidden error related to role permissions
                    if (response.code() == 403 && errorBody?.contains("Forbidden") == true) {
                        Result.failure(Exception("Akses ditolak: Anda tidak memiliki izin untuk mengakses fitur ini. Hubungi administrator sistem."))
                    } else {
                        Result.failure(Exception(errorMessage))
                    }
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    suspend fun getKelasKosongFromAttendance(
        token: String, 
        tanggal: String? = null,
        kelas: String? = null,
        guruId: Int? = null
    ): Result<KelasKosongResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getKelasKosongFromAttendance("Bearer $token", tanggal, kelas, guruId)
                if (response.isSuccessful && response.body() != null) {
                    val kelasKosongResponse = response.body()!!
                    if (kelasKosongResponse.success) {
                        Result.success(kelasKosongResponse)
                    } else {
                        Result.failure(Exception(kelasKosongResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = "Gagal mengambil kelas kosong dari kehadiran guru: ${response.code()} - $errorBody"
                    
                    // Check if it's a 403 Forbidden error related to role permissions
                    if (response.code() == 403 && errorBody?.contains("Forbidden") == true) {
                        Result.failure(Exception("Akses ditolak: Anda tidak memiliki izin untuk mengakses fitur ini. Hubungi administrator sistem."))
                    } else {
                        Result.failure(Exception(errorMessage))
                    }
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    // ==================== KURIKULUM: GURU PENGGANTI ====================
    
    suspend fun getGuruPengganti(token: String, tanggal: String? = null, kelas: String? = null): Result<GuruPenggantiResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getGuruPengganti("Bearer $token", tanggal, kelas)
                if (response.isSuccessful && response.body() != null) {
                    val guruPenggantiResponse = response.body()!!
                    if (guruPenggantiResponse.success) {
                        Result.success(guruPenggantiResponse)
                    } else {
                        Result.failure(Exception(guruPenggantiResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = "Gagal mengambil guru pengganti: ${response.code()} - $errorBody"
                    
                    // Check if it's a 403 Forbidden error related to role permissions
                    if (response.code() == 403 && errorBody?.contains("Forbidden") == true) {
                        Result.failure(Exception("Akses ditolak: Anda tidak memiliki izin untuk mengakses data guru pengganti. Hubungi administrator sistem."))
                    } else {
                        Result.failure(Exception(errorMessage))
                    }
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    suspend fun createGuruPengganti(token: String, request: GuruPenggantiRequest): Result<GuruPengganti> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.createGuruPengganti("Bearer $token", request)
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.success && apiResponse.data != null) {
                        Result.success(apiResponse.data)
                    } else {
                        Result.failure(Exception(apiResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal menambahkan guru pengganti: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    suspend fun updateGuruPengganti(token: String, id: Int, request: GuruPenggantiRequest): Result<GuruPengganti> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updateGuruPengganti("Bearer $token", id, request)
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.success && apiResponse.data != null) {
                        Result.success(apiResponse.data)
                    } else {
                        Result.failure(Exception(apiResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal mengupdate guru pengganti: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    suspend fun deleteGuruPengganti(token: String, id: Int): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.deleteGuruPengganti("Bearer $token", id)
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.success) {
                        Result.success(true)
                    } else {
                        Result.failure(Exception(apiResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal menghapus guru pengganti: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    // ==================== TEACHER REPLACEMENT ====================
    
    suspend fun getTeacherReplacements(token: String, tanggal: String? = null): Result<TeacherReplacementResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getTeacherReplacements("Bearer $token", tanggal)
                if (response.isSuccessful && response.body() != null) {
                    val replacementResponse = response.body()!!
                    if (replacementResponse.success) {
                        Result.success(replacementResponse)
                    } else {
                        Result.failure(Exception(replacementResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = "Gagal mengambil data penggantian guru: ${response.code()} - $errorBody"
                    
                    // Check if it's a 403 Forbidden error related to role permissions
                    if (response.code() == 403 && errorBody?.contains("Forbidden") == true) {
                        Result.failure(Exception("Akses ditolak: Anda tidak memiliki izin untuk mengakses data penggantian guru. Hubungi administrator sistem."))
                    } else {
                        Result.failure(Exception(errorMessage))
                    }
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }

    // ==================== STUDENT: Replacement Form & Replace Action ====================

    suspend fun getReplacementForm(token: String, attendanceId: Int): Result<ReplacementFormResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getReplacementForm("Bearer $token", attendanceId)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.success) {
                        Result.success(body)
                    } else {
                        Result.failure(Exception(body.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal mengambil form penggantian: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }

    suspend fun replaceTeacher(token: String, attendanceId: Int, request: AssignReplacementRequest): Result<ApiResponse<ReplaceResultData>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.replaceTeacher("Bearer $token", attendanceId, request)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.success) {
                        Result.success(body)
                    } else {
                        Result.failure(Exception(body.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal menyimpan penggantian: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    suspend fun assignReplacement(token: String, request: AssignReplacementRequest): Result<TeacherReplacement> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.assignReplacement("Bearer $token", request)
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.success && apiResponse.data != null) {
                        Result.success(apiResponse.data)
                    } else {
                        Result.failure(Exception(apiResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal menugaskan guru pengganti: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    suspend fun cancelReplacement(token: String, id: Int): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.cancelReplacement("Bearer $token", id)
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.success) {
                        Result.success(true)
                    } else {
                        Result.failure(Exception(apiResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal membatalkan penggantian: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    // ==================== TEACHER ATTENDANCE ====================
    
    suspend fun getTodaySchedules(token: String): Result<TodaySchedulesResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getTodaySchedules("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal mengambil jadwal hari ini: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    suspend fun getAllSchedules(token: String, tanggal: String? = null, kelas: String? = null): Result<TodaySchedulesResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAllSchedules("Bearer $token", tanggal, kelas)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal mengambil semua jadwal: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    suspend fun getTodayAttendance(token: String): Result<TodayAttendanceResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getTodayAttendance("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal mengambil kehadiran hari ini: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    suspend fun getAttendanceStatistics(
        token: String, 
        startDate: String? = null, 
        endDate: String? = null, 
        guruId: Int? = null
    ): Result<AttendanceStatistics> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAttendanceStatistics("Bearer $token", startDate, endDate, guruId)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal mengambil statistik: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    suspend fun getTeacherAttendances(
        token: String,
        tanggal: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        guruId: Int? = null,
        status: String? = null,
        kelas: String? = null,
        mataPelajaran: String? = null,
        page: Int? = null,
        perPage: Int? = null
    ): Result<PaginatedResponse<TeacherAttendance>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getTeacherAttendances(
                    "Bearer $token", tanggal, startDate, endDate, 
                    guruId, status, kelas, mataPelajaran, page, perPage
                )
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = "Gagal mengambil data kehadiran: ${response.code()} - $errorBody"
                    
                    // Check if it's a 403 Forbidden error related to role permissions
                    if (response.code() == 403 && errorBody?.contains("Forbidden") == true) {
                        Result.failure(Exception("Akses ditolak: Anda tidak memiliki izin untuk mengakses data kehadiran. Hubungi administrator sistem."))
                    } else {
                        Result.failure(Exception(errorMessage))
                    }
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    suspend fun createTeacherAttendance(token: String, request: TeacherAttendanceRequest): Result<TeacherAttendance> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.createTeacherAttendance("Bearer $token", request)
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.success && apiResponse.data != null) {
                        Result.success(apiResponse.data)
                    } else {
                        Result.failure(Exception(apiResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal mencatat kehadiran: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    suspend fun updateTeacherAttendance(token: String, id: Int, request: TeacherAttendanceUpdateRequest): Result<TeacherAttendance> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updateTeacherAttendance("Bearer $token", id, request)
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.success && apiResponse.data != null) {
                        Result.success(apiResponse.data)
                    } else {
                        Result.failure(Exception(apiResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal update kehadiran: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    suspend fun deleteTeacherAttendance(token: String, id: Int): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.deleteTeacherAttendance("Bearer $token", id)
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.success) {
                        Result.success(true)
                    } else {
                        Result.failure(Exception(apiResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal hapus kehadiran: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
    // ==================== LOGOUT ====================
    
    suspend fun logout(token: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.logout("Bearer $token")
                if (response.isSuccessful) {
                    Result.success(true)
                } else {
                    Result.failure(Exception("Logout gagal: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal logout: ${e.message}"))
            }
        }
    }
}