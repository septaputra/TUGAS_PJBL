package com.kelasxi.aplikasimonitoringkelas.data.repository

import com.kelasxi.aplikasimonitoringkelas.data.api.ApiService
import com.kelasxi.aplikasimonitoringkelas.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * DEPRECATED: Assignment feature has been removed from the system
 * This repository is kept for backward compatibility with existing ViewModels
 * All methods return empty/mock data
 */
class AssignmentRepository(private val apiService: ApiService) {

    // Get all assignments - Returns empty list
    suspend fun getAssignments(
        token: String,
        kelas: String? = null,
        mataPelajaran: String? = null,
        tipe: String? = null
    ): Result<AssignmentsResponse> {
        return withContext(Dispatchers.IO) {
            Result.success(AssignmentsResponse(
                success = true,
                message = "Assignment feature has been removed",
                data = emptyList()
            ))
        }
    }

    // Get assignment detail - Returns error
    suspend fun getAssignmentDetail(token: String, id: Int): Result<AssignmentResponse> {
        return withContext(Dispatchers.IO) {
            Result.failure(Exception("Assignment feature has been removed"))
        }
    }

    // Create assignment - Returns error
    suspend fun createAssignment(
        token: String,
        kelas: String,
        mataPelajaran: String,
        judul: String,
        deskripsi: String,
        deadline: String,
        tipe: String,
        bobot: Int,
        file: File?
    ): Result<AssignmentResponse> {
        return withContext(Dispatchers.IO) {
            Result.failure(Exception("Assignment feature has been removed"))
        }
    }

    // Submit assignment - Returns error
    suspend fun submitAssignment(
        token: String,
        assignmentId: Int,
        keterangan: String?,
        file: File
    ): Result<SubmissionResponse> {
        return withContext(Dispatchers.IO) {
            Result.failure(Exception("Assignment feature has been removed"))
        }
    }

    // Get submissions - Returns empty list
    suspend fun getAssignmentSubmissions(token: String, assignmentId: Int): Result<SubmissionsResponse> {
        return withContext(Dispatchers.IO) {
            Result.success(SubmissionsResponse(
                success = true,
                message = "Assignment feature has been removed",
                data = emptyList()
            ))
        }
    }
}
