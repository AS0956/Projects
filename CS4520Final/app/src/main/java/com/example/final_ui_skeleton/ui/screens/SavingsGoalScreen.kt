package com.example.final_ui_skeleton.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.final_ui_skeleton.ui.components.AppColors
import com.example.final_ui_skeleton.ui.components.PrimaryButton
import com.example.final_ui_skeleton.ui.components.ScreenHeading
import com.example.final_ui_skeleton.viewmodel.SproutViewModel

/**
 * 1. What: Lets the user pick their savings goal and target amount during onboarding.
 * 2. Who: Called by the NavHost during the onboarding flow.
 * 3. When: Executed after the demographic screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingsGoalScreen(
    viewModel: SproutViewModel? = null,
    options: List<String> = listOf("Vacation", "Education", "Big purchase", "Retirement", "Debt", "Other"),
    onNext: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val selected = remember { mutableStateMapOf<String, Boolean>() }
    var targetAmount by remember { mutableStateOf("") }

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
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            ScreenHeading("What are you\nsaving for?")
            Spacer(Modifier.height(16.dp))
            Text("Pick one or more. You can always add more later.", textAlign = TextAlign.Center, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Spacer(Modifier.height(24.dp))

            Column(Modifier.fillMaxWidth()) {
                options.forEach { option ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = selected[option] == true, onCheckedChange = { selected[option] = it }, colors = CheckboxDefaults.colors(checkedColor = AppColors.Primary))
                        Spacer(Modifier.width(16.dp))
                        Text(option, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = targetAmount, onValueChange = { targetAmount = it }, label = { Text("Savings target ($)") }, placeholder = { Text("e.g. 5000") }, singleLine = true, modifier = Modifier.fillMaxWidth())

            Spacer(Modifier.weight(1f))
            PrimaryButton(
                text = "Next",
                onClick = {
                    val goals = selected.filter { it.value }.keys.toList()
                    val target = targetAmount.toDoubleOrNull() ?: 0.0
                    if (viewModel != null) {
                        viewModel.updateProfileField("purpose", goals.joinToString(", "))
                        viewModel.updateSavingsTarget(target)
                    }
                    onNext()
                },
                modifier = Modifier.widthIn(min = 120.dp)
            )
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SavingsGoalScreenPreview() {
    SavingsGoalScreen()
}