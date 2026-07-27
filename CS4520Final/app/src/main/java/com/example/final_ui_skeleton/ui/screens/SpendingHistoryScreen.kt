package com.example.final_ui_skeleton.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.final_ui_skeleton.model.Spending
import com.example.final_ui_skeleton.ui.components.AppBottomBar
import com.example.final_ui_skeleton.ui.components.AppColors
import com.example.final_ui_skeleton.ui.components.BudgetSpendingTabs
import com.example.final_ui_skeleton.ui.components.SpendingRow
import com.example.final_ui_skeleton.viewmodel.SproutViewModel


/**
 * 1. What: Displays the user's spending history from Firestore with swipe to delete.
 * 2. Who: Called by the NavHost when the user navigates to spending history.
 * 3. When: Executed when the spending history tab is selected.
 */
@Composable
fun SpendingHistoryScreen(
    viewModel: SproutViewModel = viewModel(),
    onNavigateDashboard: () -> Unit = {},
    onNavigateRecommend: () -> Unit = {},
    onNavigateSettings: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(1) }

    LaunchedEffect(Unit) {
        viewModel.startListeningToExpenses()
    }

    val expenses by viewModel.expenses.collectAsState()

    Scaffold(
        containerColor = AppColors.Background,
        bottomBar = {
            AppBottomBar(
                currentRoute = "budgets",
                onDashboard = onNavigateDashboard,
                onBudgets = {},
                onRecommend = onNavigateRecommend,
                onSettings = onNavigateSettings
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            Text(
                "Spending History",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))

            BudgetSpendingTabs(
                selectedTab,
                onTabSelected = { index -> selectedTab = index },
                onNavClick = {},
                modifier = Modifier
            )

            Text(
                "Swipe from right to left to delete",
                fontFamily = FontFamily.Serif,
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
            Text(
                "Press on a purchase to see more detail",
                fontFamily = FontFamily.Serif,
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Date", modifier = Modifier.weight(1f), fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.Center)
                Text("Merchant", modifier = Modifier.weight(1f), fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.Center)
                Text("Category", modifier = Modifier.weight(1f), fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.Center)
                Text("Amount", modifier = Modifier.weight(1f), fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.Center)
            }

            expenses.forEach { (docId, spending) ->
                SpendingRow(spending, onClick = {}, onDelete = { viewModel.deleteExpense(docId) })
                Spacer(Modifier.height(10.dp))
            }

            if (expenses.isEmpty()) {
                Spacer(Modifier.height(32.dp))
                Text("No expenses yet!", fontFamily = FontFamily.Serif, fontSize = 16.sp)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SpendingHistoryScreenPreview() {
    SpendingHistoryScreen()
}