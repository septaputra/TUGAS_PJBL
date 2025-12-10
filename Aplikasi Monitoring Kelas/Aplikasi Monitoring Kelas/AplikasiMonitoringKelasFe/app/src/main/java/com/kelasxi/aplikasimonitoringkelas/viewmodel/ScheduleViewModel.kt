package com.kelasxi.aplikasimonitoringkelas.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelasxi.aplikasimonitoringkelas.data.api.RetrofitClient
import com.kelasxi.aplikasimonitoringkelas.data.model.*
import com.kelasxi.aplikasimonitoringkelas.data.repository.AppRepository
import kotlinx.coroutines.launch

class ScheduleViewModel : ViewModel() {
    
    private val repository = AppRepository(RetrofitClient.apiService)
    
    var schedules = mutableStateOf<List<Schedule>>(emptyList())
    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)
    
    fun loadSchedules(token: String, hari: String? = null, kelas: String? = null) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            
            repository.getSchedules(token, hari, kelas)
                .onSuccess { response ->
                    schedules.value = response.data
                }
                .onFailure { error ->
                    errorMessage.value = error.message
                }
            
            isLoading.value = false
        }
    }
    
    fun clearError() {
        errorMessage.value = null
    }
}