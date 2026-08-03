package com.example.final_ui_skeleton.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.final_ui_skeleton.model.Spending

/**
 * 1. What: A swipeable row showing expense info, swipe left to delete.
 * 2. Who: Called by SpendingHistoryScreen.
 * 3. When: Executed for each expense in the spending list.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpendingRow(spending: Spending, onClick: () -> Unit, onDelete: () -> Unit = {}) {

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else false
        }
    )

    val color by animateColorAsState(
        if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) Color.Red
        else Color.LightGray,
        label = "swipe color"
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(color),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text("Delete", color = Color.White, modifier = Modifier.padding(end = 16.dp))
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CircleShape)
                .background(Color.LightGray)
                .height(56.dp)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = spending.date, modifier = Modifier.weight(1f), fontFamily = FontFamily.Serif, textAlign = TextAlign.Center)
                Text(text = spending.merchant.ifBlank { "—" }, modifier = Modifier.weight(1f), fontFamily = FontFamily.Serif, textAlign = TextAlign.Center)
                Text(text = spending.category, modifier = Modifier.weight(1f), fontFamily = FontFamily.Serif, textAlign = TextAlign.Center)
                Text(text = "$${spending.amount}", modifier = Modifier.weight(1f), fontFamily = FontFamily.Serif, textAlign = TextAlign.Center)
            }
        }
    }
}

@Preview
@Composable
fun PreviewSpendingRow() {
    val spend = Spending("3/9/2026", "Whole Foods", "Food", "Personal", 32.00, "Groceries")
    SpendingRow(spend, {}, {})
}