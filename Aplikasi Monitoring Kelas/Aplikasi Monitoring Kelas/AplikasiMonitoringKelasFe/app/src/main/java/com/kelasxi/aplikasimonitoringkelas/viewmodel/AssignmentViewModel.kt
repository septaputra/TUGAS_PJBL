package com.kelasxi.aplikasimonitoringkelas.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelasxi.aplikasimonitoringkelas.data.api.RetrofitClient
import com.kelasxi.aplikasimonitoringkelas.data.model.Assignment
import com.kelasxi.aplikasimonitoringkelas.data.model.AssignmentSubmission
import com.kelasxi.aplikasimonitoringkelas.data.repository.AssignmentRepository
import kotlinx.coroutines.launch
import java.io.File

class AssignmentViewModel : ViewModel() {
    private val repository = AssignmentRepository(RetrofitClient.apiService)

    val assignments = mutableStateOf<List<Assignment>>(emptyList())
    val selectedAssignment = mutableStateOf<Assignment?>(null)
    val submissions = mutableStateOf<List<AssignmentSubmission>>(emptyList())
    val isLoading = mutableStateOf(false)
    val isSubmitting = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)
    val successMessage = mutableStateOf<String?>(null)

    // Load assignments
    fun loadAssignments(
        token: String,
        kelas: String? = null,
        mataPelajaran: String? = null,
        tipe: String? = null
    ) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            val result = repository.getAssignments(token, kelas, mataPelajaran, tipe)
            
            result.onSuccess { response ->
                assignments.value = response.data
                isLoading.value = false
            }.onFailure { exception ->
                errorMessage.value = exception.message
                isLoading.value = false
            }
        }
    }

    // Load assignment detail
    fun loadAssignmentDetail(token: String, id: Int) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            val result = repository.getAssignmentDetail(token, id)
            
            result.onSuccess { response ->
                selectedAssignment.value = response.data
                isLoading.value = false
            }.onFailure { exception ->
                errorMessage.value = exception.message
                isLoading.value = false
            }
        }
    }

    // Create assignment (Guru only)
    fun createAssignment(
        token: String,
        kelas: String,
        mataPelajaran: String,
        judul: String,
        deskripsi: String,
        deadline: String,
        tipe: String,
        bobot: Int,
        file: File?
    ) {
        viewModelScope.launch {
            isSubmitting.value = true
            errorMessage.value = null
            successMessage.value = null

            val result = repository.createAssignment(
                token, kelas, mataPelajaran, judul, deskripsi, 
                deadline, tipe, bobot, file
            )
            
            result.onSuccess { response ->
                successMessage.value = "Tugas berhasil dibuat"
                isSubmitting.value = false
                // Reload assignments
                loadAssignments(token)
            }.onFailure { exception ->
                errorMessage.value = exception.message
                isSubmitting.value = false
            }
        }
    }

    // Submit assignment (Siswa)
    fun submitAssignment(
        token: String,
        assignmentId: Int,
        keterangan: String?,
        file: File
    ) {
        viewModelScope.launch {
            isSubmitting.value = true
            errorMessage.value = null
            successMessage.value = null

            val result = repository.submitAssignment(token, assignmentId, keterangan, file)
            
            result.onSuccess { response ->
                successMessage.value = "Tugas berhasil dikumpulkan"
                isSubmitting.value = false
                // Reload assignment detail to update submission status
                loadAssignmentDetail(token, assignmentId)
            }.onFailure { exception ->
                errorMessage.value = exception.message
                isSubmitting.value = false
            }
        }
    }

    // Load submissions for assignment (Guru)
    fun loadSubmissions(token: String, assignmentId: Int) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            val result = repository.getAssignmentSubmissions(token, assignmentId)
            
            result.onSuccess { response ->
                submissions.value = response.data
                isLoading.value = false
            }.onFailure { exception ->
                errorMessage.value = exception.message
                isLoading.value = false
            }
        }
    }

    // Clear messages
    fun clearMessages() {
        errorMessage.value = null
        successMessage.value = null
    }

    fun clearError() {
        errorMessage.value = null
    }

    fun clearSuccess() {
        successMessage.value = null
    }
}
