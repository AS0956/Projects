package com.example.final_ui_skeleton.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.final_ui_skeleton.model.Spending
import com.example.final_ui_skeleton.ui.components.AppBottomBar
import com.example.final_ui_skeleton.ui.components.AppColors
import java.text.NumberFormat
import java.util.Locale

private fun Double.toDollars2() = "$${NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 2; minimumFractionDigits = 2 }.format(this)}"

/**
 * 1. What: Shows full details of a single expense with edit and delete options.
 * 2. Who: Called by the NavHost when the user taps a spending row.
 * 3. When: Executed when a spending item is selected from the activity tab.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpendingDetail(
    spending: Spending,
    onNavigateDashboard: () -> Unit = {},
    onNavigateBudgets: () -> Unit = {},
    onNavigateRecommend: () -> Unit = {},
    onNavigateSettings: () -> Unit = {},
    onBack: () -> Unit = {},
    onDelete: () -> Unit = {},
    onEdit: (Spending) -> Unit = {}
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Expense", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete this ${spending.category} expense of ${spending.amount.toDollars2()}?") },
            confirmButton = {
                Button(
                    onClick = { showDeleteDialog = false; onDelete() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("Delete", color = Color.White) }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") } }
        )
    }

    if (showEditDialog) {
        EditExpenseDialog(
            spending = spending,
            onDismiss = { showEditDialog = false },
            onSave = { updated -> showEditDialog = false; onEdit(updated) }
        )
    }

    Scaffold(
        containerColor = AppColors.Background,
        topBar = {
            TopAppBar(
                title = { Text("Spending Detail", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = AppColors.Primary)
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
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
            Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            listOf(
                "Date" to spending.date,
                "Merchant" to spending.merchant.ifBlank { "—" },
                "Category" to spending.category,
                "Amount" to spending.amount.toDollars2()
            ).forEach { (label, value) ->
                DetailRow(label = label, value = value)
                Spacer(Modifier.height(8.dp))
            }

            if (spending.description.isNotBlank()) {
                DetailRow(label = "Note", value = spending.description)
                Spacer(Modifier.height(8.dp))
            }

            if (spending.receiptImagePath.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text("Receipt Photo", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.align(Alignment.Start))
                Spacer(Modifier.height(8.dp))
                Surface(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                    AsyncImage(
                        model = Uri.parse(spending.receiptImagePath),
                        contentDescription = "Receipt",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(Modifier.height(8.dp))
            }

            if (spending.receiptText.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text("Scanned Receipt Text", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.align(Alignment.Start))
                Spacer(Modifier.height(8.dp))
                Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = Color(0xFFE8E6F0)) {
                    Text(spending.receiptText, fontSize = 13.sp, color = Color.DarkGray, modifier = Modifier.padding(16.dp), lineHeight = 20.sp)
                }
            }

            if (spending.description.isBlank() && spending.receiptText.isBlank() && spending.receiptImagePath.isBlank()) {
                Spacer(Modifier.height(8.dp))
                Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = Color(0xFFE8E6F0)) {
                    Text(
                        "No additional details.\nTap the camera icon when adding expenses to scan a receipt.",
                        fontSize = 13.sp, color = Color.Gray, textAlign = TextAlign.Center,
                        modifier = Modifier.padding(20.dp), lineHeight = 20.sp
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // delete button at bottom too
            Button(
                onClick = { showDeleteDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Delete Expense", color = Color.White, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewSpendingDetail() {
    SpendingDetail(Spending("6/21/2026", "Chipotle", "Dining Out", "Personal", 45.50, "Dinner with friends", "TOTAL $45.50", ""))
}


/**
 * 1. What: Renders a row with a label on the right side and the value on the right side
 * 2. Who: Called by SpendingDetail to display each field of an expense (date, merchant, category, amount, note).
 * 3. When: Executed for each detail field while the spending detail screen is built.
 */
@Composable
fun DetailRow(label: String, value: String) {
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = Color(0xFFE8E6F0)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color.Gray)
            Text(value, fontWeight = FontWeight.Bold, fontSize = 15.sp, textAlign = TextAlign.End,
                modifier = Modifier.weight(1f, fill = false).padding(start = 16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailRowPreview() {
    DetailRow(
        label = "Merchant",
        value = "Chipotle"
    )
}