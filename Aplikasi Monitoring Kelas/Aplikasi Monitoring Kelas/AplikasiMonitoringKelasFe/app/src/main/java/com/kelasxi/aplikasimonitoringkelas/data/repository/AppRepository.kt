package com.kelasxi.aplikasimonitoringkelas.data.repository

import com.kelasxi.aplikasimonitoringkelas.data.api.ApiService
import com.kelasxi.aplikasimonitoringkelas.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppRepository(private val apiService: ApiService) {
    
    // Jadwal Pelajaran
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
    
    // Users Management (Admin Only)
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
    
    suspend fun updateUserRole(token: String, userId: Int, role: String): Result<ApiResponse<User>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updateUserRole("Bearer $token", userId, UpdateRoleRequest(role))
                if (response.isSuccessful && response.body() != null) {
                    val updateResponse = response.body()!!
                    if (updateResponse.success) {
                        Result.success(updateResponse)
                    } else {
                        Result.failure(Exception(updateResponse.message))
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
    
    // Monitoring
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
                    Result.failure(Exception("Gagal mengambil data monitoring: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
    
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
    
    // Logout
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
                    Result.failure(Exception("Gagal mengambil laporan kelas kosong: ${response.code()} - $errorBody"))
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
                    Result.failure(Exception("Gagal mengambil data kelas kosong: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
}