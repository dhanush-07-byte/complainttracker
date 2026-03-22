package com.example.complainttracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.complainttracker.models.Complaint
import com.example.complainttracker.viewmodels.ComplaintViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddComplaintScreen(
    onComplaintAdded: () -> Unit,
    viewModel: ComplaintViewModel = viewModel()
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Medium") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var expandedCategory by remember { mutableStateOf(false) }
    var expandedPriority by remember { mutableStateOf(false) }

    val categories = listOf("Technical", "Billing", "Service", "Product", "Other")
    val priorities = listOf("Low", "Medium", "High", "Critical")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Register Complaint") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = expandedCategory,
                onExpandedChange = { expandedCategory = it }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedCategory,
                    onDismissRequest = { expandedCategory = false }
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                category = cat
                                expandedCategory = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = expandedPriority,
                onExpandedChange = { expandedPriority = it }
            ) {
                OutlinedTextField(
                    value = priority,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Priority") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPriority) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedPriority,
                    onDismissRequest = { expandedPriority = false }
                ) {
                    priorities.forEach { pri ->
                        DropdownMenuItem(
                            text = { Text(pri) },
                            onClick = {
                                priority = pri
                                expandedPriority = false
                            }
                        )
                    }
                }
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (title.isBlank() || description.isBlank() || category.isBlank()) {
                        errorMessage = "Please fill all fields"
                        return@Button
                    }

                    isLoading = true
                    val complaint = Complaint(
                        title = title,
                        description = description,
                        category = category,
                        priority = priority
                    )

                    viewModel.addComplaint(complaint) { success, error ->
                        isLoading = false
                        if (success) {
                            onComplaintAdded()
                        } else {
                            errorMessage = error
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text("Submit Complaint")
                }
            }
        }
    }
}
