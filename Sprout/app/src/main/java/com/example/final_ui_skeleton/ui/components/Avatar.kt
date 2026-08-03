package com.example.final_ui_skeleton.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.final_ui_skeleton.R

/**
 * 1. What: Displays a circular avatar image for the user profile.
 * 2. Who: Called by SettingsScreen.
 * 3. When: Executed when the settings screen is composed.
 */
// Used to show the user avatar
@Composable
fun Avatar(size:Int, modifier: Modifier = Modifier,) {
    Box(modifier = Modifier.size(size.dp)) {

        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Avatar Box",
            modifier = Modifier.fillMaxSize().clip(CircleShape),
            contentScale = ContentScale.Crop
        )

    }
}

@Preview
@Composable
fun PreviewAvatar() {
    Avatar(30)
}