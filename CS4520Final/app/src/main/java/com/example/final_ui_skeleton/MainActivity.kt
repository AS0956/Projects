package com.example.final_ui_skeleton

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.final_ui_skeleton.ui.navigation.AppNavigation
import com.example.final_ui_skeleton.ui.theme.Final_UI_SkeletonTheme
import com.example.final_ui_skeleton.viewmodel.SproutViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Final_UI_SkeletonTheme {
                val viewModel: SproutViewModel = viewModel()
                AppNavigation(sproutViewModel = viewModel)
            }
        }
    }
}