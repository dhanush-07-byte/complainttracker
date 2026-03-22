package com.example.complainttracker.models

import java.util.Date

data class Complaint(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val status: String = "Pending",
    val priority: String = "Medium",
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val imageUrl: String = "",
    val remarks: String = ""
)
