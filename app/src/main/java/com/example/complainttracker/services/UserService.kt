package com.example.complainttracker.services

import com.example.complainttracker.models.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UserService {
    private val db = Firebase.firestore
    private val usersCollection = db.collection("users")

    suspend fun createUserProfile(userId: String, name: String, email: String, phone: String, role: String = "student") {
        val user = User(
            userId = userId,
            name = name,
            email = email,
            phone = phone,
            role = role,
            createdAt = System.currentTimeMillis()
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

    fun getAllUsers(): Flow<List<User>> = callbackFlow {
        val subscription = usersCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val users = snapshot?.documents?.mapNotNull { it.toObject(User::class.java) } ?: emptyList()
            trySend(users)
        }
        awaitClose { subscription.remove() }
    }

    suspend fun updateUserRole(userId: String, newRole: String): Result<Unit> {
        return try {
            usersCollection.document(userId).update("role", newRole).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
