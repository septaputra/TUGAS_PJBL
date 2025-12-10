package com.kelasxi.aplikasimonitoringkelas.data.repository

import com.kelasxi.aplikasimonitoringkelas.data.api.ApiService
import com.kelasxi.aplikasimonitoringkelas.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * DEPRECATED: Grade/Nilai feature has been removed from the system
 * This repository is kept for backward compatibility with existing ViewModels
 * All methods return empty/mock data
 */
class GradeRepository(private val apiService: ApiService) {

    // Get all grades - Returns empty list
    suspend fun getGrades(token: String): Result<GradesResponse> {
        return withContext(Dispatchers.IO) {
            Result.success(GradesResponse(
                success = true,
                message = "Grade feature has been removed",
                data = emptyList()
            ))
        }
    }

    // Get siswa grades - Returns empty list
    suspend fun getSiswaGrades(token: String): Result<GradesResponse> {
        return withContext(Dispatchers.IO) {
            Result.success(GradesResponse(
                success = true,
                message = "Grade feature has been removed",
                data = emptyList()
            ))
        }
    }

    // Create grade - Returns error
    suspend fun createGrade(
        token: String,
        siswaId: Int,
        mataPelajaran: String,
        kelas: String,
        kategori: String,
        nilai: Double,
        keterangan: String?
    ): Result<GradeResponse> {
        return withContext(Dispatchers.IO) {
            Result.failure(Exception("Grade feature has been removed"))
        }
    }

    // Update grade - Returns error
    suspend fun updateGrade(
        token: String,
        gradeId: Int,
        nilai: Double,
        keterangan: String?
    ): Result<GradeResponse> {
        return withContext(Dispatchers.IO) {
            Result.failure(Exception("Grade feature has been removed"))
        }
    }
}
