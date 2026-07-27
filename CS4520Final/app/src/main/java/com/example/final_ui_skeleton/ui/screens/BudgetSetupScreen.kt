package com.example.final_ui_skeleton.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.final_ui_skeleton.ui.components.AppColors
import com.example.final_ui_skeleton.ui.components.PrimaryButton
import com.example.final_ui_skeleton.ui.components.ScreenHeading
import com.example.final_ui_skeleton.viewmodel.SproutViewModel

/**
 * 1. What: Lets the user set their monthly budget during onboarding.
 * 2. Who: Called by the NavHost after the savings goal screen.
 * 3. When: Executed during onboarding before the privacy screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetSetupScreen(
    viewModel: SproutViewModel? = null,
    onNext: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    var monthlyBudget by remember { mutableStateOf("") }
    var goalName by remember { mutableStateOf("") }
    var goalTarget by remember { mutableStateOf("") }

    Scaffold(
        containerColor = AppColors.Background,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.Background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))
            ScreenHeading("Let's set up\nyour budget.")
            Spacer(Modifier.height(8.dp))
            Text(
                "This helps us track your spending and keep you on course.",
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(32.dp))

            // monthly budget
            Text(
                "Monthly Budget",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = monthlyBudget,
                onValueChange = { monthlyBudget = it },
                label = { Text("Amount ($)") },
                placeholder = { Text("e.g. 1200") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(28.dp))

            // savings goal name (like "Paris 2027")
            Text(
                "Name your savings goal",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Give it a name so it feels real! e.g. \"Paris 2027\", \"Emergency Fund\"",
                fontSize = 12.sp,
                color = androidx.compose.ui.graphics.Color.Gray,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = goalName,
                onValueChange = { goalName = it },
                label = { Text("Goal name") },
                placeholder = { Text("e.g. Paris 2027") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            // savings target
            Text(
                "Savings target ($)",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = goalTarget,
                onValueChange = { goalTarget = it },
                label = { Text("Target amount ($)") },
                placeholder = { Text("e.g. 4000") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.weight(1f))

            PrimaryButton(
                text = "Next",
                onClick = {
                    val budget = monthlyBudget.toDoubleOrNull() ?: 1200.0
                    val target = goalTarget.toDoubleOrNull() ?: 0.0
                    viewModel?.updateProfileField("monthlyBudgetName", goalName)
                    viewModel?.updateSavingsTarget(target)
                    viewModel?.updateMonthlyBudget(budget)
                    onNext()
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BudgetSetupScreenPreview() {
    BudgetSetupScreen()
}