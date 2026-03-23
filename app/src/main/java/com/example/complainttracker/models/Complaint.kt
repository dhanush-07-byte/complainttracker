package com.example.complainttracker.models

import java.util.Date

data class Complaint(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val status: String = "Pending", // Pending, In Progress, Resolved, Rejected
    val priority: String = "Medium",
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val imageUrl: String? = null,
    val adminRemarks: String = "",
    val adminReply: String = "",
    val userFeedback: String = "",
    val rating: Int = 0, // Feedback rating
    val isAlerted: Boolean = false,
    val lastUnattendedAlertAt: Date? = null
)
