package com.example.complainttracker.models

data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = "user",
    val createdAt: Long = System.currentTimeMillis()
)
