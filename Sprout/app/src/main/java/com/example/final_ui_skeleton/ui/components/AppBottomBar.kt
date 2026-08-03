package com.example.final_ui_skeleton.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

/**
 * 1. What: Bottom navigation bar shared across all main screens.
 * 2. Who: Called by Dashboard, Budgets, Settings, and AI screens.
 * 3. When: Executed when any main screen is composed.
 */
@Composable
fun AppBottomBar(
    currentRoute: String = "dashboard",
    onDashboard: () -> Unit = {},
    onBudgets: () -> Unit = {},
    onRecommend: () -> Unit = {},
    onSettings: () -> Unit = {}
) {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            selected = currentRoute == "dashboard",
            onClick = onDashboard,
            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
            label = { Text("Dashboard") }
        )
        NavigationBarItem(
            selected = currentRoute == "budgets",
            onClick = onBudgets,
            icon = { Icon(Icons.Default.BarChart, contentDescription = "Budgets") },
            label = { Text("Budgets") }
        )
        NavigationBarItem(
            selected = currentRoute == "recommend",
            onClick = onRecommend,
            icon = { Icon(Icons.Default.Lightbulb, contentDescription = "Recommend") },
            label = { Text("Insights") }
        )
        NavigationBarItem(
            selected = currentRoute == "settings",
            onClick = onSettings,
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") }
        )
    }
}

@Preview
@Composable
fun PreviewAppBottomBar() {
    AppBottomBar()
}