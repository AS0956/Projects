package com.example.final_ui_skeleton.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.final_ui_skeleton.model.Spending
import com.example.final_ui_skeleton.model.User
import com.example.final_ui_skeleton.ui.components.AppBackground
import com.example.final_ui_skeleton.ui.components.AppBottomBar
import com.example.final_ui_skeleton.ui.components.AppColors
import com.example.final_ui_skeleton.viewmodel.SproutViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

private fun Double.toDollars() = "$${NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 0 }.format(this)}"
private fun Double.toDollars2() = "$${NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 2; minimumFractionDigits = 2 }.format(this)}"

// filters expenses to only the current calendar month
private fun List<Pair<String, Spending>>.currentMonth(): List<Pair<String, Spending>> {
    val fmt = SimpleDateFormat("M/d/yyyy", Locale.getDefault())
    val monthFmt = SimpleDateFormat("MM/yyyy", Locale.getDefault())
    val thisMonth = monthFmt.format(java.util.Date())
    return filter { (_, s) ->
        try { monthFmt.format(fmt.parse(s.date)!!) == thisMonth } catch (e: Exception) { false }
    }
}

// generates dynamic insight bullets from real data
fun buildInsights(user: User?, expenses: List<Pair<String, Spending>>, savedAmount: Double): List<Pair<String, String>> {
    if (user == null || expenses.isEmpty()) return listOf(
        "💡" to "Add your first expense to get personalized insights!"
    )

    val insights = mutableListOf<Pair<String, String>>()
    val budget = user.monthlyBudget
    val totalSpent = expenses.sumOf { it.second.amount }
    val remaining = (budget - totalSpent).coerceAtLeast(0.0)
    val budgetPct = if (budget > 0) (totalSpent / budget * 100).toInt() else 0
    val savingsTarget = user.savingsTarget
    val goalName = user.monthlyBudgetName.ifBlank { user.purpose.ifBlank { "your goal" } }
    val savingsPct = if (savingsTarget > 0) (savedAmount / savingsTarget * 100).toInt() else 0

    // fixed costs are not actionable — exclude from insight targeting
    val fixedCategories = setOf("rent", "car payment", "car", "mortgage", "insurance", "loan", "utilities")
    fun isFixed(category: String) = fixedCategories.any { category.lowercase().contains(it) }

    val allCategoryTotals = expenses.map { it.second }
        .groupBy { it.category }
        .mapValues { e -> e.value.sumOf { it.amount } }
        .entries.sortedByDescending { it.value }

    // variable (actionable) categories only
    val variableCategoryTotals = allCategoryTotals.filter { !isFixed(it.key) }
    val topVariable = variableCategoryTotals.firstOrNull()

    val diningSpend = allCategoryTotals.filter { it.key.lowercase().contains("dining") || it.key.lowercase().contains("restaurant") }.sumOf { it.value }
    val grocerySpend = allCategoryTotals.filter { it.key.lowercase().contains("groceri") }.sumOf { it.value }
    val shoppingSpend = allCategoryTotals.filter { it.key.lowercase().contains("shop") }.sumOf { it.value }
    val fixedSpend = allCategoryTotals.filter { isFixed(it.key) }.sumOf { it.value }
    val variableSpend = totalSpent - fixedSpend

    // merchant analysis — exclude fixed-cost merchants (landlord, car dealer, etc.)
    val merchantTotals = expenses.map { it.second }
        .filter { it.merchant.isNotBlank() && !isFixed(it.category) }
        .groupBy { it.merchant }
        .mapValues { e -> e.value.sumOf { it.amount } }
        .entries.sortedByDescending { it.value }
    val topMerchant = merchantTotals.firstOrNull()
    val repeatMerchants = expenses.map { it.second }
        .filter { it.merchant.isNotBlank() && !isFixed(it.category) }
        .groupBy { it.merchant }
        .filter { it.value.size >= 2 }

    // budget status
    when {
        budgetPct >= 100 -> insights += "🚨" to "You're ${(totalSpent - budget).toDollars2()} over your ${budget.toDollars()} budget. Pause non-essential spending for the rest of the month."
        budgetPct >= 80 -> insights += "⚠️" to "You've used $budgetPct% of your budget — only ${remaining.toDollars()} left. Watch your discretionary spending this week."
        budgetPct >= 50 -> insights += "📊" to "Halfway through your budget with ${remaining.toDollars()} remaining. Your fixed costs are ${fixedSpend.toDollars2()} — focus on cutting the ${variableSpend.toDollars2()} in variable spending."
        else -> insights += "✅" to "Only $budgetPct% of your ${budget.toDollars()} budget used. ${remaining.toDollars()} still available — strong start!"
    }

    // top variable merchant (not fixed)
    if (topMerchant != null && budget > 0) {
        val pct = (topMerchant.value / budget * 100).toInt()
        val count = expenses.count { it.second.merchant == topMerchant.key }
        if (pct >= 8) {
            insights += "🏪" to "${topMerchant.key} is your biggest discretionary merchant — ${topMerchant.value.toDollars2()} across $count visit${if (count != 1) "s" else ""} ($pct% of budget). Worth reviewing frequency."
        }
    }

    // repeat merchant
    if (repeatMerchants.isNotEmpty() && insights.size < 3) {
        val m = repeatMerchants.entries.first()
        val total = m.value.sumOf { it.amount }
        insights += "🔁" to "You've been to ${m.key} ${m.value.size} times — ${total.toDollars2()} total. These small repeat visits are adding up."
    }

    // top actionable category
    if (topVariable != null && insights.size < 4) {
        val pct = if (budget > 0) (topVariable.value / budget * 100).toInt() else 0
        val count = expenses.count { it.second.category == topVariable.key }
        if (pct > 20) {
            insights += "🏆" to "${topVariable.key} is your biggest variable expense: ${topVariable.value.toDollars2()} across $count transaction${if (count != 1) "s" else ""} ($pct% of budget). A 20% cut here saves ${(topVariable.value * 0.2).toDollars2()} this month."
        } else if (pct > 0) {
            insights += "📈" to "Your top discretionary category is ${topVariable.key} at ${topVariable.value.toDollars2()} ($pct% of budget). Looking reasonable!"
        }
    }

    // dining vs groceries
    if (diningSpend > 0 && grocerySpend > 0 && diningSpend > grocerySpend && insights.size < 4) {
        insights += "🍽️" to "Dining out (${diningSpend.toDollars2()}) is outpacing groceries (${grocerySpend.toDollars2()}). Swapping just one dining trip per week for home cooking could save ${(diningSpend * 0.25).toDollars2()} this month."
    } else if (diningSpend > 0 && diningSpend > budget * 0.15 && insights.size < 4) {
        insights += "🍽️" to "Dining out is ${((diningSpend / budget) * 100).toInt()}% of your budget at ${diningSpend.toDollars2()}. Even one less restaurant trip a week adds up."
    }

    // savings goal
    if (savingsTarget > 0 && insights.size < 4) {
        when {
            savingsPct >= 100 -> insights += "🎉" to "You've hit your $goalName goal of ${savingsTarget.toDollars()}! Time to set a new one."
            savingsPct >= 75 -> insights += "💪" to "$savingsPct% toward $goalName — ${savedAmount.toDollars()} of ${savingsTarget.toDollars()} saved. Almost there!"
            remaining > 0 -> insights += "🌱" to "Your unspent ${remaining.toDollars()} this month goes straight to $goalName. You're $savingsPct% of the way there."
        }
    }

    // shopping
    if (shoppingSpend > 0 && shoppingSpend > budget * 0.2 && insights.size < 4) {
        insights += "🛍️" to "Shopping is ${((shoppingSpend / budget) * 100).toInt()}% of your budget (${shoppingSpend.toDollars2()}). Try the 24-hour rule before any non-essential purchase."
    }

    return insights.take(4)
}

/**
 * 1. What: The home dashboard with a budget overview and spending snapshot cards, a savings goal progress card, and a dynamic insights card.
 * 2. Who: Called by the AppNavigator
 * 3. When: Executed after sign in or after sing up.
 */
@Composable
fun DashboardScreen(
    viewModel: SproutViewModel = viewModel(),
    onNavigateBudgets: () -> Unit = {},
    onNavigateRecommend: () -> Unit = {},
    onNavigateSettings: () -> Unit = {},
    onNavigateBudgetHistory: () -> Unit = {}
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val allExpenses by viewModel.expenses.collectAsState()
    val expenses = allExpenses.currentMonth() // only show this month's data
    val savedAmount by viewModel.savedAmount.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUserProfile()
        viewModel.startListeningToExpenses()
    }

    val firstName = userProfile?.name?.split(" ")?.firstOrNull() ?: "..."
    val initials = userProfile?.name
        ?.split(" ")?.filter { it.isNotEmpty() }?.take(2)
        ?.joinToString("") { it.first().uppercaseChar().toString() } ?: "?"

    val monthlyBudget = userProfile?.monthlyBudget ?: 1200.0
    val totalSpent = expenses.sumOf { it.second.amount }
    val remaining = (monthlyBudget - totalSpent).coerceAtLeast(0.0)
    val budgetProgress = if (monthlyBudget > 0) (totalSpent / monthlyBudget).toFloat().coerceIn(0f, 1f) else 0f

    val savingsTarget = userProfile?.savingsTarget ?: 0.0
    val goalName = userProfile?.monthlyBudgetName?.ifBlank { null }
        ?: userProfile?.purpose?.ifBlank { null } ?: "Your Goal"
    val savingsProgress = if (savingsTarget > 0) (savedAmount / savingsTarget).toFloat().coerceIn(0f, 1f) else 0f

    val categoryTotals = expenses.map { it.second }
        .groupBy { it.category }
        .mapValues { e -> e.value.sumOf { it.amount } }
        .entries.sortedByDescending { it.value }.take(5)

    // dynamic insights based on real data
    val insights = remember(userProfile, expenses, savedAmount) {
        buildInsights(userProfile, expenses, savedAmount)
    }

    AppBackground {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                AppBottomBar(
                    currentRoute = "dashboard",
                    onDashboard = {},
                    onBudgets = onNavigateBudgets,
                    onRecommend = onNavigateRecommend,
                    onSettings = onNavigateSettings
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(Modifier.height(16.dp))

                // avatar + greeting
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(64.dp).background(Color(0xFFB0B0C0), CircleShape), contentAlignment = Alignment.Center) {
                        Text(initials, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.White)
                    }
                    Spacer(Modifier.width(12.dp))
                    Text("Welcome $firstName!", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 28.sp)
                }

                Spacer(Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // budget overview
                    Surface(modifier = Modifier.weight(1f).clickable { onNavigateBudgetHistory() }, shape = RoundedCornerShape(20.dp), color = Color(0xFFE8E6F0)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Budget Overview", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                            }
                            Spacer(Modifier.height(8.dp))
                            Text("Monthly Budget:", fontSize = 12.sp)
                            Text(monthlyBudget.toDollars(), fontWeight = FontWeight.Bold, fontSize = 22.sp)
                            Spacer(Modifier.height(8.dp))
                            LinearProgressIndicator(progress = { budgetProgress }, modifier = Modifier.fillMaxWidth().height(6.dp), color = Color(0xFFCC0000), trackColor = Color(0xFFDDDDE6))
                            Spacer(Modifier.height(6.dp))
                            Text("${totalSpent.toDollars()} spent", color = Color(0xFFCC0000), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text("${remaining.toDollars()} unspent", color = Color(0xFF2E7D32), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    // spending snapshot
                    Surface(modifier = Modifier.weight(1f).clickable { onNavigateBudgets() }, shape = RoundedCornerShape(20.dp), color = Color(0xFFE8E6F0)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Spending Snapshot", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                            }
                            Spacer(Modifier.height(8.dp))
                            if (categoryTotals.isEmpty()) {
                                Text("No expenses yet", fontSize = 12.sp, color = Color.Gray)
                            } else {
                                categoryTotals.forEach { (cat, amt) ->
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("$cat:", fontSize = 12.sp, modifier = Modifier.weight(1f))
                                        Text(amt.toDollars(), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    Spacer(Modifier.height(4.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // savings goal card
                Surface(modifier = Modifier.fillMaxWidth().clickable { onNavigateBudgets() }, shape = RoundedCornerShape(20.dp), color = Color(0xFFE8E6F0)) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(goalName, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(22.dp))
                        }
                        Spacer(Modifier.height(4.dp))
                        Text("Budget surplus saved toward goal", fontSize = 11.sp, color = Color.Gray)
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(progress = { savingsProgress }, modifier = Modifier.fillMaxWidth().height(8.dp), color = AppColors.Primary, trackColor = Color(0xFFDDDDE6))
                        Spacer(Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${savedAmount.toDollars()} saved", fontSize = 16.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.SemiBold)
                            Text("Goal: ${savingsTarget.toDollars()}", fontSize = 14.sp, color = Color.Gray)
                        }
                        if (remaining > 0) {
                            Spacer(Modifier.height(6.dp))
                            Text("This month: ${remaining.toDollars()} unspent → goes to savings", fontSize = 11.sp, color = AppColors.Primary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // dynamic insights card
                Surface(
                    modifier = Modifier.fillMaxWidth().clickable { onNavigateRecommend() },
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFE8E6F0)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Insights", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(22.dp))
                        }
                        Spacer(Modifier.height(12.dp))

                        insights.forEachIndexed { index, (emoji, text) ->
                            Row(verticalAlignment = Alignment.Top) {
                                Text(emoji, fontSize = 16.sp, modifier = Modifier.width(28.dp))
                                Text(text, fontSize = 13.sp, lineHeight = 19.sp, modifier = Modifier.weight(1f))
                            }
                            if (index < insights.lastIndex) Spacer(Modifier.height(10.dp))
                        }

                        Spacer(Modifier.height(12.dp))
                        Text(
                            "View full AI recommendations →",
                            color = AppColors.Primary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            modifier = Modifier.clickable { onNavigateRecommend() }
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    DashboardScreen()
}