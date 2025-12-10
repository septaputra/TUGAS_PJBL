package com.kelasxi.aplikasimonitoringkelas.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelasxi.aplikasimonitoringkelas.data.api.RetrofitClient
import com.kelasxi.aplikasimonitoringkelas.data.model.*
import com.kelasxi.aplikasimonitoringkelas.data.repository.AppRepository
import kotlinx.coroutines.launch

class MonitoringViewModel : ViewModel() {
    
    private val repository = AppRepository(RetrofitClient.apiService)
    
    var monitoringList = mutableStateOf<List<Monitoring>>(emptyList())
    var isLoading = mutableStateOf(false)
    var isSubmitting = mutableStateOf(false)
    var submitSuccess = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)
    
    fun loadMonitoring(token: String, tanggal: String? = null, kelas: String? = null, guruId: Int? = null) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            
            repository.getMonitoring(token, tanggal, kelas, guruId)
                .onSuccess { response ->
                    monitoringList.value = response.data
                }
                .onFailure { error ->
                    errorMessage.value = error.message
                }
            
            isLoading.value = false
        }
    }
    
    fun submitMonitoring(
        token: String,
        guruId: Int,
        statusHadir: String,
        catatan: String?,
        kelas: String,
        mataPelajaran: String,
        tanggal: String? = null,
        jamLaporan: String? = null
    ) {
        viewModelScope.launch {
            isSubmitting.value = true
            errorMessage.value = null
            submitSuccess.value = false
            
            val request = MonitoringRequest(
                guru_id = guruId,
                status_hadir = statusHadir,
                catatan = catatan,
                kelas = kelas,
                mata_pelajaran = mataPelajaran,
                tanggal = tanggal,
                jam_laporan = jamLaporan
            )
            
            repository.storeMonitoring(token, request)
                .onSuccess { response ->
                    submitSuccess.value = true
                }
                .onFailure { error ->
                    errorMessage.value = error.message
                }
            
            isSubmitting.value = false
        }
    }
    
    fun clearError() {
        errorMessage.value = null
    }
    
    fun clearSubmitSuccess() {
        submitSuccess.value = false
    }
    
    fun loadEmptyClassReports(token: String, tanggal: String? = null, kelas: String? = null, guruId: Int? = null) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            
            repository.getEmptyClassReports(token, tanggal, kelas, guruId)
                .onSuccess { response ->
                    monitoringList.value = response.data
                }
                .onFailure { error ->
                    errorMessage.value = error.message
                }
            
            isLoading.value = false
        }
    }
    
    fun loadEmptyClassesOnly(token: String, tanggal: String? = null, kelas: String? = null, guruId: Int? = null) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            
            repository.getEmptyClassesOnly(token, tanggal, kelas, guruId)
                .onSuccess { response ->
                    monitoringList.value = response.data
                }
                .onFailure { error ->
                    errorMessage.value = error.message
                }
            
            isLoading.value = false
        }
    }
}