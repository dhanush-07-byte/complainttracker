package com.example.complainttracker.services

import com.example.complainttracker.models.Complaint
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date

class ComplaintService {
    private val db = Firebase.firestore
    private val complaintsCollection = db.collection("complaints")

    suspend fun createComplaint(complaint: Complaint): Result<String> {
        return try {
            val docRef = complaintsCollection.document()
            val complaintWithId = complaint.copy(
                id = docRef.id,
                createdAt = Date(),
                updatedAt = Date()
            )
            docRef.set(complaintWithId).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getUserComplaints(userId: String): Flow<List<Complaint>> = callbackFlow {
        val subscription = complaintsCollection
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val complaints = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Complaint::class.java)
                } ?: emptyList()

                trySend(complaints)
            }

        awaitClose {
            subscription.remove()
        }
    }

    fun getAllComplaints(): Flow<List<Complaint>> = callbackFlow {
        val subscription = complaintsCollection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val complaints = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Complaint::class.java)
                } ?: emptyList()

                trySend(complaints)
            }

        awaitClose {
            subscription.remove()
        }
    }

    suspend fun updateComplaintStatus(complaintId: String, status: String, adminRemarks: String? = null): Result<Unit> {
        return try {
            val updates = mutableMapOf<String, Any>(
                "status" to status,
                "updatedAt" to Date()
            )
            adminRemarks?.let { updates["adminRemarks"] = it }
            complaintsCollection.document(complaintId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addAdminReply(complaintId: String, reply: String): Result<Unit> {
        return try {
            val updates = mapOf(
                "adminReply" to reply,
                "updatedAt" to Date()
            )
            complaintsCollection.document(complaintId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addUserFeedback(complaintId: String, feedback: String, rating: Int): Result<Unit> {
        return try {
            val updates = mapOf(
                "userFeedback" to feedback,
                "rating" to rating,
                "updatedAt" to Date()
            )
            complaintsCollection.document(complaintId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markAsAlerted(complaintId: String): Result<Unit> {
        return try {
            val updates = mapOf(
                "isAlerted" to true,
                "lastUnattendedAlertAt" to Date()
            )
            complaintsCollection.document(complaintId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
