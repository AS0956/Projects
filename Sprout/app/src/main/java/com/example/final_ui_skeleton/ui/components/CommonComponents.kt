package com.example.final_ui_skeleton.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Shared colors used by every screen so the look stays consistent.
 */
object AppColors {
    val Background = Color(0xFFC6CBF0)
    val Primary = Color(0xFF6C5DD3)
    val Card = Color(0xFFDDDDE6)
    val Track = Color(0xFFCFCFE0)
}

/**
 * 1. What: Displays a large serif heading at the top of a screen.
 * 2. Who: Called by onboarding and main screens that need a title.
 * 3. When: Executed when the screen is first composed.
 */
@Composable
fun ScreenHeading(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}


@Preview(showBackground = true)
@Composable
fun ScreenHeadingPreview() {
    ScreenHeading("Preview")
}


/**
 * 1. What: Renders a purple pill-shaped button used as the primary action on each screen.
 * 2. Who: Called by any screen that needs a main action button.
 * 3. When: Executed when the screen is composed and the user taps it.
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
        modifier = modifier.height(48.dp)
    ) {
        Text(text, color = Color.White, fontWeight = FontWeight.SemiBold)
    }
}

@Preview(showBackground = true)
@Composable
fun PrimaryButtonPreview() {
    PrimaryButton("Sign in", onClick = {})
}

/**
 * 1. What: Renders a labeled outlined text field for user input like email or password.
 * 2. Who: Called by SignInScreen and BasicInformationScreen.
 * 3. When: Executed when the screen is composed and the user types into it.
 */
@Composable
fun LabeledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false,
    leadingIcon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        leadingIcon = if (leadingIcon != null) {
            { Icon(leadingIcon, contentDescription = null) }
        } else null,
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation()
        else VisualTransformation.None,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
fun LabeledTextFieldPreview() {
    LabeledTextField("", {}, "Email address", leadingIcon = Icons.Filled.Email)
}

/**
 * 1. What: Renders a social sign-in button for Google or Apple login.
 * 2. Who: Called by SignInScreen.
 * 3. When: Executed when the sign in screen is composed.
 */
@Composable
fun SocialSignInButton(
    text: String,
    container: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            contentColor = contentColor
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Text(text, fontWeight = FontWeight.Medium)
    }
}

@Preview(showBackground = true)
@Composable
fun SocialSignInButtonPreview() {
    SocialSignInButton("Sign in with Google", Color.White, Color.Black, onClick = {})
}

/**
 * 1. What: Renders a rounded gray card container used to group budget information.
 * 2. Who: Called by DashboardScreen, BudgetSnapshotScreen, and SpendingHistoryScreen.
 * 3. When: Executed when the parent screen is composed.
 */
@Composable
fun SectionCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        color = AppColors.Card,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp), content = content)
    }
}

@Preview(showBackground = true)
@Composable
fun SectionCardPreview() {
    SectionCard {
        BudgetProgressRow("Groceries: 66%", 0.66f, "$150", "$250")
    }
}

/**
 * 1. What: Renders a labeled progress bar row showing budget category progress.
 * 2. Who: Called by DashboardScreen and BudgetSnapshotScreen.
 * 3. When: Executed when the parent screen is composed with expense data.
 */
@Composable
fun BudgetProgressRow(
    label: String,
    progress: Float,
    leftAmount: String = "",
    rightAmount: String = "",
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(label, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            color = AppColors.Primary,
            trackColor = AppColors.Track,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )
        if (leftAmount.isNotEmpty() || rightAmount.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(leftAmount, fontSize = 12.sp)
                Text(rightAmount, fontSize = 12.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BudgetProgressRowPreview() {
    BudgetProgressRow("Groceries: 66%", 0.66f, "$150", "$250")
}

/**
 * 1. What: Fills the screen with the lavender app background color.
 * 2. Who: Called by screens that need the background applied manually.
 * 3. When: Executed when the parent screen is composed.
 */
@Composable
fun AppBackground(content: @Composable () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) { content() }
}

@Preview(showBackground = true)
@Composable
fun CommonComponentsPreview() {
    AppBackground {
        Column(Modifier.padding(24.dp)) {
            ScreenHeading("Preview")
            Spacer(Modifier.height(16.dp))
            LabeledTextField("", {}, "Email address", leadingIcon = Icons.Filled.Email)
            Spacer(Modifier.height(12.dp))
            PrimaryButton("Sign in", onClick = {})
            Spacer(Modifier.height(12.dp))
            SocialSignInButton("Sign in with Google", Color.White, Color.Black, onClick = {})
            Spacer(Modifier.height(12.dp))
            SectionCard {
                BudgetProgressRow("Groceries: 66%", 0.66f, "$150", "$250")
            }
        }
    }
}