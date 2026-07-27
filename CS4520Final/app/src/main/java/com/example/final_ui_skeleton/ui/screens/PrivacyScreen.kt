package com.example.final_ui_skeleton.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.final_ui_skeleton.ui.components.AppColors
import com.example.final_ui_skeleton.ui.components.PrimaryButton
import com.example.final_ui_skeleton.ui.components.ScreenHeading

/**
 * 1. What: Displays privacy policy information before the user enters the app.
 * 2. Who: Called by the NavHost after the savings goal screen.
 * 3. When: Executed as the last step before navigating to the dashboard.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyScreen(
    onTakeHome: () -> Unit = {},
    onBack: () -> Unit = {}
) {
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
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp)) {
            Spacer(Modifier.height(16.dp))
            ScreenHeading("No guilty pleasure here!")
            Spacer(Modifier.height(32.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AttachMoney, contentDescription = null, tint = AppColors.Primary)
                Spacer(Modifier.width(8.dp))
                Text("Your data is never sold to third-party.")
            }
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = AppColors.Primary)
                Spacer(Modifier.width(8.dp))
                Text("Rest assured - your data is always encrypted by latest security standards.")
            }
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.RemoveRedEye, contentDescription = null, tint = AppColors.Primary)
                Spacer(Modifier.width(8.dp))
                Text("Only you can see your spending and savings - even in a shared budget.")
            }
            Spacer(Modifier.height(24.dp))
            Text("To view more about our privacy and security standards and protections visit:")
            Text("www.sproutbudget.com", color = AppColors.Primary)

            Spacer(Modifier.weight(1f))
            PrimaryButton(text = "Take me home", onClick = onTakeHome, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PrivacyScreenPreview() {
    PrivacyScreen()
}