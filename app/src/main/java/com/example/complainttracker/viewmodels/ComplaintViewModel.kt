package com.example.complainttracker.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.complainttracker.models.Complaint
import com.example.complainttracker.services.AuthService
import com.example.complainttracker.services.ComplaintService
import com.example.complainttracker.services.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ComplaintViewModel : ViewModel() {
    private val complaintService = ComplaintService()
    private val authService = AuthService()
    private val userService = UserService()

    private val _complaints = MutableStateFlow<List<Complaint>>(emptyList())
    val complaints: StateFlow<List<Complaint>> = _complaints.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadComplaints()
    }

    private fun loadComplaints() {
        viewModelScope.launch {
            _isLoading.value = true
            val userId = authService.getCurrentUserId()
            if (userId != null) {
                complaintService.getUserComplaints(userId)
                    .catch { _ ->
                        _isLoading.value = false
                        _complaints.value = emptyList()
                    }
                    .collect { complaintsList ->
                        _complaints.value = complaintsList
                        _isLoading.value = false
                    }
            } else {
                _isLoading.value = false
            }
        }
    }

    fun addComplaint(complaint: Complaint, callback: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val userId = authService.getCurrentUserId()
            if (userId == null) {
                _isLoading.value = false
                callback(false, "User not logged in")
                return@launch
            }
            
            val user = userService.getUserProfile(userId)

            val fullComplaint = complaint.copy(
                userId = userId,
                userName = user?.name ?: "",
                userEmail = user?.email ?: ""
            )

            val result = complaintService.createComplaint(fullComplaint)
            result.onSuccess {
                _isLoading.value = false
                callback(true, null)
            }.onFailure { error ->
                _isLoading.value = false
                callback(false, error.message)
            }
        }
    }

    fun signOut() {
        authService.signOut()
    }
}
