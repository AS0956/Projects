package com.example.final_ui_skeleton.data

import com.example.final_ui_skeleton.model.Contribution
import com.example.final_ui_skeleton.model.MemberPlan
import com.example.final_ui_skeleton.model.SavingsGoal
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Calendar

object SharedGoalsRepository {

    private val db get() = Firebase.firestore
    private val uid get() = Firebase.auth.currentUser?.uid
    private val goals get() = db.collection("sharedGoals")

    fun monthsRemaining(deadlineMonth: Int, deadlineYear: Int): Int {
        val now = Calendar.getInstance()
        val nowMonth = now.get(Calendar.MONTH) + 1
        val nowYear = now.get(Calendar.YEAR)
        return ((deadlineYear - nowYear) * 12 + (deadlineMonth - nowMonth)).coerceAtLeast(1)
    }

    suspend fun createGoal(
        name: String, target: Double, groupName: String,
        deadlineMonth: Int, deadlineYear: Int
    ): Result<String> {
        val myUid = uid ?: return Result.failure(Exception("Not signed in"))
        return try {
            val code = generateInviteCode()
            val goal = SavingsGoal(
                name = name, groupName = groupName, targetAmount = target,
                currentAmount = 0.0, ownerUid = myUid, members = listOf(myUid),
                inviteCode = code, createdAt = System.currentTimeMillis(),
                deadlineMonth = deadlineMonth, deadlineYear = deadlineYear
            )
            goals.add(goal).await()
            Result.success(code)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateGoal(goalId: String, name: String, target: Double, groupName: String, deadlineMonth: Int, deadlineYear: Int): Result<Unit> {
        return try {
            goals.document(goalId).update(mapOf(
                "name" to name, "targetAmount" to target,
                "groupName" to groupName,
                "deadlineMonth" to deadlineMonth,
                "deadlineYear" to deadlineYear
            )).await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun deleteGoal(goalId: String): Result<Unit> {
        return try { goals.document(goalId).delete().await(); Result.success(Unit) }
        catch (e: Exception) { Result.failure(e) }
    }

    suspend fun joinGoal(inviteCode: String): Result<Unit> {
        val myUid = uid ?: return Result.failure(Exception("Not signed in"))
        return try {
            val snap = goals.whereEqualTo("inviteCode", inviteCode.uppercase().trim()).limit(1).get().await()
            val doc = snap.documents.firstOrNull() ?: return Result.failure(Exception("No goal found"))
            doc.reference.update("members", FieldValue.arrayUnion(myUid)).await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    /**
     * Member sets their personal plan:
     * - totalCommitment: total $ they plan to contribute by deadline
     * - savingsPct: % of their monthly surplus they'll allocate
     * - monthlyAmount: auto-calculated from totalCommitment / monthsRemaining
     */
    suspend fun setMemberPlan(
        goalId: String,
        totalCommitment: Double,
        savingsPct: Double,
        deadlineMonth: Int,
        deadlineYear: Int,
        userName: String,
        userSavedAmount: Double
    ): Result<Unit> {
        val myUid = uid ?: return Result.failure(Exception("Not signed in"))
        return try {
            val months = monthsRemaining(deadlineMonth, deadlineYear)
            val monthlyAmount = if (months > 0) totalCommitment / months else totalCommitment
            val plan = MemberPlan(
                totalCommitment = totalCommitment,
                monthlyAmount = monthlyAmount,
                savingsPct = savingsPct
            )
            goals.document(goalId).update("memberPlans.$myUid", plan).await()

            // calculate this month's contribution from savings %
            val thisMonthContrib = userSavedAmount * (savingsPct / 100.0)

            // replace old contributions from this user
            val oldContribs = goals.document(goalId).collection("contributions")
                .whereEqualTo("uid", myUid).get().await()
            val batch = db.batch()
            oldContribs.documents.forEach { batch.delete(it.reference) }
            batch.commit().await()

            if (thisMonthContrib > 0) {
                val contrib = Contribution(uid = myUid, userName = userName, amount = thisMonthContrib, timestamp = System.currentTimeMillis())
                goals.document(goalId).collection("contributions").add(contrib).await()
            }

            // recalculate total
            val all = goals.document(goalId).collection("contributions").get().await()
            val total = all.documents.sumOf { it.getDouble("amount") ?: 0.0 }
            goals.document(goalId).update("currentAmount", total).await()

            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun contribute(goalId: String, amount: Double, userName: String): Result<Unit> {
        val myUid = uid ?: return Result.failure(Exception("Not signed in"))
        return try {
            val contribution = Contribution(uid = myUid, userName = userName, amount = amount, timestamp = System.currentTimeMillis())
            goals.document(goalId).collection("contributions").add(contribution).await()
            goals.document(goalId).update("currentAmount", FieldValue.increment(amount)).await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    fun myGoalsFlow(): Flow<List<Pair<String, SavingsGoal>>> = callbackFlow {
        val myUid = uid
        if (myUid == null) { trySend(emptyList()); awaitClose { }; return@callbackFlow }
        // listen to ALL shared goals — filter client-side so any member joining or
        // any plan change on any document triggers a real-time update for everyone
        val reg = goals.addSnapshotListener { snap, _ ->
            val list = snap?.documents?.mapNotNull { d ->
                try {
                    val members = (d.get("members") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
                    if (myUid !in members) return@mapNotNull null
                    @Suppress("UNCHECKED_CAST")
                    val rawPlans = d.get("memberPlans") as? Map<String, Map<String, Any>> ?: emptyMap()
                    val memberPlans = rawPlans.mapValues { (_, v) ->
                        MemberPlan(
                            totalCommitment = (v["totalCommitment"] as? Number)?.toDouble() ?: 0.0,
                            monthlyAmount = (v["monthlyAmount"] as? Number)?.toDouble() ?: 0.0,
                            savingsPct = (v["savingsPct"] as? Number)?.toDouble() ?: 0.0
                        )
                    }
                    val goal = SavingsGoal(
                        name = d.getString("name") ?: "",
                        groupName = d.getString("groupName") ?: "",
                        targetAmount = d.getDouble("targetAmount") ?: 0.0,
                        currentAmount = d.getDouble("currentAmount") ?: 0.0,
                        ownerUid = d.getString("ownerUid") ?: "",
                        members = members,
                        inviteCode = d.getString("inviteCode") ?: "",
                        createdAt = d.getLong("createdAt") ?: 0L,
                        deadlineMonth = (d.getLong("deadlineMonth") ?: 0L).toInt(),
                        deadlineYear = (d.getLong("deadlineYear") ?: 0L).toInt(),
                        memberPlans = memberPlans
                    )
                    d.id to goal
                } catch (e: Exception) { null }
            } ?: emptyList()
            trySend(list)
        }
        awaitClose { reg.remove() }
    }

    fun contributionsFlow(goalId: String): Flow<List<Contribution>> = callbackFlow {
        val reg = goals.document(goalId).collection("contributions")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, _ ->
                val list = snap?.documents?.mapNotNull { it.toObject(Contribution::class.java) } ?: emptyList()
                trySend(list)
            }
        awaitClose { reg.remove() }
    }

    private fun generateInviteCode(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        return (1..6).map { chars.random() }.joinToString("")
    }
}