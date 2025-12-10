package com.kelasxi.aplikasimonitoringkelas.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelasxi.aplikasimonitoringkelas.data.api.RetrofitClient
import com.kelasxi.aplikasimonitoringkelas.data.model.User
import com.kelasxi.aplikasimonitoringkelas.data.repository.AppRepository
import kotlinx.coroutines.launch

class UsersViewModel : ViewModel() {
    
    private val repository = AppRepository(RetrofitClient.apiService)
    
    var users = mutableStateOf<List<User>>(emptyList())
    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)
    var updateSuccess = mutableStateOf(false)
    
    fun loadUsers(token: String) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            
            repository.getUsers(token)
                .onSuccess { response ->
                    users.value = response.data
                }
                .onFailure { error ->
                    errorMessage.value = error.message
                }
            
            isLoading.value = false
        }
    }
    
    fun updateUserRole(token: String, userId: Int, role: String) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            updateSuccess.value = false
            
            repository.updateUserRole(token, userId, role)
                .onSuccess { response ->
                    updateSuccess.value = true
                    // Reload users after successful update
                    loadUsers(token)
                }
                .onFailure { error ->
                    errorMessage.value = error.message
                    isLoading.value = false
                }
        }
    }
    
    fun clearError() {
        errorMessage.value = null
    }
    
    fun clearUpdateSuccess() {
        updateSuccess.value = false
    }
}