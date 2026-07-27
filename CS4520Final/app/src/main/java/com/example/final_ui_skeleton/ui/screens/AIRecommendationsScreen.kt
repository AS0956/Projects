package com.example.final_ui_skeleton.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.final_ui_skeleton.model.Spending
import com.example.final_ui_skeleton.model.User
import com.example.final_ui_skeleton.ui.components.AppBottomBar
import com.example.final_ui_skeleton.ui.components.AppColors
import com.example.final_ui_skeleton.viewmodel.ChatMessage
import com.example.final_ui_skeleton.viewmodel.SproutViewModel
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.Icon

private fun Double.fmt() = "$${NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 0 }.format(this)}"
private fun Double.fmt2() = "$${NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 2; minimumFractionDigits = 2 }.format(this)}"

private fun List<Pair<String, Spending>>.currentMonth(): List<Pair<String, Spending>> {
    val fmt = java.text.SimpleDateFormat("M/d/yyyy", java.util.Locale.getDefault())
    val monthFmt = java.text.SimpleDateFormat("MM/yyyy", java.util.Locale.getDefault())
    val thisMonth = monthFmt.format(java.util.Date())
    return filter { (_, s) ->
        try { monthFmt.format(fmt.parse(s.date)!!) == thisMonth } catch (e: Exception) { false }
    }
}

fun generateInsights(user: User?, expenses: List<Pair<String, Spending>>, savedAmount: Double): String {
    if (user == null) return "Load your profile and I'll give you personalized insights!"

    val totalSpent = expenses.sumOf { it.second.amount }
    val budget = user.monthlyBudget
    val remaining = (budget - totalSpent).coerceAtLeast(0.0)
    val savingsTarget = user.savingsTarget
    val goalName = user.monthlyBudgetName.ifBlank { user.purpose.ifBlank { "your goal" } }
    val city = user.city.ifBlank { null }
    val income = user.incomeBracket.ifBlank { null }
    val occupation = user.occupation.ifBlank { null }
    val savingsProgress = if (savingsTarget > 0) (savedAmount / savingsTarget * 100).toInt() else 0
    val budgetUsedPct = if (budget > 0) (totalSpent / budget * 100).toInt() else 0

    val fixedCategories = setOf("rent", "car payment", "car", "mortgage", "insurance", "loan", "utilities")
    fun isFixed(cat: String) = fixedCategories.any { cat.lowercase().contains(it) }

    val allCategoryTotals = expenses.map { it.second }
        .groupBy { it.category }
        .mapValues { e -> e.value.sumOf { it.amount } }
        .entries.sortedByDescending { it.value }

    val variableCategoryTotals = allCategoryTotals.filter { !isFixed(it.key) }
    val topVariable = variableCategoryTotals.firstOrNull()
    val fixedSpend = allCategoryTotals.filter { isFixed(it.key) }.sumOf { it.value }
    val variableSpend = totalSpent - fixedSpend

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

    val diningSpend = allCategoryTotals.filter { it.key.lowercase().contains("dining") || it.key.lowercase().contains("restaurant") }.sumOf { it.value }
    val grocerySpend = allCategoryTotals.filter { it.key.lowercase().contains("groceri") }.sumOf { it.value }

    val lines = mutableListOf<String>()
    val context = listOfNotNull(occupation, city).joinToString(" in ")
    lines += "Here's your Sprout summary${if (context.isNotBlank()) " ($context)" else ""}:"
    lines += ""

    lines += when {
        expenses.isEmpty() -> "💡 No expenses logged yet — add some and I'll analyse your patterns!"
        budgetUsedPct >= 100 -> "🚨 Over budget by ${(totalSpent - budget).fmt2()}! You've spent ${totalSpent.fmt2()} of your ${budget.fmt()} budget. Pause non-essentials now."
        budgetUsedPct >= 80 -> "⚠️ $budgetUsedPct% of your budget used — only ${remaining.fmt()} left. Your fixed costs are ${fixedSpend.fmt2()}, leaving ${variableSpend.fmt2()} in variable spend to watch."
        budgetUsedPct >= 50 -> "📊 Halfway through: ${totalSpent.fmt2()} of ${budget.fmt()} spent. Fixed costs: ${fixedSpend.fmt2()} • Variable: ${variableSpend.fmt2()}. Focus cuts on the variable side."
        else -> "✅ Only $budgetUsedPct% used — ${remaining.fmt()} still available. Fixed: ${fixedSpend.fmt2()} • Variable so far: ${variableSpend.fmt2()}."
    }

    if (topMerchant != null) {
        val pct = if (budget > 0) (topMerchant.value / budget * 100).toInt() else 0
        val count = expenses.count { it.second.merchant == topMerchant.key }
        lines += ""
        lines += "🏪 Top discretionary merchant: ${topMerchant.key} — ${topMerchant.value.fmt2()} across $count visit${if (count != 1) "s" else ""} ($pct% of budget)."
        if (pct >= 12) lines += "   That's significant. Could you cut even one visit? That's ~${(topMerchant.value / count).fmt2()} back in your pocket."
    }

    if (repeatMerchants.isNotEmpty()) {
        val m = repeatMerchants.entries.first()
        val total = m.value.sumOf { it.amount }
        lines += ""
        lines += "🔁 ${m.key} visited ${m.value.size} times — ${total.fmt2()} total. Recurring visits like this are easy to underestimate."
    }

    if (topVariable != null) {
        val pct = if (budget > 0) (topVariable.value / budget * 100).toInt() else 0
        val count = expenses.count { it.second.category == topVariable.key }
        lines += ""
        lines += "🏆 Biggest variable category: ${topVariable.key} — ${topVariable.value.fmt2()} across $count transaction${if (count != 1) "s" else ""} ($pct% of budget)."
        if (pct > 20) lines += "   A 20% cut here saves ${(topVariable.value * 0.2).fmt2()} this month."
    }

    if (diningSpend > 0 && grocerySpend > 0 && diningSpend > grocerySpend) {
        lines += ""
        lines += "🍽️ Dining out (${diningSpend.fmt2()}) is outpacing groceries (${grocerySpend.fmt2()}). One fewer restaurant trip per week could recover ~${(diningSpend * 0.25).fmt2()} this month."
    } else if (diningSpend > 0 && budget > 0 && diningSpend / budget > 0.15) {
        lines += ""
        lines += "🍽️ Dining out is ${((diningSpend / budget) * 100).toInt()}% of your budget at ${diningSpend.fmt2()}. Even swapping one meal out per week adds up over the month."
    }

    if (savingsTarget > 0) {
        lines += ""
        lines += when {
            savingsProgress >= 100 -> "🎉 You've hit your $goalName goal of ${savingsTarget.fmt()}! Time to set a new one."
            savingsProgress >= 75 -> "💪 $savingsProgress% toward $goalName — ${savedAmount.fmt()} of ${savingsTarget.fmt()} saved. Almost there!"
            savingsProgress >= 50 -> "🌱 Halfway to $goalName — ${savedAmount.fmt()} of ${savingsTarget.fmt()} saved. Keep the momentum."
            savingsProgress > 0 -> "🌱 ${savedAmount.fmt()} toward $goalName (${savingsTarget.fmt()} target). Every month of surplus gets you closer."
            else -> "🎯 Your unspent budget goes straight to $goalName. The less you spend, the faster you get there."
        }
        if (remaining > 0 && savingsProgress < 100) {
            lines += "   This month's unspent ${remaining.fmt()} is already going toward your goal! 🙌"
        }
    }

    if (income != null) {
        lines += ""
        lines += when {
            income.contains("Under") -> "💡 On a tighter budget, the 50/30/20 rule helps: 50% needs, 30% wants, 20% savings."
            income.contains("50,000") || income.contains("75,000") -> "💡 At your income level, automating a fixed savings transfer each payday is one of the highest-leverage habits you can build."
            income.contains("100,000") || income.contains("150,000") -> "💡 Consider maxing tax-advantaged accounts before discretionary spending — the compounding benefit is significant."
            income.contains("Over") -> "💡 At higher incomes, diversified savings vehicles and investment accounts compound significantly over time."
            else -> "💡 Consistency beats intensity — a small fixed monthly saving compounds more than irregular large ones."
        }
    }

    if (city != null) {
        lines += "🏙️ In $city — look for free local events and community deals to cut leisure spending without sacrificing fun."
    }

    val sharedExpenses = expenses.filter { it.second.perOrShared == "Shared Budget" }
    if (sharedExpenses.isNotEmpty()) {
        val sharedTotal = sharedExpenses.sumOf { it.second.amount }
        lines += ""
        lines += "🤝 ${sharedTotal.fmt2()} in shared expenses this month — make sure your shared goals reflect these contributions!"
    }

    return lines.joinToString("\n")
}


fun generateResponse(input: String, user: User?, expenses: List<Pair<String, Spending>>, savedAmount: Double): String {
    val lower = input.lowercase()
    val budget = user?.monthlyBudget ?: 0.0
    val totalSpent = expenses.sumOf { it.second.amount }
    val remaining = (budget - totalSpent).coerceAtLeast(0.0)
    val savingsTarget = user?.savingsTarget ?: 0.0
    val goalName = user?.monthlyBudgetName?.ifBlank { null } ?: user?.purpose?.ifBlank { null } ?: "your goal"
    val city = user?.city?.ifBlank { null }
    val income = user?.incomeBracket?.ifBlank { null }
    val occupation = user?.occupation?.ifBlank { null }

    val fixedCategories = setOf("rent", "car payment", "car", "mortgage", "insurance", "loan", "utilities")
    fun isFixed(cat: String) = fixedCategories.any { cat.lowercase().contains(it) }

    val allCategoryTotals = expenses.map { it.second }
        .groupBy { it.category }
        .mapValues { e -> e.value.sumOf { it.amount } }
        .entries.sortedByDescending { it.value }

    val variableCategoryTotals = allCategoryTotals.filter { !isFixed(it.key) }
    val topVariable = variableCategoryTotals.firstOrNull()
    val topCategory = allCategoryTotals.firstOrNull()
    val fixedSpend = allCategoryTotals.filter { isFixed(it.key) }.sumOf { it.value }
    val variableSpend = totalSpent - fixedSpend

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

    // merchant queries
    if (lower.contains("merchant") || lower.contains("store") || lower.contains("shop at") || lower.contains("where do i")) {
        if (merchantTotals.isEmpty()) return "No merchant info yet — add merchants when logging expenses and I'll track your patterns!"
        val top3 = merchantTotals.take(3).joinToString("\n") { "• ${it.key}: ${it.value.fmt2()}" }
        val repeatStr = if (repeatMerchants.isNotEmpty())
            "\n\n🔁 Repeat visits: " + repeatMerchants.entries.joinToString(", ") { "${it.key} (${it.value.size}x)" }
        else ""
        return "Your top discretionary merchants this month:\n$top3$repeatStr\n\n(Fixed-cost merchants like landlords excluded)"
    }

    // savings
    if (lower.contains("sav") || lower.contains("goal") || lower.contains("progress")) {
        if (savingsTarget <= 0) return "You haven't set a savings target yet. Go to Settings to update it!"
        val pct = (savedAmount / savingsTarget * 100).toInt()
        val stillNeeded = (savingsTarget - savedAmount).coerceAtLeast(0.0)
        val monthsLeft = if (remaining > 0) Math.ceil(stillNeeded / remaining).toInt() else -1
        return "You've saved ${savedAmount.fmt()} of your ${savingsTarget.fmt()} $goalName goal ($pct%).\n" +
                "Still need: ${stillNeeded.fmt()}\n" +
                if (remaining > 0 && monthsLeft > 0) "At your current monthly surplus of ${remaining.fmt()}, you'd hit your goal in about $monthsLeft more month${if (monthsLeft != 1) "s" else ""}. 🎯"
                else if (remaining > 0) "Your unspent ${remaining.fmt()} this month is already contributing!"
                else "Try reducing spending to build a surplus toward your goal."
    }

    // budget
    if (lower.contains("budget") || lower.contains("spent") || lower.contains("left") || lower.contains("remaining") || lower.contains("how much")) {
        if (budget <= 0) return "You haven't set a monthly budget yet. Tap the + on the Budgets screen!"
        val pct = if (budget > 0) (totalSpent / budget * 100).toInt() else 0
        return "Budget breakdown:\n" +
                "• Total spent: ${totalSpent.fmt2()} ($pct% of ${budget.fmt()})\n" +
                "• Fixed costs: ${fixedSpend.fmt2()} (rent, car, etc.)\n" +
                "• Variable spend: ${variableSpend.fmt2()} ← this is where cuts come from\n" +
                "• Remaining: ${remaining.fmt()}\n\n" +
                if (pct > 80) "⚠️ Running low — focus cuts on your variable spending." else "You're on track! 👍"
    }

    // categories
    if (lower.contains("categor") || lower.contains("most") || lower.contains("biggest") || lower.contains("top") || lower.contains("where")) {
        if (allCategoryTotals.isEmpty()) return "No expenses logged yet — add some and I'll break down your spending!"
        val fixed = allCategoryTotals.filter { isFixed(it.key) }.take(3).joinToString("\n") { "• ${it.key}: ${it.value.fmt2()} (fixed)" }
        val variable = variableCategoryTotals.take(3).joinToString("\n") { "• ${it.key}: ${it.value.fmt2()}" }
        return "Your spending by category:\n\n🔒 Fixed costs (can't easily cut):\n${fixed.ifBlank { "None logged" }}\n\n✂️ Variable (where you can cut):\n${variable.ifBlank { "None logged" }}" +
                if (topVariable != null && budget > 0 && topVariable.value / budget > 0.2)
                    "\n\n${topVariable.key} is your biggest variable expense — a 20% reduction saves ${(topVariable.value * 0.2).fmt2()} this month." else ""
    }

    // dining/food
    if (lower.contains("food") || lower.contains("dining") || lower.contains("restaurant") || lower.contains("eat") || lower.contains("groceri")) {
        val diningSpend = allCategoryTotals.filter { it.key.lowercase().contains("dining") }.sumOf { it.value }
        val grocerySpend = allCategoryTotals.filter { it.key.lowercase().contains("groceri") }.sumOf { it.value }
        val diningCount = expenses.count { it.second.category.lowercase().contains("dining") }
        val groceryCount = expenses.count { it.second.category.lowercase().contains("groceri") }
        val diningMerchants = expenses.filter { it.second.category.lowercase().contains("dining") }.mapNotNull { it.second.merchant.ifBlank { null } }.distinct()
        val groceryMerchants = expenses.filter { it.second.category.lowercase().contains("groceri") }.mapNotNull { it.second.merchant.ifBlank { null } }.distinct()

        val lines = mutableListOf<String>()
        if (diningSpend > 0) {
            val detail = if (diningMerchants.isNotEmpty()) " (${diningMerchants.joinToString(", ")})" else ""
            lines += "🍽️ Dining Out: ${diningSpend.fmt2()} across $diningCount transaction${if (diningCount != 1) "s" else ""}$detail"
        }
        if (grocerySpend > 0) {
            val detail = if (groceryMerchants.isNotEmpty()) " (${groceryMerchants.joinToString(", ")})" else ""
            lines += "🛒 Groceries: ${grocerySpend.fmt2()} across $groceryCount transaction${if (groceryCount != 1) "s" else ""}$detail"
        }
        if (diningSpend > 0 && grocerySpend > 0) {
            lines += if (diningSpend > grocerySpend)
                "\nDining is outpacing groceries by ${(diningSpend - grocerySpend).fmt2()}. One fewer restaurant trip per week ≈ ${(diningSpend * 0.25).fmt2()} back this month."
            else "Good balance — groceries are ahead of dining. 🍳 Keep it up!"
        } else if (diningSpend == 0.0 && grocerySpend == 0.0) {
            return "No dining or grocery expenses logged yet. Add some and I'll break it down!"
        }
        return lines.joinToString("\n")
    }

    // transport
    if (lower.contains("transport") || lower.contains("car") || lower.contains("uber") || lower.contains("gas") || lower.contains("commut")) {
        val transportSpend = allCategoryTotals.filter { it.key.lowercase().contains("transport") }.sumOf { it.value }
        val carSpend = allCategoryTotals.filter { it.key.lowercase().contains("car") }.sumOf { it.value }
        return when {
            transportSpend > 0 -> "Transport: ${transportSpend.fmt2()}. ${if (city != null) "In $city, " else ""}public transit or carpooling can meaningfully cut this."
            carSpend > 0 -> "Car expenses: ${carSpend.fmt2()}. Note this is likely a fixed cost — focus variable cuts elsewhere."
            else -> "No transport expenses logged yet."
        }
    }

    // shopping
    if (lower.contains("shop") || lower.contains("cloth") || lower.contains("buy") || lower.contains("purchas")) {
        val shopSpend = allCategoryTotals.filter { it.key.lowercase().contains("shop") }.sumOf { it.value }
        val shopCount = expenses.count { it.second.category.lowercase().contains("shop") }
        return if (shopSpend > 0) {
            val avg = shopSpend / shopCount
            "Shopping: ${shopSpend.fmt2()} across $shopCount purchase${if (shopCount != 1) "s" else ""} (avg ${avg.fmt2()} each).\n\nTry the 24-hour rule — wait a day before non-essential purchases. Even cutting one purchase saves ${avg.fmt2()}."
        } else "No shopping expenses yet. The 24-hour rule is a great habit to avoid impulse purchases!"
    }

    // tips / advice
    if (lower.contains("tip") || lower.contains("advice") || lower.contains("suggest") || lower.contains("help") || lower.contains("improv")) {
        val tips = mutableListOf<String>()
        if (topVariable != null && budget > 0 && topVariable.value / budget > 0.2)
            tips += "• ${topVariable.key} is your biggest variable expense at ${topVariable.value.fmt2()} — a 20% cut saves ${(topVariable.value * 0.2).fmt2()}."
        if (topMerchant != null && budget > 0 && topMerchant.value / budget > 0.1) {
            val count = expenses.count { it.second.merchant == topMerchant.key }
            tips += "• You've visited ${topMerchant.key} $count times for ${topMerchant.value.fmt2()} — is every visit necessary?"
        }
        if (repeatMerchants.isNotEmpty()) {
            val m = repeatMerchants.entries.first()
            tips += "• ${m.key} visited ${m.value.size}x — these small repeat visits total ${m.value.sumOf { it.amount }.fmt2()}."
        }
        if (savingsTarget > 0 && savedAmount < savingsTarget)
            tips += "• Automate a transfer to $goalName each payday — even ${remaining.fmt()} consistently compounds fast."
        if (city != null) tips += "• Check free events in $city — leisure spending is the easiest to cut without feeling it."
        if (income != null && (income.contains("50,000") || income.contains("75,000")))
            tips += "• At your income, automating savings before you spend is the single highest-leverage habit."
        if (occupation != null) tips += "• As a $occupation, log work-related expenses — some may be tax-deductible."
        tips += "• Audit subscriptions monthly — most people have at least one they forgot about."
        tips += "• Awareness alone reduces spending 10-15%. You're already ahead by tracking this."
        return if (tips.isNotEmpty()) "Here are your personalized tips:\n\n${tips.joinToString("\n")}" else "Keep tracking consistently — that's the foundation of every good financial habit!"
    }

    // occupation-specific
    if (occupation != null && (lower.contains("work") || lower.contains("job") || lower.contains("career") || lower.contains("profession"))) {
        return "As a $occupation, log any work-related expenses (equipment, software, professional development) — these can sometimes be tax-deductible. Also check for any employee benefits or discounts you're not using!"
    }

    // how am i doing
    if (lower.contains("doing") || lower.contains("track") || lower.contains("status") || lower.contains("overall")) {
        val budgetPct = if (budget > 0) (totalSpent / budget * 100).toInt() else 0
        val budgetOk = budget > 0 && budgetPct < 80
        val savingsOk = savingsTarget > 0 && savedAmount > 0
        return when {
            budgetOk && savingsOk -> "Doing well! $budgetPct% of budget used ✅ and making progress toward $goalName ✅.\n\nFixed costs: ${fixedSpend.fmt2()} • Variable: ${variableSpend.fmt2()} • Remaining: ${remaining.fmt()}."
            budgetOk -> "Budget healthy at $budgetPct% used, but no progress toward $goalName yet. Your ${remaining.fmt()} surplus this month could be a start!"
            savingsOk -> "Saving toward $goalName ✅, but budget is at $budgetPct% — watch the variable spending (${variableSpend.fmt2()})."
            else -> "Set a savings goal and keep logging — then I can give you a full breakdown!"
        }
    }

    // shared expenses
    if (lower.contains("shared") || lower.contains("group") || lower.contains("together") || lower.contains("split")) {
        val sharedTotal = expenses.filter { it.second.perOrShared == "Shared Budget" }.sumOf { it.second.amount }
        return if (sharedTotal > 0) "You have ${sharedTotal.fmt2()} in shared expenses. Make sure these are tracked in your shared goals so everyone sees the full picture!"
        else "No shared expenses yet. Use the shared goals feature to split costs with friends or family!"
    }

    // greeting
    if (lower.contains("hi") || lower.contains("hello") || lower.contains("hey") || lower.contains("what can")) {
        return "Hey! I'm Sprout 🌱 — your personal finance assistant. Ask me about:\n• Budget breakdown (fixed vs variable)\n• Top spending categories\n• Merchant patterns\n• Savings progress toward $goalName\n• Personalized tips\n\nWhat would you like to know?"
    }

    // fallback
    val suggestions = mutableListOf<String>()
    if (budget > 0) suggestions += "your budget breakdown"
    if (variableCategoryTotals.isNotEmpty()) suggestions += "your variable spending"
    if (savingsTarget > 0) suggestions += "savings progress toward $goalName"
    suggestions += "personalized tips"
    return "I can help with: ${suggestions.joinToString(", ")}. What would you like to know?"
}

fun buildSuggestions(user: User?, expenses: List<Pair<String, Spending>>, savedAmount: Double): List<String> {
    val suggestions = mutableListOf<String>()
    val budget = user?.monthlyBudget ?: 0.0
    val goalName = user?.monthlyBudgetName?.ifBlank { null } ?: user?.purpose?.ifBlank { null } ?: "my goal"
    val categoryTotals = expenses.map { it.second }.groupBy { it.category }
        .mapValues { e -> e.value.sumOf { it.amount } }

    if (budget > 0) suggestions += "How's my budget?"
    if (categoryTotals.isNotEmpty()) suggestions += "Where am I spending most?"
    if (expenses.any { it.second.merchant.isNotBlank() }) suggestions += "My top merchants"
    if (user?.savingsTarget ?: 0.0 > 0) suggestions += "Progress toward $goalName"
    if (categoryTotals.keys.any { it.lowercase().contains("dining") }) suggestions += "Dining vs groceries"
    if (user?.occupation?.isNotBlank() == true) suggestions += "Work expense tips"
    if (expenses.any { it.second.perOrShared == "Shared Budget" }) suggestions += "My shared expenses"
    suggestions += "Give me tips"
    suggestions += "How am I doing overall?"

    return suggestions.take(6)
}

/**
 * 1. What: Displays an AI-style chat that generates personalized spending and savings insights.
 * 2. Who: Called by AppNavigation
 * 3. When: Executed when the user selects the recommend tab from the bottom navigation bar.
 */
@Composable
fun AIRecommendationsScreen(
    viewModel: SproutViewModel = viewModel(),
    onNavigateDashboard: () -> Unit = {},
    onNavigateBudgets: () -> Unit = {},
    onNavigateSettings: () -> Unit = {}
) {
    val allExpenses by viewModel.expenses.collectAsState()
    val expenses = allExpenses.currentMonth()
    val userProfile by viewModel.userProfile.collectAsState()
    val savedAmount by viewModel.savedAmount.collectAsState()
    val messages by viewModel.chatMessages.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUserProfile()
        viewModel.startListeningToExpenses()
    }

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val suggestions = remember(userProfile, expenses) { buildSuggestions(userProfile, expenses, savedAmount) }

    // generate opening summary only once per app session
    LaunchedEffect(userProfile) {
        if (userProfile != null) {
            val summary = generateInsights(userProfile, expenses, savedAmount)
            viewModel.generateSummaryIfNeeded(ChatMessage(summary, isUser = false))
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        viewModel.addChatMessage(ChatMessage(text, isUser = true))
        val reply = generateResponse(text, userProfile, expenses, savedAmount)
        viewModel.addChatMessage(ChatMessage(reply, isUser = false))
    }

    Scaffold(
        containerColor = AppColors.Background,
        bottomBar = {
            AppBottomBar(
                currentRoute = "recommend",
                onDashboard = onNavigateDashboard,
                onBudgets = onNavigateBudgets,
                onRecommend = {},
                onSettings = onNavigateSettings
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Text("AI Chat", fontSize = 28.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp, bottom = 4.dp))

            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { msg ->
                    if (msg.isUser) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                            Surface(color = AppColors.Primary, shape = RoundedCornerShape(16.dp, 4.dp, 16.dp, 16.dp), modifier = Modifier.widthIn(max = 280.dp)) {
                                Text(msg.text, color = Color.White, fontSize = 14.sp, modifier = Modifier.padding(14.dp))
                            }
                        }
                    } else {
                        Row(verticalAlignment = Alignment.Top) {
                            Box(modifier = Modifier.size(40.dp).background(AppColors.Primary, CircleShape), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Eco, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                            }
                            Spacer(Modifier.width(8.dp))
                            Surface(color = Color(0xFFDAD7CF), shape = RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp), modifier = Modifier.widthIn(max = 280.dp)) {
                                Text(msg.text, color = Color.Black, fontSize = 14.sp, lineHeight = 20.sp, modifier = Modifier.padding(14.dp))
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(suggestions) { suggestion ->
                    SuggestionChip(
                        onClick = { sendMessage(suggestion) },
                        label = { Text(suggestion, fontSize = 12.sp) }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Ask Sprout anything...", color = Color.Gray) },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.weight(1f),
                    maxLines = 3
                )
                Spacer(Modifier.width(8.dp))
                FloatingActionButton(
                    onClick = { sendMessage(inputText.trim()); inputText = "" },
                    containerColor = AppColors.Primary,
                    contentColor = Color.White,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", modifier = Modifier.size(20.dp))
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AIRecommendationsScreenPreview() {
    AIRecommendationsScreen()
}