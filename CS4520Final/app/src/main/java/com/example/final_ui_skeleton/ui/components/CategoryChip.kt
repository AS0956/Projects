package com.example.final_ui_skeleton.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.internal.isLiveLiteralsEnabled
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 1. What: Selectable chip used for picking expense categories.
 * 2. Who: Called by AddExpenseScreen.
 * 3. When: Executed when the add expense form is composed.
 */
// used selecting categories
@Composable
fun CategoryChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (selected) AppColors.Primary else Color.White,
        shape = RoundedCornerShape(50),
        modifier = Modifier
            .border(1.dp, Color.LightGray, RoundedCornerShape(50))
            .clickable { onClick() }
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else Color.Black,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

@Composable
@Preview
fun PreviewCategoryChip() {
    CategoryChip(label = "test", selected = true, onClick = {})
}
