package com.example.final_ui_skeleton.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 1. What: Tab selector to switch between budget overview and activity views.
 * 2. Who: Called by BudgetSnapshotScreen and SpendingHistoryScreen.
 * 3. When: Executed when the budgets screen is composed.
 */
// A composable to switch between budget and history
@Composable
fun BudgetSpendingTabs(selectedTab: Int,
                       onTabSelected: (Int) -> Unit,
                       onNavClick: () -> Unit,
                       modifier: Modifier) {

    val tabs = listOf("Budgets Overview", "Activity")

    // Pill tab selector
    Row(
        Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(Color.White)
            .padding(4.dp)
    ) {
        tabs.forEachIndexed { index, title ->
            val isSelected = index == selectedTab
            Box(
                Modifier
                    .weight(1f)
                    .clip(CircleShape)
                    .background(if (isSelected) AppColors.Card else Color.Transparent)
                    .clickable { onTabSelected(index)}
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    title,
                    color = if (isSelected) AppColors.Primary else Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
@Preview
fun PreviewBudgetSpendingTabs() {
    BudgetSpendingTabs(
        0, { 0 },
        onNavClick = {},
        modifier = Modifier,
    )
}