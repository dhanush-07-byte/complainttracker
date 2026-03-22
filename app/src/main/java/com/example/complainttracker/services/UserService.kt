package com.example.complainttracker.services

import com.example.complainttracker.models.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class UserService {
    private val db = Firebase.firestore
    private val usersCollection = db.collection("users")

    suspend fun createUserProfile(userId: String, name: String, email: String) {
        val user = User(
            userId = userId,
            name = name,
            email = email
        )
        usersCollection.document(userId).set(user).await()
    }

    suspend fun getUserProfile(userId: String): User? {
        return try {
            val document = usersCollection.document(userId).get().await()
            document.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
