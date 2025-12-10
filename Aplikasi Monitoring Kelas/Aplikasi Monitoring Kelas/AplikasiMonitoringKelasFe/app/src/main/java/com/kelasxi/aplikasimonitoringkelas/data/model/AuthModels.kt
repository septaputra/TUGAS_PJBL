package com.kelasxi.aplikasimonitoringkelas.data.model

import com.google.gson.annotations.JsonAdapter

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: LoginData?
)

data class LoginData(
    val user: User,
    val token: String,
    val token_type: String,
    val expires_at: String?
)

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    val mata_pelajaran: String? = null,
    val class_id: Int? = null,
    val kelas: String? = null,  // The class name when included in the response
    @JsonAdapter(BooleanTypeAdapter::class)
    val is_banned: Boolean = false,
    val created_at: String,
    val updated_at: String
)

data class CreateUserRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String,
    val mata_pelajaran: String? = null
)

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val errors: Map<String, List<String>>? = null
)