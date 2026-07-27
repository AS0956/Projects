package com.example.final_ui_skeleton.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.final_ui_skeleton.viewmodel.SproutViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

private fun Double.toD() = "$${NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 0 }.format(this)}"
private fun Double.toD2() = "$${NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 2; minimumFractionDigits = 2 }.format(this)}"

/**
 * 1. What: Displays a month-by-month history of a user spending.
 * 2. Who: Called by the AppNavigation
 * 3. When: Executed when the user selects budget history.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetHistoryScreen(
    viewModel: SproutViewModel = viewModel(),
    onBack: () -> Unit = {},
    onNavigateDashboard: () -> Unit = {},
    onNavigateBudgets: () -> Unit = {},
    onNavigateRecommend: () -> Unit = {},
    onNavigateSettings: () -> Unit = {}
) {
    val allExpenses by viewModel.expenses.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val monthlyBudget = userProfile?.monthlyBudget ?: 1200.0

    LaunchedEffect(Unit) {
        viewModel.loadUserProfile()
        viewModel.startListeningToExpenses()
    }

    // build list of all months that have expenses
    val fmt = SimpleDateFormat("M/d/yyyy", Locale.getDefault())
    val monthFmt = SimpleDateFormat("MM/yyyy", Locale.getDefault())
    val displayFmt = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    val availableMonths = allExpenses
        .mapNotNull { (_, s) -> try { monthFmt.format(fmt.parse(s.date)!!) } catch (e: Exception) { null } }
        .distinct()
        .sortedDescending()

    var selectedMonthKey by remember(availableMonths) {
        mutableStateOf(availableMonths.firstOrNull() ?: monthFmt.format(Date()))
    }

    val selectedIndex = availableMonths.indexOf(selectedMonthKey)

    val monthExpenses = allExpenses.filter { (_, s) ->
        try { monthFmt.format(fmt.parse(s.date)!!) == selectedMonthKey } catch (e: Exception) { false }
    }

    val totalSpent = monthExpenses.sumOf { it.second.amount }
    val remaining = (monthlyBudget - totalSpent).coerceAtLeast(0.0)
    val budgetPct = if (monthlyBudget > 0) (totalSpent / monthlyBudget * 100).toInt() else 0

    val categoryTotals = monthExpenses.map { it.second }
        .groupBy { it.category }
        .mapValues { e -> e.value.sumOf { it.amount } }
        .entries.sortedByDescending { it.value }

    val displayMonth = try {
        displayFmt.format(monthFmt.parse(selectedMonthKey)!!)
    } catch (e: Exception) { selectedMonthKey }

    Scaffold(
        containerColor = AppColors.Background,
        topBar = {
            TopAppBar(
                title = {
                    Text("Budget History", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.Background)
            )
        },
        bottomBar = {
            AppBottomBar(
                currentRoute = "budgets",
                onDashboard = onNavigateDashboard,
                onBudgets = onNavigateBudgets,
                onRecommend = onNavigateRecommend,
                onSettings = onNavigateSettings
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(8.dp))

            // month selector
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFE8E6F0),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = {
                            if (selectedIndex < availableMonths.size - 1)
                                selectedMonthKey = availableMonths[selectedIndex + 1]
                        },
                        enabled = selectedIndex < availableMonths.size - 1
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Previous month",
                            tint = if (selectedIndex < availableMonths.size - 1) AppColors.Primary else Color.LightGray
                        )
                    }
                    Text(
                        displayMonth,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = AppColors.Primary,
                        textAlign = TextAlign.Center
                    )
                    IconButton(
                        onClick = {
                            if (selectedIndex > 0)
                                selectedMonthKey = availableMonths[selectedIndex - 1]
                        },
                        enabled = selectedIndex > 0
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Next month",
                            tint = if (selectedIndex > 0) AppColors.Primary else Color.LightGray
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            if (monthExpenses.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFE8E6F0),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "No expenses recorded for $displayMonth.",
                        fontSize = 15.sp, color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(32.dp)
                    )
                }
            } else {
                // budget summary card
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFE8E6F0),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "$displayMonth Summary",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text("Monthly budget: ${monthlyBudget.toD()}", fontSize = 13.sp, color = Color.Gray)
                        Spacer(Modifier.height(16.dp))

                        // category bars
                        categoryTotals.forEach { (category, spent) ->
                            val progress = (spent / monthlyBudget).toFloat().coerceIn(0f, 1f)
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "$category:",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        "${"%.0f".format(progress * 100)}%",
                                        fontSize = 13.sp,
                                        color = AppColors.Primary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier.fillMaxWidth().height(8.dp),
                                    color = AppColors.Primary,
                                    trackColor = Color(0xFFDDDDE6)
                                )
                                Spacer(Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(spent.toD2(), fontSize = 12.sp)
                                    Text(monthlyBudget.toD(), fontSize = 12.sp, color = Color.Gray)
                                }
                                Spacer(Modifier.height(12.dp))
                            }
                        }

                        HorizontalDivider(color = Color(0xFFCCCCCC))
                        Spacer(Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total spent:", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(totalSpent.toD2(), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFFCC0000))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Remaining:", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(remaining.toD2(), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF2E7D32))
                        }
                        if (remaining > 0) {
                            Spacer(Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = AppColors.Primary.copy(alpha = 0.1f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    "💰 ${remaining.toD()} surplus from $displayMonth went toward your savings goal.",
                                    fontSize = 12.sp,
                                    color = AppColors.Primary,
                                    modifier = Modifier.padding(10.dp),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // expense list for that month
                Text(
                    "Expenses",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp)) {
                    Text("Date", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1.8f))
                    Text("Merchant", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(2f))
                    Text("Category", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1.8f))
                    Text("Amount", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1.4f), textAlign = TextAlign.End)
                }
                HorizontalDivider(color = Color(0xFFCCCCCC))
                Spacer(Modifier.height(4.dp))

                monthExpenses.forEach { (_, spending) ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFE8E6F0),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(spending.date, fontSize = 11.sp, modifier = Modifier.weight(1.8f))
                            Text(spending.merchant.ifBlank { "—" }, fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(2f))
                            Text(spending.category.ifBlank { "—" }, fontSize = 11.sp, modifier = Modifier.weight(1.8f))
                            Text(spending.amount.toD2(), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AppColors.Primary, modifier = Modifier.weight(1.4f), textAlign = TextAlign.End)
                        }
                    }
                }
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewBudgetHistoryScreen() {
    BudgetHistoryScreen()
}