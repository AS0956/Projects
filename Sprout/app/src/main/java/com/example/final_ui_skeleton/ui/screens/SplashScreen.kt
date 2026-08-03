package com.example.final_ui_skeleton.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.final_ui_skeleton.R
import com.example.final_ui_skeleton.ui.components.AppColors
import kotlinx.coroutines.delay

/**
 * 1. What: Displays a branded launch screen when the app starts which shows Sprout Logo.
 * 2. Who: Called by AppNavigation.
 * 3. When: Executes at the start of the app running.
 */
@Composable
fun SplashScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000)
        onFinished()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.sprout_logo),
            contentDescription = "Sprout logo",
            modifier = Modifier.size(120.dp)
        )
        Spacer(Modifier.height(24.dp))
        Text(
            "Sprout",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
            color = AppColors.Primary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "your personal finance companion",
            fontSize = 14.sp,
            color = Color.Gray
        )
        Spacer(Modifier.height(40.dp))
        CircularProgressIndicator(
            color = AppColors.Primary,
            strokeWidth = 3.dp,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Preview
@Composable
fun PreviewSplashScreen() {
    SplashScreen({})
}
