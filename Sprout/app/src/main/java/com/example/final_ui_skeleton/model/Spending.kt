package com.example.final_ui_skeleton.model

data class Spending(
    val date: String = "",
    val merchant: String = "",
    val category: String = "",
    val perOrShared: String = "",
    val amount: Double = 0.0,
    val description: String = "",
    val receiptText: String = "",
    val receiptImagePath: String = ""
)