package com.example.final_ui_skeleton.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.final_ui_skeleton.data.SharedGoalsRepository
import com.example.final_ui_skeleton.model.SavingsGoal
import com.example.final_ui_skeleton.model.Spending
import com.example.final_ui_skeleton.ui.components.AppBottomBar
import com.example.final_ui_skeleton.ui.components.AppColors
import com.example.final_ui_skeleton.ui.components.BudgetSpendingTabs
import com.example.final_ui_skeleton.viewmodel.SproutViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.Locale
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

private fun Double.toDollars() = "$${NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 0; minimumFractionDigits = 0 }.format(this)}"
private fun Double.toDollars2() = "$${NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 2; minimumFractionDigits = 2 }.format(this)}"

private fun List<Pair<String, Spending>>.currentMonth(): List<Pair<String, Spending>> {
    val fmt = SimpleDateFormat("M/d/yyyy", Locale.getDefault())
    val monthFmt = SimpleDateFormat("MM/yyyy", Locale.getDefault())
    val thisMonth = monthFmt.format(Date())
    return filter { (_, s) ->
        try { monthFmt.format(fmt.parse(s.date)!!) == thisMonth } catch (e: Exception) { false }
    }
}

/**
 * 1. What: Shows the user's current month budget snapshot, that also shows personal goal and shared goals, it also has Activity toggle to see individual expense.
 * 2. Who: Called by the AppNavigation.
 * 3. When: Executed when the user selects budgets on the bottom bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetSnapshotScreen(
    viewModel: SproutViewModel = viewModel(),
    onNavigateDashboard: () -> Unit = {},
    onNavigateRecommend: () -> Unit = {},
    onNavigateSettings: () -> Unit = {},
    onNavigateAddExpense: () -> Unit = {},
    onNavigateSpendingDetail: () -> Unit = {},
    onNavigateBudgetHistory: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(viewModel.selectedBudgetTab) }
    var fabExpanded by remember { mutableStateOf(false) }
    var showBudgetDialog by remember { mutableStateOf(false) }
    var showCreateGoalDialog by remember { mutableStateOf(false) }
    var showJoinGoalDialog by remember { mutableStateOf(false) }
    var editingExpense by remember { mutableStateOf<Pair<String, Spending>?>(null) }

    LaunchedEffect(Unit) {
        viewModel.startListeningToExpenses()
        viewModel.loadUserProfile()
        viewModel.startListeningToGoals()
    }

    val allExpenses by viewModel.expenses.collectAsState()
    val expenses = allExpenses.currentMonth() // budget bars show this month only
    val userProfile by viewModel.userProfile.collectAsState()
    val sharedGoals by viewModel.sharedGoals.collectAsState()
    val monthlyBudget = userProfile?.monthlyBudget ?: 1200.0
    val totalSpent = expenses.sumOf { it.second.amount }
    val categoryTotals = expenses.map { it.second }.groupBy { it.category }
        .mapValues { e -> e.value.sumOf { it.amount } }.entries.sortedByDescending { it.value }
    val savingsTarget = userProfile?.savingsTarget ?: 0.0
    val savedAmount by viewModel.savedAmount.collectAsState()
    val goalName = userProfile?.monthlyBudgetName?.ifBlank { null } ?: userProfile?.purpose?.ifBlank { null } ?: "Your Goal"
    val savingsProgress = if (savingsTarget > 0) (savedAmount / savingsTarget).toFloat().coerceIn(0f, 1f) else 0f

    if (showBudgetDialog) {
        BudgetEditDialog(currentBudget = monthlyBudget, currentTarget = savingsTarget,
            onDismiss = { showBudgetDialog = false },
            onSave = { b, t -> viewModel.updateMonthlyBudget(b); viewModel.updateSavingsTarget(t); showBudgetDialog = false })
    }
    if (showCreateGoalDialog) {
        CreateSharedGoalDialog(onDismiss = { showCreateGoalDialog = false },
            onCreate = { name, target, groupName, dm, dy ->
                viewModel.createGoal(name, target, groupName, dm, dy) {}
                showCreateGoalDialog = false
            })
    }
    if (showJoinGoalDialog) {
        JoinGoalDialog(onDismiss = { showJoinGoalDialog = false },
            onJoin = { code -> viewModel.joinGoal(code) { _, _ -> }; showJoinGoalDialog = false })
    }
    editingExpense?.let { (docId, spending) ->
        EditExpenseDialog(spending = spending, onDismiss = { editingExpense = null },
            onSave = { updated -> viewModel.updateExpense(docId, updated); editingExpense = null })
    }

    Scaffold(
        containerColor = AppColors.Background,
        bottomBar = { AppBottomBar(currentRoute = "budgets", onDashboard = onNavigateDashboard, onBudgets = {}, onRecommend = onNavigateRecommend, onSettings = onNavigateSettings) },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                AnimatedVisibility(visible = fabExpanded, enter = fadeIn() + slideInVertically { it }, exit = fadeOut() + slideOutVertically { it }) {
                    Column(horizontalAlignment = Alignment.End) {
                        FabMenuItem("Add Expense", Icons.Default.AddCircle) { fabExpanded = false; onNavigateAddExpense() }
                        Spacer(Modifier.height(12.dp))
                        FabMenuItem("Edit Budget", Icons.Default.Edit) { fabExpanded = false; showBudgetDialog = true }
                        Spacer(Modifier.height(12.dp))
                        FabMenuItem("Create Shared Goal", Icons.Default.Group) { fabExpanded = false; showCreateGoalDialog = true }
                        Spacer(Modifier.height(12.dp))
                        FabMenuItem("Join Shared Goal", Icons.Default.Link) { fabExpanded = false; showJoinGoalDialog = true }
                        Spacer(Modifier.height(12.dp))
                    }
                }
                FloatingActionButton(onClick = { fabExpanded = !fabExpanded }, containerColor = AppColors.Primary, contentColor = Color.White) {
                    Icon(if (fabExpanded) Icons.Default.Close else Icons.Default.Add, contentDescription = null)
                }
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            Spacer(Modifier.height(16.dp))
            Text("A snapshot of your budgets", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 22.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(16.dp))
            BudgetSpendingTabs(selectedTab, onTabSelected = { selectedTab = it; viewModel.selectedBudgetTab = it }, onNavClick = {}, modifier = Modifier)
            Spacer(Modifier.height(16.dp))
            if (selectedTab == 0) {
                BudgetOverviewContent(
                    userName = userProfile?.name ?: "Your",
                    monthlyBudget = monthlyBudget, totalSpent = totalSpent,
                    categoryTotals = categoryTotals, goalName = goalName,
                    savedAmount = savedAmount, savingsTarget = savingsTarget,
                    savingsProgress = savingsProgress, sharedGoals = sharedGoals,
                    viewModel = viewModel,
                    onNavigateBudgetHistory = onNavigateBudgetHistory
                )
            } else {
                ActivityContent(expenses = allExpenses, onDelete = { viewModel.deleteExpense(it) },
                    onEdit = { editingExpense = it },
                    onRowClick = { pair -> viewModel.selectSpending(pair.second, pair.first); onNavigateSpendingDetail() })
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BudgetSnapshotScreenPreview() { BudgetSnapshotScreen() }


/**
 * 1. What: Renders a single expandable FAB menu entry with a list of other button for add expense, edit budget, or create/join a goal.
 * 2. Who: Called by BudgetSnapshotScreen.
 * 3. When: Executed when the FAB is expanded.
 */
@Composable
fun FabMenuItem(label: String, icon: ImageVector, onClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(shape = RoundedCornerShape(8.dp), color = Color.White, shadowElevation = 4.dp) {
            Text(label, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.width(8.dp))
        SmallFloatingActionButton(onClick = onClick, containerColor = AppColors.Primary, contentColor = Color.White) {
            Icon(icon, contentDescription = label, modifier = Modifier.size(20.dp))
        }
    }
}

@Preview
@Composable
fun FabMenuItemPreview() {
    FabMenuItem(
        label = "Add Expense",
        icon = Icons.Default.AddCircle,
        onClick = {}
    ) }


/**
 * 1. What: Dialog for creating a new shared savings goal.
 * 2. Who: Called by BudgetSnapshotScreen.
 * 3. When: Executed when the user taps the create goal action from the FAB menu.
 */
@Composable
fun CreateSharedGoalDialog(onDismiss: () -> Unit, onCreate: (String, Double, String, Int, Int) -> Unit) {
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
    var name by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("") }
    var groupName by remember { mutableStateOf("") }
    var deadlineMonthStr by remember { mutableStateOf(currentMonth.toString()) }
    var deadlineYearStr by remember { mutableStateOf(currentYear.toString()) }
    val deadlineMonth = deadlineMonthStr.toIntOrNull()?.coerceIn(1, 12) ?: currentMonth
    val deadlineYear = deadlineYearStr.toIntOrNull() ?: currentYear
    val monthsLeft = ((deadlineYear - currentYear) * 12 + (deadlineMonth - currentMonth)).coerceAtLeast(1)
    val monthNames = listOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")

    AlertDialog(onDismissRequest = onDismiss,
        title = { Text("Create Shared Goal", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif) },
        text = {
            Column {
                Text("Set a goal, target, and deadline. Each member sets their own contribution plan after joining.", fontSize = 13.sp, color = Color.Gray)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Goal name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = target, onValueChange = { if (it.isEmpty() || it.toDoubleOrNull()?.let { v -> v >= 0 } == true) target = it }, label = { Text("Total group target ($)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = groupName, onValueChange = { groupName = it }, label = { Text("Group name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                Text("Deadline", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Spacer(Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = deadlineMonthStr, onValueChange = { if (it.isEmpty() || it.toIntOrNull()?.let { v -> v in 1..12 } == true) deadlineMonthStr = it }, label = { Text("Month (1-12)") }, singleLine = true, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = deadlineYearStr, onValueChange = { deadlineYearStr = it }, label = { Text("Year") }, singleLine = true, modifier = Modifier.weight(1f))
                }
                if (monthsLeft > 0) {
                    Spacer(Modifier.height(10.dp))
                    Surface(shape = RoundedCornerShape(8.dp), color = AppColors.Primary.copy(alpha = 0.1f), modifier = Modifier.fillMaxWidth()) {
                        Text(
                            monthsLeft.toString() + " months until " + monthNames.getOrElse(deadlineMonth - 1) { "" } + " " + deadlineYear + ". Each member sets their own contribution plan.",
                            fontSize = 12.sp, color = AppColors.Primary, modifier = Modifier.padding(10.dp), lineHeight = 18.sp
                        )
                    }
                }
            }
        },
        confirmButton = { Button(onClick = { if (name.isNotBlank() && target.isNotBlank()) onCreate(name, target.toDoubleOrNull() ?: 0.0, groupName, deadlineMonth, deadlineYear) }, colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)) { Text("Create") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Preview
@Composable
fun CreatedSharedGoalDialogPreview() {
    CreateSharedGoalDialog(
        onDismiss = {},
        onCreate = { _, _, _, _, _ -> }
    )
}


/**
 * 1. What: Dialog for joining an existing shared goal by entering the 6-letter.
 * 2. Who: Called by BudgetSnapshotScreen.
 * 3. When: Executed when the user taps the join goal action from the FAB menu.
 */
@Composable
fun JoinGoalDialog(onDismiss: () -> Unit, onJoin: (String) -> Unit) {
    var code by remember { mutableStateOf("") }
    AlertDialog(onDismissRequest = onDismiss,
        title = { Text("Join Shared Goal", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif) },
        text = {
            Column {
                Text("Enter the 6-letter invite code your friend shared with you.", fontSize = 13.sp, color = Color.Gray)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = code, onValueChange = { code = it.uppercase() }, label = { Text("Invite code") }, singleLine = true, modifier = Modifier.fillMaxWidth(), placeholder = { Text("e.g. ABC123") })
            }
        },
        confirmButton = { Button(onClick = { if (code.length == 6) onJoin(code) }, colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)) { Text("Join") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Preview
@Composable
fun JoinGoalDialogPreview() {
    JoinGoalDialog(
        onDismiss = {},
        onJoin = {}
    )
}


/**
 * 1. What: Displays a single shared savings goal with its name, group, deadline banner, copyable invite code, and overall progress and lets the member set their own contribution plans well as edit or delete the goal.
 * 2. Who: Called by BudgetSnapshotScreen.
 * 3. When: Executed when shared goals are loaded and shown on the budgets tab.
 */
@Composable
fun SharedGoalCard(goalId: String, goal: SavingsGoal, viewModel: SproutViewModel) {
    val contributions by viewModel.contributionsFlow(goalId).collectAsState(initial = emptyList())
    val allExpenses by viewModel.expenses.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val monthlyBudget = userProfile?.monthlyBudget ?: 1200.0
    val currentMonthSpent = allExpenses.currentMonth().sumOf { it.second.amount }
    val monthlySurplus = (monthlyBudget - currentMonthSpent).coerceAtLeast(0.0)
    val savedAmount by viewModel.savedAmount.collectAsState()
    val myUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val myExistingPlan = goal.memberPlans[myUid]
    var sliderValue by remember(myExistingPlan) { mutableStateOf(myExistingPlan?.savingsPct?.toFloat() ?: 0f) }
    var commitmentText by remember(myExistingPlan) { mutableStateOf(myExistingPlan?.totalCommitment?.toInt()?.toString() ?: "") }
    val myContribution = monthlySurplus * (sliderValue / 100.0)

    val monthsLeft = if (goal.deadlineMonth > 0 && goal.deadlineYear > 0)
        SharedGoalsRepository.monthsRemaining(goal.deadlineMonth, goal.deadlineYear) else 0
    val monthNames = listOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")
    val deadlineStr = if (goal.deadlineMonth > 0 && goal.deadlineYear > 0)
        monthNames.getOrElse(goal.deadlineMonth - 1) { "" } + " " + goal.deadlineYear else ""

    val progress = if (goal.targetAmount > 0) (goal.currentAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f) else 0f
    val clipboardManager = LocalClipboardManager.current
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var codeCopied by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    if (showEditDialog) {
        EditSharedGoalDialog(goal = goal, onDismiss = { showEditDialog = false },
            onSave = { name, target, groupName, dm, dy ->
                viewModel.updateGoal(goalId, name, target, groupName, dm, dy)
                showEditDialog = false
            })
    }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Goal", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete this goal? This cannot be undone.") },
            confirmButton = {
                Button(onClick = { viewModel.deleteGoal(goalId); showDeleteDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") } }
        )
    }

    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), color = Color(0xFFE8E6F0)) {
        Column(modifier = Modifier.padding(20.dp)) {

            // title + edit/delete
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(goal.name, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    if (goal.groupName.isNotBlank()) Text("Group: " + goal.groupName, fontSize = 12.sp, color = Color.Gray)
                }
                Row {
                    IconButton(onClick = { showEditDialog = true }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = AppColors.Primary, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = { showDeleteDialog = true }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(18.dp))
                    }
                }
            }

            // deadline banner
            if (deadlineStr.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                val deadlineColor = when {
                    monthsLeft <= 2 -> Color(0xFFCC0000)
                    monthsLeft <= 5 -> Color(0xFFE65100)
                    else -> AppColors.Primary
                }
                Surface(shape = RoundedCornerShape(10.dp), color = deadlineColor.copy(alpha = 0.1f), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DateRange, contentDescription = null, tint = deadlineColor, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Column {
                            Text(
                                "Deadline: $deadlineStr",
                                fontSize = 13.sp, color = deadlineColor, fontWeight = FontWeight.Bold
                            )
                            Text(
                                when {
                                    monthsLeft <= 1 -> "⚠️ Final month — make your contribution count!"
                                    monthsLeft <= 3 -> "🔥 $monthsLeft months left — pick up the pace!"
                                    else -> "$monthsLeft months remaining to hit your goal"
                                },
                                fontSize = 11.sp, color = deadlineColor
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // invite code chip
            Surface(
                onClick = {
                    clipboardManager.setText(AnnotatedString(goal.inviteCode))
                    codeCopied = true
                    scope.launch { delay(2000); codeCopied = false }
                },
                shape = RoundedCornerShape(8.dp),
                color = AppColors.Primary.copy(alpha = 0.15f)
            ) {
                Text(
                    if (codeCopied) "Copied!" else "Code: " + goal.inviteCode,
                    color = AppColors.Primary, fontWeight = FontWeight.Bold, fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            // group progress bar
            HorizontalDivider(color = Color(0xFFCCCCCC))
            Spacer(Modifier.height(10.dp))
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(8.dp), color = AppColors.Primary, trackColor = Color(0xFFDDDDE6))
            Spacer(Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(goal.currentAmount.toDollars() + " saved", color = Color(0xFF2E7D32), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text("Goal: " + goal.targetAmount.toDollars(), fontSize = 13.sp, color = Color.Gray)
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFCCCCCC))
            Spacer(Modifier.height(12.dp))

            // my contribution plan
            Text("My Contribution Plan", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(Modifier.height(4.dp))
            Text("Set your total commitment and what % of your monthly savings to allocate each month.", fontSize = 12.sp, color = Color.Gray, lineHeight = 17.sp)
            Spacer(Modifier.height(12.dp))

            // total commitment input
            OutlinedTextField(
                value = commitmentText,
                onValueChange = { if (it.isEmpty() || it.toDoubleOrNull()?.let { v -> v >= 0 } == true) commitmentText = it },
                label = { Text("My total contribution ($)") },
                placeholder = { Text("e.g. 1000") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            val commitmentAmt = commitmentText.toDoubleOrNull() ?: 0.0
            if (monthsLeft > 0 && commitmentAmt > 0) {
                val monthlyNeeded = commitmentAmt / monthsLeft
                Spacer(Modifier.height(6.dp))
                Surface(shape = RoundedCornerShape(8.dp), color = AppColors.Primary.copy(alpha = 0.1f), modifier = Modifier.fillMaxWidth()) {
                    Text(
                        monthlyNeeded.toDollars2() + "/month over " + monthsLeft + " months to reach your " + commitmentAmt.toDollars() + " commitment",
                        fontSize = 12.sp, color = AppColors.Primary, modifier = Modifier.padding(10.dp), lineHeight = 17.sp
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // savings % slider
            Text("Monthly savings % to allocate: " + sliderValue.toInt() + "%", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(2.dp))
            Text(sliderValue.toInt().toString() + "% of your " + monthlySurplus.toDollars() + " monthly surplus = " + myContribution.toDollars2() + "/month", fontSize = 12.sp, color = Color.Gray)
            Spacer(Modifier.height(6.dp))
            Slider(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                valueRange = 0f..100f,
                steps = 19,
                colors = SliderDefaults.colors(thumbColor = AppColors.Primary, activeTrackColor = AppColors.Primary),
                modifier = Modifier.fillMaxWidth()
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("0%", fontSize = 11.sp, color = Color.Gray)
                Text("100%", fontSize = 11.sp, color = Color.Gray)
            }

            // projection message
            if (commitmentAmt > 0 && myContribution > 0) {
                val monthsToComplete = Math.ceil(commitmentAmt / myContribution).toInt()
                val projectionColor = when {
                    monthsToComplete < monthsLeft -> Color(0xFF2E7D32)
                    monthsToComplete == monthsLeft -> AppColors.Primary
                    else -> Color(0xFFCC0000)
                }
                val projectionMsg = when {
                    monthsToComplete < monthsLeft ->
                        "🎉 At ${sliderValue.toInt()}% of your savings, you'll hit your ${commitmentAmt.toDollars()} commitment in $monthsToComplete month${if (monthsToComplete != 1) "s" else ""} — ${monthsLeft - monthsToComplete} month${if (monthsLeft - monthsToComplete != 1) "s" else ""} ahead of schedule!"
                    monthsToComplete == monthsLeft ->
                        "✅ At this rate you'll reach your ${commitmentAmt.toDollars()} commitment right on time in $monthsLeft month${if (monthsLeft != 1) "s" else ""}."
                    else ->
                        "⚠️ At ${sliderValue.toInt()}% you'll need $monthsToComplete month${if (monthsToComplete != 1) "s" else ""} to reach ${commitmentAmt.toDollars()}, but the deadline is in $monthsLeft. Consider increasing your % or reducing your commitment."
                }
                Spacer(Modifier.height(8.dp))
                Surface(shape = RoundedCornerShape(10.dp), color = projectionColor.copy(alpha = 0.1f), modifier = Modifier.fillMaxWidth()) {
                    Text(projectionMsg, fontSize = 12.sp, color = projectionColor, modifier = Modifier.padding(12.dp), lineHeight = 18.sp)
                }
            }

            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    viewModel.setMemberPlan(
                        goalId,
                        commitmentAmt,
                        sliderValue.toDouble(),
                        goal.deadlineMonth,
                        goal.deadlineYear,
                        monthlySurplus
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50)
            ) { Text("Save My Plan") }

            // member plans
            if (contributions.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFCCCCCC))
                Spacer(Modifier.height(8.dp))
                Text("Member Plans", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color.Gray)
                Spacer(Modifier.height(4.dp))
                val byUser = contributions.groupBy { it.userName }
                    .mapValues { e -> e.value.sumOf { it.amount } }
                    .entries.sortedByDescending { it.value }
                byUser.forEach { (uName, amount) ->
                    val userProgress = if (goal.targetAmount > 0) (amount / goal.targetAmount).toFloat().coerceIn(0f, 1f) else 0f
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.width(90.dp)) {
                            Text(uName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text(amount.toDollars() + " so far", fontSize = 10.sp, color = Color.Gray)
                        }
                        Spacer(Modifier.width(8.dp))
                        LinearProgressIndicator(progress = { userProgress }, modifier = Modifier.weight(1f).height(6.dp), color = AppColors.Primary, trackColor = Color(0xFFDDDDE6))
                        Spacer(Modifier.width(8.dp))
                        Text(amount.toDollars(), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(52.dp), textAlign = TextAlign.End)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SharedGoalCardPreview() {
    SharedGoalCard(
        goalId = "preview-goal-id",
        goal = SavingsGoal(
            name = "Paris 2027",
            groupName = "The Travelers",
            targetAmount = 4000.0,
            currentAmount = 1500.0,
            inviteCode = "ABC123",
            deadlineMonth = 12,
            deadlineYear = 2027
        ),
        viewModel = viewModel()
    )
}




/**
 * 1. What: Renders the budget overview tab content which shows information like total spent and remaining and spending in each category, it also shows the personal goal progress.
 * 2. Who: Called by BudgetSnapshotScreen.
 * 3. When: Executed when the budgets tab is clicked on the bottom bar.
 */
@Composable
fun BudgetOverviewContent(
    userName: String, monthlyBudget: Double, totalSpent: Double,
    categoryTotals: List<Map.Entry<String, Double>>, goalName: String,
    savedAmount: Double, savingsTarget: Double, savingsProgress: Float,
    sharedGoals: List<Pair<String, SavingsGoal>> = emptyList(),
    viewModel: SproutViewModel = viewModel(),
    onNavigateBudgetHistory: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Surface(modifier = Modifier.fillMaxWidth().clickable { onNavigateBudgetHistory() }, shape = RoundedCornerShape(20.dp), color = Color(0xFFE8E6F0)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(userName + "'s Budget", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "History", tint = Color.Gray, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.height(4.dp))
                Text("Monthly budget: " + monthlyBudget.toDollars(), fontSize = 13.sp, color = Color.Gray)
                Spacer(Modifier.height(16.dp))
                if (categoryTotals.isEmpty()) {
                    Text("No expenses yet!", fontSize = 14.sp, color = Color.Gray)
                } else {
                    categoryTotals.forEach { (category, spent) ->
                        val progress = (spent / monthlyBudget).toFloat().coerceIn(0f, 1f)
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(category + ":", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("${"%.0f".format(progress * 100)}%", fontSize = 13.sp, color = AppColors.Primary, fontWeight = FontWeight.SemiBold)
                            }
                            Spacer(Modifier.height(4.dp))
                            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(8.dp), color = AppColors.Primary, trackColor = Color(0xFFDDDDE6))
                            Spacer(Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(spent.toDollars2(), fontSize = 12.sp)
                                Text(monthlyBudget.toDollars(), fontSize = 12.sp, color = Color.Gray)
                            }
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                }
                HorizontalDivider(color = Color(0xFFCCCCCC), thickness = 1.dp)
                Spacer(Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total spent:", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(totalSpent.toDollars2(), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFFCC0000))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Remaining:", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text((monthlyBudget - totalSpent).coerceAtLeast(0.0).toDollars2(), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF2E7D32))
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), color = Color(0xFFE8E6F0)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(goalName, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                Spacer(Modifier.height(12.dp))
                LinearProgressIndicator(progress = { savingsProgress }, modifier = Modifier.fillMaxWidth().height(8.dp), color = AppColors.Primary, trackColor = Color(0xFFDDDDE6))
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(savedAmount.toDollars() + " saved", color = Color(0xFF2E7D32), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text("Goal: " + savingsTarget.toDollars(), fontSize = 14.sp, color = Color.Gray)
                }
            }
        }

        if (sharedGoals.isNotEmpty()) {
            Spacer(Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Shared Goals", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Default.Group, contentDescription = null, tint = AppColors.Primary, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(12.dp))
            sharedGoals.forEach { (goalId, goal) ->
                SharedGoalCard(goalId = goalId, goal = goal, viewModel = viewModel)
                Spacer(Modifier.height(12.dp))
            }
        }

        Spacer(Modifier.height(80.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun BudgetOverviewContentPreview() {
    BudgetOverviewContent(
        userName = "Alex",
        monthlyBudget = 1200.0,
        totalSpent = 740.0,
        categoryTotals = mapOf(
            "Groceries" to 320.0,
            "Dining Out" to 240.0,
            "Transport" to 180.0
        ).entries.sortedByDescending { it.value },
        goalName = "Paris 2027",
        savedAmount = 1500.0,
        savingsTarget = 4000.0,
        savingsProgress = 0.375f,
        sharedGoals = emptyList(),
        viewModel = viewModel(),
        onNavigateBudgetHistory = {}
    )
}

/**
 * 1. What: Renders the activity tab to show the individual spending of the user.
 * 2. Who: Called by BudgetSnapshotScreen to display the activity/transaction list tab.
 * 3. When: Executed when the user views the activity tab of the budget snapshot screen.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ActivityContent(expenses: List<Pair<String, Spending>>, onDelete: (String) -> Unit, onEdit: (Pair<String, Spending>) -> Unit, onRowClick: (Pair<String, Spending>) -> Unit = {}) {
    var selectedIds by remember { mutableStateOf(setOf<String>()) }
    var selectionMode by remember { mutableStateOf(false) }
    var showBulkEditDialog by remember { mutableStateOf(false) }
    var showBulkDeleteDialog by remember { mutableStateOf(false) }

    // bulk edit dialog — only edits category for all selected
    if (showBulkEditDialog) {
        var newCategory by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showBulkEditDialog = false },
            title = { Text("Edit ${selectedIds.size} Expenses", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif) },
            text = {
                Column {
                    Text("Update category for all selected expenses:", fontSize = 13.sp, color = Color.Gray)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = newCategory, onValueChange = { newCategory = it }, label = { Text("New category") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(onClick = {
                    expenses.filter { it.first in selectedIds }.forEach { (docId, spending) ->
                        onEdit(Pair(docId, spending.copy(category = newCategory)))
                    }
                    selectedIds = emptySet(); selectionMode = false; showBulkEditDialog = false
                }, colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)) { Text("Apply") }
            },
            dismissButton = { TextButton(onClick = { showBulkEditDialog = false }) { Text("Cancel") } }
        )
    }

    if (showBulkDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showBulkDeleteDialog = false },
            title = { Text("Delete ${selectedIds.size} Expenses", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete ${selectedIds.size} expenses? This cannot be undone.") },
            confirmButton = {
                Button(onClick = {
                    selectedIds.forEach { onDelete(it) }
                    selectedIds = emptySet(); selectionMode = false; showBulkDeleteDialog = false
                }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("Delete All", color = Color.White) }
            },
            dismissButton = { TextButton(onClick = { showBulkDeleteDialog = false }) { Text("Cancel") } }
        )
    }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        if (expenses.isEmpty()) {
            Spacer(Modifier.height(40.dp))
            Text("No expenses yet!\nTap + to add one.", textAlign = TextAlign.Center, fontSize = 16.sp, color = Color.Gray, modifier = Modifier.fillMaxWidth())
        } else {
            // selection mode toolbar
            if (selectionMode) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { selectionMode = false; selectedIds = emptySet() }) {
                        Text("Cancel", color = Color.Gray)
                    }
                    TextButton(onClick = {
                        selectedIds = if (selectedIds.size == expenses.size) emptySet()
                        else expenses.map { it.first }.toSet()
                    }) {
                        Text(if (selectedIds.size == expenses.size) "Deselect All" else "Select All", color = AppColors.Primary, fontSize = 13.sp)
                    }
                    Text("${selectedIds.size} selected", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Row {
                        TextButton(
                            onClick = { if (selectedIds.isNotEmpty()) showBulkEditDialog = true },
                            enabled = selectedIds.isNotEmpty()
                        ) { Text("Edit", color = if (selectedIds.isNotEmpty()) AppColors.Primary else Color.Gray) }
                        TextButton(
                            onClick = { if (selectedIds.isNotEmpty()) showBulkDeleteDialog = true },
                            enabled = selectedIds.isNotEmpty()
                        ) { Text("Delete", color = if (selectedIds.isNotEmpty()) Color.Red else Color.Gray) }
                    }
                }
                HorizontalDivider(color = Color(0xFFCCCCCC))
                Spacer(Modifier.height(4.dp))
            } else {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp)) {
                    Text("Date", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1.8f))
                    Text("Merchant", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(2f))
                    Text("Category", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1.8f))
                    Text("Amount", fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(1.4f), textAlign = TextAlign.End)
                }
                HorizontalDivider(color = Color(0xFFCCCCCC))
                Spacer(Modifier.height(4.dp))
            }

            expenses.forEach { (docId, spending) ->
                val isSelected = docId in selectedIds
                if (selectionMode) {
                    // in selection mode: tap to toggle, no swipe
                    Surface(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable {
                            selectedIds = if (isSelected) selectedIds - docId else selectedIds + docId
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) AppColors.Primary.copy(alpha = 0.15f) else Color(0xFFE8E6F0)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = { selectedIds = if (isSelected) selectedIds - docId else selectedIds + docId },
                                colors = CheckboxDefaults.colors(checkedColor = AppColors.Primary),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(spending.date, fontSize = 11.sp, modifier = Modifier.weight(1.8f))
                            Text(spending.merchant.ifBlank { "—" }, fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(2f))
                            Text(spending.category.ifBlank { "—" }, fontSize = 11.sp, modifier = Modifier.weight(1.8f))
                            Text(spending.amount.toDollars2(), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AppColors.Primary, modifier = Modifier.weight(1.4f), textAlign = TextAlign.End)
                        }
                    }
                } else {
                    val dismissState = rememberSwipeToDismissBoxState(confirmValueChange = { value ->
                        when (value) {
                            SwipeToDismissBoxValue.EndToStart -> { onDelete(docId); true }
                            SwipeToDismissBoxValue.StartToEnd -> { onEdit(Pair(docId, spending)); false }
                            else -> false
                        }
                    })
                    SwipeToDismissBox(state = dismissState, backgroundContent = {
                        val dir = dismissState.dismissDirection
                        val color = when (dir) { SwipeToDismissBoxValue.EndToStart -> Color(0xFFCC0000); SwipeToDismissBoxValue.StartToEnd -> Color(0xFF1976D2); else -> Color.Transparent }
                        val align = if (dir == SwipeToDismissBoxValue.EndToStart) Alignment.CenterEnd else Alignment.CenterStart
                        val icon = if (dir == SwipeToDismissBoxValue.EndToStart) Icons.Default.Delete else Icons.Default.Edit
                        Box(Modifier.fillMaxSize().background(color, RoundedCornerShape(12.dp)).padding(horizontal = 20.dp), contentAlignment = align) {
                            Icon(icon, contentDescription = null, tint = Color.White)
                        }
                    }, modifier = Modifier.padding(vertical = 4.dp)) {
                        Surface(
                            modifier = Modifier.fillMaxWidth()
                                .clickable { onRowClick(Pair(docId, spending)) }
                                .combinedClickable(
                                    onClick = { onRowClick(Pair(docId, spending)) },
                                    onLongClick = { selectionMode = true; selectedIds = setOf(docId) }
                                ),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFE8E6F0)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(spending.date, fontSize = 11.sp, modifier = Modifier.weight(1.8f))
                                Text(spending.merchant.ifBlank { "—" }, fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(2f))
                                Text(spending.category.ifBlank { "—" }, fontSize = 11.sp, modifier = Modifier.weight(1.8f))
                                Text(spending.amount.toDollars2(), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AppColors.Primary, modifier = Modifier.weight(1.4f), textAlign = TextAlign.End)
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(80.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun ActivityContentPreview() {
    ActivityContent(
        expenses = listOf(
            "1" to Spending("6/21/2026", "Chipotle", "Dining Out", "Personal", 45.50, "Dinner with friends", "", ""),
            "2" to Spending("6/19/2026", "Trader Joe's", "Groceries", "Personal", 82.30, "", "", ""),
            "3" to Spending("6/15/2026", "Shell", "Transport", "Personal", 38.00, "", "", "")
        ),
        onDelete = {},
        onEdit = {},
        onRowClick = {}
    )
}


/**
 * 1. What: Dialog for editing a single expense's amount, merchant, category, and note, pre-filled with the expense's current values.
 * 2. Who: Called by BudgetSnapshotScreen and SpendingDetail when the user chooses to edit an expense.
 * 3. When: Executed when the user swipes to edit or taps the edit action on an expense.
 */
@Composable
fun EditExpenseDialog(spending: Spending, onDismiss: () -> Unit, onSave: (Spending) -> Unit) {
    var amount by remember { mutableStateOf(spending.amount.toString()) }
    var merchant by remember { mutableStateOf(spending.merchant) }
    var category by remember { mutableStateOf(spending.category) }
    var note by remember { mutableStateOf(spending.description) }
    AlertDialog(onDismissRequest = onDismiss,
        title = { Text("Edit Expense", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif) },
        text = {
            Column {
                OutlinedTextField(value = amount, onValueChange = { input -> if (input.isEmpty() || input.toDoubleOrNull()?.let { it >= 0 } == true) amount = input }, label = { Text("Amount ($)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = merchant, onValueChange = { merchant = it }, label = { Text("Merchant") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("Note") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { Button(onClick = { onSave(spending.copy(amount = amount.toDoubleOrNull() ?: spending.amount, merchant = merchant, category = category, description = note)) }, colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Preview(showBackground = true)
@Composable
fun EditExpenseDialogPreview() {
    EditExpenseDialog(
        spending = Spending("6/21/2026", "Chipotle", "Dining Out", "Personal", 45.50, "Dinner with friends", "", ""),
        onDismiss = {},
        onSave = {}
    )
}


/**
 * 1. What: Dialog for editing the user's monthly budget and savings target amounts.
 * 2. Who: Called by BudgetSnapshotScreen.
 * 3. When: Executed when the user taps the edit-budget action from the FAB menu.
 */
@Composable
fun BudgetEditDialog(currentBudget: Double, currentTarget: Double, onDismiss: () -> Unit, onSave: (Double, Double) -> Unit) {
    var budgetText by remember { mutableStateOf(currentBudget.toInt().toString()) }
    var targetText by remember { mutableStateOf(currentTarget.toInt().toString()) }
    AlertDialog(onDismissRequest = onDismiss,
        title = { Text("Edit Budget", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif) },
        text = {
            Column {
                Text("Monthly Budget ($)", fontSize = 13.sp, color = Color.Gray)
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(value = budgetText, onValueChange = { if (it.isEmpty() || it.toDoubleOrNull()?.let { v -> v >= 0 } == true) budgetText = it }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(16.dp))
                Text("Savings Target ($)", fontSize = 13.sp, color = Color.Gray)
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(value = targetText, onValueChange = { if (it.isEmpty() || it.toDoubleOrNull()?.let { v -> v >= 0 } == true) targetText = it }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { Button(onClick = { onSave(budgetText.toDoubleOrNull() ?: currentBudget, targetText.toDoubleOrNull() ?: currentTarget) }, colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Preview(showBackground = true)
@Composable
fun BudgetEditDialogPreview() {
    BudgetEditDialog(
        currentBudget = 1200.0,
        currentTarget = 4000.0,
        onDismiss = {},
        onSave = { _, _ -> }
    )
}

/**
 * 1. What: Dialog for editing an existing shared savings goal.
 * 2. Who: Called by SharedGoalCard.
 * 3. When: Executed when the user taps the edit action on a shared goal card.
 */
@Composable
fun EditSharedGoalDialog(
    goal: SavingsGoal,
    onDismiss: () -> Unit,
    onSave: (String, Double, String, Int, Int) -> Unit
) {
    var name by remember { mutableStateOf(goal.name) }
    var target by remember { mutableStateOf(goal.targetAmount.toInt().toString()) }
    var groupName by remember { mutableStateOf(goal.groupName) }
    var deadlineMonthStr by remember { mutableStateOf(goal.deadlineMonth.takeIf { it > 0 }?.toString() ?: "12") }
    var deadlineYearStr by remember { mutableStateOf(goal.deadlineYear.takeIf { it > 0 }?.toString() ?: "2026") }
    AlertDialog(onDismissRequest = onDismiss,
        title = { Text("Edit Shared Goal", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif) },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Goal name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = target, onValueChange = { if (it.isEmpty() || it.toDoubleOrNull()?.let { v -> v >= 0 } == true) target = it }, label = { Text("Target amount ($)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = groupName, onValueChange = { groupName = it }, label = { Text("Group name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                Text("Deadline", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Spacer(Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = deadlineMonthStr, onValueChange = { deadlineMonthStr = it }, label = { Text("Month (1-12)") }, singleLine = true, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = deadlineYearStr, onValueChange = { deadlineYearStr = it }, label = { Text("Year") }, singleLine = true, modifier = Modifier.weight(1f))
                }
            }
        },
        confirmButton = { Button(onClick = { onSave(name, target.toDoubleOrNull() ?: goal.targetAmount, groupName, deadlineMonthStr.toIntOrNull() ?: goal.deadlineMonth, deadlineYearStr.toIntOrNull() ?: goal.deadlineYear) }, colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Preview(showBackground = true)
@Composable
fun EditSharedGoalDialogPreview() {
    EditSharedGoalDialog(
        goal = SavingsGoal(
            name = "Paris 2027",
            groupName = "The Travelers",
            targetAmount = 4000.0,
            currentAmount = 1500.0,
            inviteCode = "ABC123",
            deadlineMonth = 12,
            deadlineYear = 2027
        ),
        onDismiss = {},
        onSave = { _, _, _, _, _ -> }
    )
}