package com.example.final_ui_skeleton.model

data class User(
    val name: String = "",
    val city: String = "",
    val email: String = "",
    val password: String = "",
    val incomeBracket: String = "",
    val occupation: String = "",
    val purpose: String = "",
    val savingsGoals: List<String> = emptyList(),
    val savingsTarget: Double = 0.0,
    val savedAmount: Double = 0.0,
    val monthlyBudget: Double = 1200.0,
    val monthlyBudgetName: String = ""
)