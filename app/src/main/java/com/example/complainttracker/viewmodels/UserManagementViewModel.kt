package com.example.complainttracker.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.complainttracker.models.User
import com.example.complainttracker.services.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserManagementViewModel : ViewModel() {
    private val userService = UserService()

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadUsers()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            userService.getAllUsers().collect { userList ->
                _users.value = userList
                _isLoading.value = false
            }
        }
    }

    fun updateRole(userId: String, newRole: String) {
        viewModelScope.launch {
            _isLoading.value = true
            userService.updateUserRole(userId, newRole)
            _isLoading.value = false
        }
    }
}
