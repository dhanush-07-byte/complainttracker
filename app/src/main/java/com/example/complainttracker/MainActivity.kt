package com.example.complainttracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.complainttracker.ui.screens.*
import com.example.complainttracker.ui.theme.ComplaintTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComplaintTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ComplaintTrackerApp()
                }
            }
        }
    }
}

@Composable
fun ComplaintTrackerApp() {
    var currentScreen by remember { mutableStateOf("login") }

    when (currentScreen) {
        "login" -> {
            LoginScreen(
                onLoginSuccess = { currentScreen = "home" },
                onNavigateToSignUp = { currentScreen = "signup" }
            )
        }
        "signup" -> {
            SignUpScreen(
                onSignUpSuccess = { currentScreen = "home" },
                onNavigateToLogin = { currentScreen = "login" }
            )
        }
        "home" -> {
            HomeScreen(
                onLogout = { currentScreen = "login" },
                onAddComplaint = { currentScreen = "add_complaint" },
                onComplaintClick = { currentScreen = "home" }
            )
        }
        "add_complaint" -> {
            AddComplaintScreen(
                onComplaintAdded = { currentScreen = "home" }
            )
        }
    }
}
