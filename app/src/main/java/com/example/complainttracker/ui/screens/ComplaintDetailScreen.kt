package com.example.complainttracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.complainttracker.models.Complaint
import com.example.complainttracker.viewmodels.ComplaintViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplaintDetailScreen(
    complaint: Complaint,
    onBack: () -> Unit,
    viewModel: ComplaintViewModel = viewModel()
) {
    val role by viewModel.currentUserRole.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val scrollState = rememberScrollState()
    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

    var showStatusDialog by remember { mutableStateOf(false) }
    var adminRemarks by remember { mutableStateOf(complaint.adminRemarks) }
    var adminReply by remember { mutableStateOf(complaint.adminReply) }
    var userFeedback by remember { mutableStateOf(complaint.userFeedback) }
    var rating by remember { mutableIntStateOf(complaint.rating) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Complaint Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
                // Status Header
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = when (complaint.status) {
                            "Resolved" -> Color(0xFFE8F5E9)
                            "In Progress" -> Color(0xFFE3F2FD)
                            "Pending" -> Color(0xFFFFF3E0)
                            else -> Color(0xFFFFEBEE)
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Status", style = MaterialTheme.typography.labelMedium)
                            Text(
                                text = complaint.status,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (role == "admin" || role == "manager") {
                            Button(onClick = { showStatusDialog = true }) {
                                Text("Update Status")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Alert Student
                if (role == "student" && complaint.status == "Pending") {
                    val days = (Date().time - complaint.createdAt.time) / (1000 * 60 * 60 * 24)
                    if (days >= 1 && !complaint.isAlerted) {
                        Button(
                            onClick = { viewModel.alertUnattended(complaint.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Alert: Unattended for ${days} days")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // Complaint Info
                Text(text = "Details", style = MaterialTheme.typography.titleMedium)
                Text(text = complaint.title, style = MaterialTheme.typography.headlineSmall)
                Text(text = "Category: ${complaint.category}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Priority: ${complaint.priority}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Created: ${dateFormat.format(complaint.createdAt)}", style = MaterialTheme.typography.bodySmall)

                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Description", style = MaterialTheme.typography.titleSmall)
                Text(text = complaint.description, style = MaterialTheme.typography.bodyLarge)

                if (complaint.imageUrl != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Image attached", color = MaterialTheme.colorScheme.primary)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Admin Section
                if (complaint.adminRemarks.isNotEmpty()) {
                    Text(text = "Admin Remarks", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)
                    Text(text = complaint.adminRemarks, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (role == "admin") {
                    OutlinedTextField(
                        value = adminReply,
                        onValueChange = { adminReply = it },
                        label = { Text("Reply to user") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = { viewModel.addReply(complaint.id, adminReply) },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Send Reply")
                    }
                } else if (complaint.adminReply.isNotEmpty()) {
                    Text(text = "Admin Reply", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                    Text(text = complaint.adminReply, style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Feedback Section
                if (complaint.status == "Resolved" && role == "student") {
                    Text(text = "Feedback", style = MaterialTheme.typography.titleSmall)
                    OutlinedTextField(
                        value = userFeedback,
                        onValueChange = { userFeedback = it },
                        label = { Text("Your feedback") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Rating: ")
                        (1..5).forEach { i ->
                            TextButton(onClick = { rating = i }) {
                                Text(text = if (i <= rating) "★" else "☆", style = MaterialTheme.typography.headlineSmall)
                            }
                        }
                    }
                    Button(
                        onClick = { viewModel.submitFeedback(complaint.id, userFeedback, rating) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Submit Feedback")
                    }
                } else if (complaint.userFeedback.isNotEmpty()) {
                    Text(text = "User Feedback", style = MaterialTheme.typography.titleSmall)
                    Text(text = "Rating: ${complaint.rating}/5")
                    Text(text = complaint.userFeedback, style = MaterialTheme.typography.bodyMedium)
                }
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }

    if (showStatusDialog) {
        var selectedStatus by remember { mutableStateOf(complaint.status) }
        AlertDialog(
            onDismissRequest = { showStatusDialog = false },
            title = { Text("Update Progress") },
            text = {
                Column {
                    listOf("Pending", "In Progress", "Resolved", "Rejected").forEach { status ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedStatus == status,
                                onClick = { selectedStatus = status }
                            )
                            Text(status)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = adminRemarks,
                        onValueChange = { adminRemarks = it },
                        label = { Text("Remarks") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.updateStatus(complaint.id, selectedStatus, adminRemarks)
                    showStatusDialog = false
                }) {
                    Text("Update")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStatusDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
