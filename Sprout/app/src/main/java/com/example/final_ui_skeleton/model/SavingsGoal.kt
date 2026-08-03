package com.example.final_ui_skeleton.model

data class SavingsGoal(
    val name: String = "",
    val groupName: String = "",
    val targetAmount: Double = 0.0,       // total group goal e.g. $4,000
    val currentAmount: Double = 0.0,      // sum of all contributions so far
    val ownerUid: String = "",
    val members: List<String> = emptyList(),
    val inviteCode: String = "",
    val createdAt: Long = 0L,
    val deadlineMonth: Int = 0,           // e.g. 11 for November
    val deadlineYear: Int = 0,            // e.g. 2026
    // uid -> MemberPlan (their personal commitment)
    val memberPlans: Map<String, MemberPlan> = emptyMap()
)

data class MemberPlan(
    val totalCommitment: Double = 0.0,    // e.g. $1,000 total they plan to contribute
    val monthlyAmount: Double = 0.0,      // auto-calculated: totalCommitment / monthsRemaining
    val savingsPct: Double = 0.0          // % of their monthly surplus they allocate
)

data class Contribution(
    val uid: String = "",
    val userName: String = "",
    val amount: Double = 0.0,
    val timestamp: Long = 0L
)