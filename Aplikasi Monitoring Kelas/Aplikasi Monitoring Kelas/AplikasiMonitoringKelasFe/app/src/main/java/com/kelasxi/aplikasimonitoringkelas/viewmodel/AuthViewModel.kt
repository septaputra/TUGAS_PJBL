package com.kelasxi.aplikasimonitoringkelas.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kelasxi.aplikasimonitoringkelas.data.api.RetrofitClient
import com.kelasxi.aplikasimonitoringkelas.data.model.LoginResponse
import com.kelasxi.aplikasimonitoringkelas.data.model.User
import com.kelasxi.aplikasimonitoringkelas.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    
    private val repository = AuthRepository(RetrofitClient.apiService)
    
    var isLoading = mutableStateOf(false)
    var loginSuccess = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)
    var user = mutableStateOf<User?>(null)
    var token = mutableStateOf<String?>(null)
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            loginSuccess.value = false
            
            // Validasi input sebelum kirim ke server
            if (!isValidEmail(email)) {
                errorMessage.value = "Format email tidak valid"
                isLoading.value = false
                return@launch
            }
            
            if (password.length < 6) {
                errorMessage.value = "Password minimal 6 karakter"
                isLoading.value = false
                return@launch
            }
            
            // Kirim request ke server Laravel
            repository.login(email, password)
                .onSuccess { response ->
                    response.data?.let { data ->
                        user.value = data.user
                        token.value = data.token
                        loginSuccess.value = true
                    } ?: run {
                        errorMessage.value = "Data login tidak valid"
                    }
                }
                .onFailure { error ->
                    errorMessage.value = error.message ?: "Login gagal"
                }
            
            isLoading.value = false
        }
    }
    
    fun clearError() {
        errorMessage.value = null
    }
    
    fun resetLoginState() {
        loginSuccess.value = false
        errorMessage.value = null
        user.value = null
        token.value = null
    }
    
    // Load user information using the token
    fun loadUser(token: String) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            
            repository.getUser("Bearer $token")
                .onSuccess { response ->
                    response.data?.let { data ->
                        user.value = data.user
                        loginSuccess.value = true
                    } ?: run {
                        errorMessage.value = "Data pengguna tidak valid"
                    }
                }
                .onFailure { error ->
                    errorMessage.value = error.message ?: "Gagal mengambil data pengguna"
                }
            
            isLoading.value = false
        }
    }
    
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}