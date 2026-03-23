package com.example.complainttracker.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.complainttracker.services.AuthService
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val authService = AuthService()

    fun signIn(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = authService.signIn(email, password)
            result.onSuccess {
                callback(true, null)
            }.onFailure { error ->
                callback(false, error.message ?: "An unknown login error occurred")
            }
        }
    }

    fun signUp(email: String, password: String, name: String, phone: String, callback: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = authService.signUp(email, password, name, phone)
            result.onSuccess {
                callback(true, null)
            }.onFailure { error ->
                callback(false, error.message ?: "An unknown registration error occurred")
            }
        }
    }

    fun signOut() {
        authService.signOut()
    }
}
