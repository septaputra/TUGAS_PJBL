package com.kelasxi.aplikasimonitoringkelas.data.repository

import com.kelasxi.aplikasimonitoringkelas.data.api.ApiService
import com.kelasxi.aplikasimonitoringkelas.data.model.LoginRequest
import com.kelasxi.aplikasimonitoringkelas.data.model.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val apiService: ApiService) {
    
    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.login(LoginRequest(email, password))
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    if (loginResponse.success) {
                        Result.success(loginResponse)
                    } else {
                        Result.failure(Exception(loginResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Login gagal: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }

    suspend fun getUser(token: String): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getUser(token)
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    if (loginResponse.success) {
                        Result.success(loginResponse)
                    } else {
                        Result.failure(Exception(loginResponse.message))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Gagal mengambil data pengguna: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
            }
        }
    }
}