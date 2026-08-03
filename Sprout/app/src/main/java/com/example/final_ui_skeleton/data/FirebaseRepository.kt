package com.example.final_ui_skeleton.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.example.final_ui_skeleton.model.User
import com.example.final_ui_skeleton.model.Spending
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

object FirebaseRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    val currentUserId: String? get() = auth.currentUser?.uid
    val isLoggedIn: Boolean get() = auth.currentUser != null

    suspend fun signUp(email: String, password: String): Result<String> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(result.user!!.uid)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun signIn(email: String, password: String): Result<String> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user!!.uid)
        } catch (e: Exception) { Result.failure(e) }
    }

    fun signOut() = auth.signOut()

    suspend fun updatePassword(newPassword: String): Result<Unit> {
        val user = auth.currentUser ?: return Result.failure(Exception("Not logged in"))
        return try {
            user.updatePassword(newPassword).await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun saveUserProfile(user: User): Result<Unit> {
        val uid = currentUserId ?: return Result.failure(Exception("Not logged in"))
        return try {
            val data = mapOf(
                "name" to user.name,
                "city" to user.city,
                "email" to user.email,
                "incomeBracket" to user.incomeBracket,
                "occupation" to user.occupation,
                "purpose" to user.purpose,
                "savingsTarget" to user.savingsTarget,
                "savedAmount" to user.savedAmount,
                "monthlyBudget" to user.monthlyBudget,
                "monthlyBudgetName" to user.monthlyBudgetName,
                "createdAt" to com.google.firebase.Timestamp.now()
            )
            db.collection("users").document(uid).set(data).await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getUserProfile(): Result<User> {
        val uid = currentUserId ?: return Result.failure(Exception("Not logged in"))
        return try {
            val doc = db.collection("users").document(uid).get().await()
            Result.success(User(
                name = doc.getString("name") ?: "",
                city = doc.getString("city") ?: "",
                email = doc.getString("email") ?: "",
                password = "",
                incomeBracket = doc.getString("incomeBracket") ?: "",
                occupation = doc.getString("occupation") ?: "",
                purpose = doc.getString("purpose") ?: "",
                savingsTarget = doc.getDouble("savingsTarget") ?: 0.0,
                savedAmount = doc.getDouble("savedAmount") ?: 0.0,
                monthlyBudget = doc.getDouble("monthlyBudget") ?: 1200.0,
                monthlyBudgetName = doc.getString("monthlyBudgetName") ?: ""
            ))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateUserProfile(field: String, value: String): Result<Unit> {
        val uid = currentUserId ?: return Result.failure(Exception("Not logged in"))
        return try {
            db.collection("users").document(uid).update(field, value).await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateUserProfileDouble(field: String, value: Double): Result<Unit> {
        val uid = currentUserId ?: return Result.failure(Exception("Not logged in"))
        return try {
            db.collection("users").document(uid).update(field, value).await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun addExpense(spending: Spending): Result<Unit> {
        val uid = currentUserId ?: return Result.failure(Exception("Not logged in"))
        return try {
            val data = mapOf(
                "date" to spending.date,
                "merchant" to spending.merchant,
                "category" to spending.category,
                "perOrShared" to spending.perOrShared,
                "amount" to spending.amount,
                "description" to spending.description,
                "receiptText" to spending.receiptText,
                "receiptImagePath" to spending.receiptImagePath,
                "createdAt" to com.google.firebase.Timestamp.now()
            )
            db.collection("users").document(uid).collection("expenses").add(data).await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateExpense(expenseId: String, spending: Spending): Result<Unit> {
        val uid = currentUserId ?: return Result.failure(Exception("Not logged in"))
        return try {
            val data = mapOf(
                "date" to spending.date,
                "merchant" to spending.merchant,
                "category" to spending.category,
                "perOrShared" to spending.perOrShared,
                "amount" to spending.amount,
                "description" to spending.description,
                "receiptText" to spending.receiptText,
                "receiptImagePath" to spending.receiptImagePath
            )
            db.collection("users").document(uid).collection("expenses").document(expenseId).update(data).await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun deleteExpense(expenseId: String): Result<Unit> {
        val uid = currentUserId ?: return Result.failure(Exception("Not logged in"))
        return try {
            db.collection("users").document(uid).collection("expenses").document(expenseId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun saveChatMessages(messages: List<Map<String, Any>>): Result<Unit> {
        val uid = currentUserId ?: return Result.failure(Exception("Not logged in"))
        return try {
            db.collection("users").document(uid).collection("chat").document("history")
                .set(mapOf("messages" to messages)).await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun loadChatMessages(): Result<List<Map<String, Any>>> {
        val uid = currentUserId ?: return Result.failure(Exception("Not logged in"))
        return try {
            val doc = db.collection("users").document(uid).collection("chat").document("history").get().await()
            @Suppress("UNCHECKED_CAST")
            val raw = doc.get("messages") as? List<Map<String, Any>> ?: emptyList()
            Result.success(raw)
        } catch (e: Exception) { Result.success(emptyList()) }
    }

    fun getExpensesFlow(): Flow<List<Pair<String, Spending>>> = callbackFlow {
        val uid = currentUserId ?: run { trySend(emptyList()); close(); return@callbackFlow }
        val listener = db.collection("users").document(uid)
            .collection("expenses")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val list = snapshot?.documents?.mapNotNull { doc ->
                    Pair(doc.id, Spending(
                        date = doc.getString("date") ?: "",
                        merchant = doc.getString("merchant") ?: "",
                        category = doc.getString("category") ?: "",
                        perOrShared = doc.getString("perOrShared") ?: "",
                        amount = doc.getDouble("amount") ?: 0.0,
                        description = doc.getString("description") ?: "",
                        receiptText = doc.getString("receiptText") ?: "",
                        receiptImagePath = doc.getString("receiptImagePath") ?: ""
                    ))
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }
}