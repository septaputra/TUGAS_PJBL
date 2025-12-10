package com.kelasxi.aplikasimonitoringkelas.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPrefManager private constructor(context: Context) {
    
    private val sharedPref: SharedPreferences
    
    companion object {
        private const val PREF_NAME = "monitoring_kelas_pref"
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_USER_CLASS = "user_class"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        
        @Volatile
        private var INSTANCE: SharedPrefManager? = null
        
        fun getInstance(context: Context): SharedPrefManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SharedPrefManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    init {
        sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
    
    fun saveLoginData(token: String, userId: Int, name: String, email: String, role: String, userClass: String? = null) {
        sharedPref.edit().apply {
            putString(KEY_TOKEN, token)
            putInt(KEY_USER_ID, userId)
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_ROLE, role)
            userClass?.let { putString(KEY_USER_CLASS, it) }
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }
    
    fun getToken(): String? = sharedPref.getString(KEY_TOKEN, null)
    
    fun getUserRole(): String? = sharedPref.getString(KEY_USER_ROLE, null)
    
    fun getUserName(): String? = sharedPref.getString(KEY_USER_NAME, null)
    
    fun getUserEmail(): String? = sharedPref.getString(KEY_USER_EMAIL, null)
    
    fun getUserClass(): String? = sharedPref.getString(KEY_USER_CLASS, null)
    
    fun getUserId(): Int? {
        return if (sharedPref.contains(KEY_USER_ID)) {
            sharedPref.getInt(KEY_USER_ID, 0)
        } else {
            null
        }
    }
    
    fun isLoggedIn(): Boolean = sharedPref.getBoolean(KEY_IS_LOGGED_IN, false)
    
    fun logout() {
        sharedPref.edit().clear().apply()
    }
}