package com.example.final_ui_skeleton.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.final_ui_skeleton.model.User
import com.example.final_ui_skeleton.ui.components.AppColors
import com.example.final_ui_skeleton.ui.components.PrimaryButton
import com.example.final_ui_skeleton.viewmodel.SproutViewModel

private val incomeBrackets = listOf(
    "Under $25,000", "$25,000 - $50,000", "$50,000 - $75,000",
    "$75,000 - $100,000", "$100,000 - $150,000", "Over $150,000"
)

private val occupations = listOf(
    "Student", "Education", "Finance", "Healthcare", "Technology",
    "Retail", "Food & Hospitality", "Government", "Arts & Media",
    "Construction", "Legal", "Other"
)

private val savingGoals = listOf(
    "Vacation", "Education", "Big Purchase", "Retirement", "Debt Payoff",
    "Emergency Fund", "A Home", "Other"
)

private val usCities = listOf(
    "New York", "Los Angeles", "Chicago", "Houston", "Phoenix",
    "Philadelphia", "San Antonio", "San Diego", "Dallas", "Boston",
    "Austin", "Seattle", "Denver", "Atlanta", "Miami", "Other"
)

/**
 * 1. What: Collects user info during onboarding and saves to Firestore via Firebase Auth.
 * 2. Who: Called by the NavHost during the sign up flow.
 * 3. When: Executed when a new user creates an account.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicInformationScreen(
    viewModel: SproutViewModel = viewModel(),
    onSuccess: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var incomeBracket by remember { mutableStateOf("") }
    var occupation by remember { mutableStateOf("") }
    var purpose by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var cityError by remember { mutableStateOf("") }
    var incomeError by remember { mutableStateOf("") }
    var occupationError by remember { mutableStateOf("") }
    var purposeError by remember { mutableStateOf("") }

    fun validate(): Boolean {
        var valid = true
        nameError = if (name.isBlank()) { valid = false; "Name is required" } else ""
        emailError = when {
            email.isBlank() -> { valid = false; "Email is required" }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> { valid = false; "Enter a valid email" }
            else -> ""
        }
        passwordError = when {
            password.isBlank() -> { valid = false; "Password is required" }
            password.length < 6 -> { valid = false; "Password must be at least 6 characters" }
            else -> ""
        }
        cityError = if (city.isBlank()) { valid = false; "City is required" } else ""
        incomeError = if (incomeBracket.isBlank()) { valid = false; "Income bracket is required" } else ""
        occupationError = if (occupation.isBlank()) { valid = false; "Occupation is required" } else ""
        purposeError = if (purpose.isBlank()) { valid = false; "Savings goal is required" } else ""
        return valid
    }

    Scaffold(
        containerColor = AppColors.Background,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.Background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Let's start with the basics.",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 27.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it; nameError = "" },
                label = { Text("Name") },
                isError = nameError.isNotEmpty(),
                supportingText = { if (nameError.isNotEmpty()) Text(nameError, color = Color.Red) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; emailError = "" },
                label = { Text("Email") },
                isError = emailError.isNotEmpty(),
                supportingText = { if (emailError.isNotEmpty()) Text(emailError, color = Color.Red) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it; passwordError = "" },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                isError = passwordError.isNotEmpty(),
                supportingText = { if (passwordError.isNotEmpty()) Text(passwordError, color = Color.Red) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            DropdownField(
                label = "City",
                options = usCities,
                selected = city,
                onSelect = { city = it; cityError = "" },
                error = cityError
            )

            Spacer(modifier = Modifier.height(8.dp))

            DropdownField(
                label = "Income Bracket",
                options = incomeBrackets,
                selected = incomeBracket,
                onSelect = { incomeBracket = it; incomeError = "" },
                error = incomeError
            )

            Spacer(modifier = Modifier.height(8.dp))

            DropdownField(
                label = "Occupation/Industry",
                options = occupations,
                selected = occupation,
                onSelect = { occupation = it; occupationError = "" },
                error = occupationError
            )

            Spacer(modifier = Modifier.height(8.dp))

            DropdownField(
                label = "What are you saving for?",
                options = savingGoals,
                selected = purpose,
                onSelect = { purpose = it; purposeError = "" },
                error = purposeError
            )

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = "Continue",
                onClick = {
                    if (validate()) {
                        val user = User(
                            name = name,
                            city = city,
                            email = email,
                            password = password,
                            incomeBracket = incomeBracket,
                            occupation = occupation,
                            purpose = purpose
                        )
                        viewModel.signUp(email, password, user, onSuccess = onSuccess)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    error: String = ""
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
            isError = error.isNotEmpty(),
            supportingText = { if (error.isNotEmpty()) Text(error, color = Color.Red) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewBasicInformationScreen() {
    BasicInformationScreen()
}