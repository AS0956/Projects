package com.example.final_ui_skeleton.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.final_ui_skeleton.R
import com.example.final_ui_skeleton.ui.components.AppBackground
import com.example.final_ui_skeleton.ui.components.AppBottomBar
import com.example.final_ui_skeleton.ui.components.AppColors
import com.example.final_ui_skeleton.ui.components.PrimaryButton
import com.example.final_ui_skeleton.ui.components.ScreenHeading

/**
 * 1. What: Displays the welcome screen with the Sprout logo and a continue button.
 * 2. Who: Called by the NavHost when the app is first launched.
 * 3. When: Executed at app startup before the user signs in.
 */
@Composable
fun WelcomeScreen(onContinue: () -> Unit) {
    Scaffold(
        containerColor = AppColors.Background,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp), // add this
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.sprout_logo),
                contentDescription = "Sprout logo",
                modifier = Modifier.size(100.dp)
            )

            Spacer(Modifier.height(24.dp))

            ScreenHeading("Welcome to Sprout!")

            Spacer(Modifier.height(8.dp))

            Text("Here's to your financial well-being!")

            Spacer(Modifier.height(32.dp))

            PrimaryButton(
                text = "Continue",
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreen(onContinue = {})
}