package com.example.final_ui_skeleton.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.final_ui_skeleton.ui.components.AppColors
import com.example.final_ui_skeleton.ui.components.LabeledTextField
import com.example.final_ui_skeleton.ui.components.PrimaryButton
import com.example.final_ui_skeleton.ui.components.ScreenHeading
import com.example.final_ui_skeleton.viewmodel.SproutViewModel
import androidx.compose.runtime.LaunchedEffect

/**
 * 1. What: Handles email and password login using Firebase Authentication.
 * 2. Who: Called by the NavHost when the user needs to sign in.
 * 3. When: Executed when the user taps continue on the welcome screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    viewModel: SproutViewModel = viewModel(),
    onSignIn: () -> Unit = {},
    onGoogle: () -> Unit = {},
    onApple: () -> Unit = {},
    onCreateAccount: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authMessage by viewModel.authMessage.collectAsState()

    LaunchedEffect(Unit) { viewModel.clearAuthMessage() }

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
            Spacer(Modifier.height(40.dp))

            ScreenHeading("Sign in")

            Spacer(Modifier.height(40.dp))

            LabeledTextField(
                email, { email = it }, "Email address",
                leadingIcon = Icons.Filled.Email,
            )
            Spacer(Modifier.height(12.dp))
            LabeledTextField(
                password, { password = it }, "Password",
                isPassword = true,
                leadingIcon = Icons.Filled.Lock
            )

            if (authMessage != null) {
                Spacer(Modifier.height(8.dp))
                Text(authMessage ?: "", color = Color.Red, fontSize = 13.sp)
            }

            Spacer(Modifier.height(32.dp))

            PrimaryButton(
                text = "Sign in",
                onClick = { viewModel.signIn(email, password, onSuccess = onSignIn) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.weight(1f))

            Text(
                "New here?",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp
            )
            Spacer(Modifier.height(4.dp))
            TextButton(onClick = onCreateAccount) {
                Text(
                    "Create an account",
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignInScreenPreview() {
    SignInScreen()
}