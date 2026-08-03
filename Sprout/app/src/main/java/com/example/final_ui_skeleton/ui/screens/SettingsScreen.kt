package com.example.final_ui_skeleton.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.final_ui_skeleton.R
import com.example.final_ui_skeleton.ui.components.AppBottomBar
import com.example.final_ui_skeleton.ui.components.AppColors
import com.example.final_ui_skeleton.viewmodel.SproutViewModel
import kotlinx.coroutines.launch
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api


/**
 * 1. What: Lets the user view and edit their profile, it also has option to save the changes made by user and a singout button.
 * 2. Who: Called by the NavHost when the user navigates to the settings tab.
 * 3. When: Executed when the user selects settings, loading their profile on entry.
 */
@Composable
fun SettingsScreen(
    viewModel: SproutViewModel = viewModel(),
    onLogout: () -> Unit = {},
    onNavigateDashboard: () -> Unit = {},
    onNavigateBudgets: () -> Unit = {},
    onNavigateRecommend: () -> Unit = {}
) {
    LaunchedEffect(Unit) { viewModel.loadUserProfile() }

    val userProfile by viewModel.userProfile.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var name by remember(userProfile) { mutableStateOf(userProfile?.name ?: "") }
    var city by remember(userProfile) { mutableStateOf(userProfile?.city ?: "") }
    var email by remember(userProfile) { mutableStateOf(userProfile?.email ?: "") }
    var password by remember { mutableStateOf("") }
    var incomeBracket by remember(userProfile) { mutableStateOf(userProfile?.incomeBracket ?: "") }
    var occupation by remember(userProfile) { mutableStateOf(userProfile?.occupation ?: "") }
    var purpose by remember(userProfile) { mutableStateOf(userProfile?.purpose ?: "") }
    var showPassword by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = AppColors.Background,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = AppColors.Primary,
                    contentColor = Color.White
                )
            }
        },
        bottomBar = {
            AppBottomBar(
                currentRoute = "settings",
                onDashboard = onNavigateDashboard,
                onBudgets = onNavigateBudgets,
                onRecommend = onNavigateRecommend,
                onSettings = {}
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Settings",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.sprout_logo),
                contentDescription = "Sprout logo",
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(8.dp))

            // city dropdown
            SettingsDropdown(
                label = "City",
                selected = city,
                options = listOf("New York", "Los Angeles", "Chicago", "Houston", "Phoenix", "Philadelphia", "San Antonio", "San Diego", "Dallas", "Boston", "Seattle", "Denver", "Atlanta", "Miami", "Austin", "Portland", "Las Vegas", "Minneapolis", "Detroit", "Nashville"),
                onSelect = { city = it }
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("New Password (leave blank to keep current)") },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    androidx.compose.material3.TextButton(onClick = { showPassword = !showPassword }) {
                        Text(if (showPassword) "Hide" else "Show", fontSize = 12.sp, color = AppColors.Primary)
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // income bracket dropdown
            SettingsDropdown(
                label = "Income Bracket",
                selected = incomeBracket,
                options = listOf("Under $25,000", "$25,000 - $50,000", "$50,000 - $75,000", "$75,000 - $100,000", "$100,000 - $150,000", "Over $150,000"),
                onSelect = { incomeBracket = it }
            )
            Spacer(modifier = Modifier.height(8.dp))

            // occupation dropdown
            SettingsDropdown(
                label = "Occupation/Industry",
                selected = occupation,
                options = listOf("Student", "Education", "Finance", "Healthcare", "Technology", "Retail", "Food & Hospitality", "Government", "Arts & Media", "Construction", "Legal", "Other"),
                onSelect = { occupation = it }
            )
            Spacer(modifier = Modifier.height(8.dp))

            // savings goal dropdown
            SettingsDropdown(
                label = "What are you saving for?",
                selected = purpose,
                options = listOf("Vacation", "Education", "Big Purchase", "Retirement", "Debt Payoff", "Emergency Fund", "A Home", "Other"),
                onSelect = { purpose = it }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    viewModel.updateProfileField("name", name)
                    viewModel.updateProfileField("city", city)
                    viewModel.updateProfileField("email", email)
                    viewModel.updateProfileField("incomeBracket", incomeBracket)
                    viewModel.updateProfileField("occupation", occupation)
                    viewModel.updateProfileField("purpose", purpose)
                    if (password.isNotEmpty()) {
                        viewModel.updatePassword(password)
                        password = ""
                    }
                    scope.launch {
                        snackbarHostState.showSnackbar("✓ Changes saved!")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                Text("Save Changes", fontWeight = FontWeight.SemiBold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { viewModel.signOut(onDone = onLogout) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                Text("Sign Out", color = Color.White, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewSettingsScreen() {
    SettingsScreen()
}


/**
 * 1. What: A read-only dropdown field that displays the selected value and lets user choose from the dropdown.
 * 2. Who: Called by SettingsScreen for the city, income bracket, occupation, and savings purpose fields.
 * 3. When: Executed whenever one of the settings dropdown fields is rendered.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDropdown(label: String, selected: String, options: List<String>, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
    ) {
        OutlinedTextField(
            value = selected.ifBlank { "Select..." },
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = { onSelect(option); expanded = false }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsDropdownPreview() {
    var selected by remember { mutableStateOf("Technology") }
    SettingsDropdown(
        label = "Occupation/Industry",
        selected = selected,
        options = listOf("Student", "Education", "Finance", "Healthcare", "Technology", "Retail", "Other"),
        onSelect = { selected = it }
    )
}