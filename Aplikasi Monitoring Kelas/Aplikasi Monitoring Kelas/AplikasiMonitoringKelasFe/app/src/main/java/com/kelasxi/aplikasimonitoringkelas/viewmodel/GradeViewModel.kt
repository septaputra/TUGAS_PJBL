package com.kelasxi.aplikasimonitoringkelas.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelasxi.aplikasimonitoringkelas.data.api.RetrofitClient
import com.kelasxi.aplikasimonitoringkelas.data.model.Grade
import com.kelasxi.aplikasimonitoringkelas.data.model.GradeStatistics
import com.kelasxi.aplikasimonitoringkelas.data.repository.GradeRepository
import kotlinx.coroutines.launch

/**
 * DEPRECATED: Grade/Nilai feature has been removed
 * This ViewModel is kept for backward compatibility
 */
class GradeViewModel : ViewModel() {
    private val repository = GradeRepository(RetrofitClient.apiService)

    val grades = mutableStateOf<List<Grade>>(emptyList())
    val statistics = mutableStateOf<GradeStatistics?>(null)
    val isLoading = mutableStateOf(false)
    val isSubmitting = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)
    val successMessage = mutableStateOf<String?>(null)

    // Load grades - Returns empty list (feature removed)
    fun loadGrades(
        token: String,
        mataPelajaran: String? = null,
        kelas: String? = null
    ) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            val result = repository.getGrades(token)
            
            result.onSuccess { response ->
                grades.value = response.data
                isLoading.value = false
            }.onFailure { exception ->
                errorMessage.value = exception.message
                isLoading.value = false
            }
        }
    }

    // Load grades for specific siswa - Returns empty list (feature removed)
    fun loadSiswaGrades(token: String, siswaId: Int) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            val result = repository.getSiswaGrades(token)
            
            result.onSuccess { response ->
                grades.value = response.data
                isLoading.value = false
            }.onFailure { exception ->
                errorMessage.value = exception.message
                isLoading.value = false
            }
        }
    }

    // Create grade - Does nothing (feature removed)
    fun createGrade(
        token: String,
        siswaId: Int,
        mataPelajaran: String,
        kelas: String,
        kategori: String,
        nilai: Double,
        keterangan: String?
    ) {
        viewModelScope.launch {
            isSubmitting.value = true
            errorMessage.value = null
            successMessage.value = null

            val result = repository.createGrade(token, siswaId, mataPelajaran, kelas, kategori, nilai, keterangan)
            
            result.onSuccess { response ->
                successMessage.value = "Nilai berhasil disimpan"
                isSubmitting.value = false
                loadGrades(token)
            }.onFailure { exception ->
                errorMessage.value = exception.message
                isSubmitting.value = false
            }
        }
    }

    // Update grade - Does nothing (feature removed)
    fun updateGrade(
        token: String,
        gradeId: Int,
        nilai: Double,
        keterangan: String?
    ) {
        viewModelScope.launch {
            isSubmitting.value = true
            errorMessage.value = null
            successMessage.value = null

            val result = repository.updateGrade(token, gradeId, nilai, keterangan)
            
            result.onSuccess { _ ->
                successMessage.value = "Nilai berhasil diupdate"
                isSubmitting.value = false
                loadGrades(token)
            }.onFailure { exception ->
                errorMessage.value = exception.message
                isSubmitting.value = false
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
