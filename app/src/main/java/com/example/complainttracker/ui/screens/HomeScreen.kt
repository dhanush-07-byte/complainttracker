package com.example.complainttracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.complainttracker.models.Complaint
import com.example.complainttracker.viewmodels.ComplaintViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onAddComplaint: () -> Unit,
    onComplaintClick: (Complaint) -> Unit,
    onManageUsers: () -> Unit,
    viewModel: ComplaintViewModel = viewModel()
) {
    val complaints by viewModel.complaints.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val role by viewModel.currentUserRole.collectAsState()
    val scope = rememberCoroutineScope()
    val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(if (role == "admin" || role == "manager") "All Complaints" else "My Complaints") 
                },
                actions = {
                    if (role == "admin") {
                        IconButton(onClick = onManageUsers) {
                            Icon(Icons.Default.Person, contentDescription = "Manage Users")
                        }
                    }
                    IconButton(onClick = {
                        scope.launch {
                            viewModel.signOut()
                            onLogout()
                        }
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            if (role == "student") {
                FloatingActionButton(onClick = onAddComplaint) {
                    Icon(Icons.Default.Add, contentDescription = "Add Complaint")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                complaints.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No complaints found")
                        if (role == "student") {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = onAddComplaint) {
                                Text("Register a Complaint")
                            }
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(complaints) { complaint ->
                            Card(
                                onClick = { onComplaintClick(complaint) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = complaint.title,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                            if (role == "admin" || role == "manager") {
                                                Text(
                                                    text = "By: ${complaint.userName}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.secondary
                                                )
                                            }
                                        }
                                        AssistChip(
                                            onClick = {},
                                            label = { Text(complaint.status) }
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = complaint.description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 1
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${complaint.category} • ${complaint.priority}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = dateFormat.format(complaint.createdAt),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                    
                                    if (complaint.isAlerted) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "⚠️ Unattended Alert",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
