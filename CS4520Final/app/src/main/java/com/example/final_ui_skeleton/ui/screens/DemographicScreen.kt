package com.example.final_ui_skeleton.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.final_ui_skeleton.ui.components.AppColors
import com.example.final_ui_skeleton.ui.components.PrimaryButton
import com.example.final_ui_skeleton.ui.components.ScreenHeading
import com.example.final_ui_skeleton.viewmodel.SproutViewModel


fun getSpendingAverages(incomeBracket: String): Map<String, String> {
    return when (incomeBracket) {
        "Under $25,000" -> mapOf("Housing/Rent" to "$650", "Groceries and Essentials" to "$280", "Dining and Extras" to "$150", "Miscellaneous" to "$120")
        "$25,000 - $50,000" -> mapOf("Housing/Rent" to "$950", "Groceries and Essentials" to "$380", "Dining and Extras" to "$220", "Miscellaneous" to "$180")
        "$50,000 - $75,000" -> mapOf("Housing/Rent" to "$1,300", "Groceries and Essentials" to "$480", "Dining and Extras" to "$300", "Miscellaneous" to "$250")
        "$75,000 - $100,000" -> mapOf("Housing/Rent" to "$1,700", "Groceries and Essentials" to "$580", "Dining and Extras" to "$400", "Miscellaneous" to "$350")
        "$100,000 - $150,000" -> mapOf("Housing/Rent" to "$2,200", "Groceries and Essentials" to "$700", "Dining and Extras" to "$550", "Miscellaneous" to "$480")
        "Over $150,000" -> mapOf("Housing/Rent" to "$3,000", "Groceries and Essentials" to "$900", "Dining and Extras" to "$800", "Miscellaneous" to "$700")
        else -> mapOf("Housing/Rent" to "$1,200", "Groceries and Essentials" to "$450", "Dining and Extras" to "$300", "Miscellaneous" to "$250")
    }
}

/**
 * 1. What: Shows average spending breakdown by income bracket during onboarding.
 * 2. Who: Called by the NavHost after BasicInformationScreen.
 * 3. When: Executed after the user submits their basic information.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemographicScreen(
    viewModel: SproutViewModel = viewModel(),
    onGotchaClick: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val incomeBracket = userProfile?.incomeBracket ?: ""
    val averages = getSpendingAverages(incomeBracket)

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
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            ScreenHeading(text = "Here's how\npeople like you\nspend:", modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Based on your demographic, here's a typical monthly breakdown.", fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())

            if (incomeBracket.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(color = AppColors.Primary.copy(alpha = 0.15f), shape = RoundedCornerShape(20.dp)) {
                    Text(text = "Income: $incomeBracket", color = AppColors.Primary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            averages.forEach { (label, amount) ->
                SpendingAverageRow(label = label, amount = amount)
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.weight(1f))
            Text(text = "These are averages and your Sprout plan will be built around you. Don't worry if your current spending is over this - we all splurge!", fontSize = 13.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, fontFamily = FontFamily.Serif, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(24.dp))
            PrimaryButton(text = "Gotcha", onClick = onGotchaClick, modifier = Modifier.width(160.dp))
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DemographicScreenPreview() {
    DemographicScreen()
}



/**
 * 1. What: Renders a single labeled row showing a spending category and its average dollar amount in a rounded surface.
 * 2. Who: Called by DemographicScreen.
 * 3. When: Executed for each entry while the demographic screen builds its list of spending averages.
 */
@Composable
fun SpendingAverageRow(label: String, amount: String) {
    Surface(color = Color(0xFFDDDDE6), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = label, fontSize = 15.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Serif)
            Text(text = amount, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AppColors.Primary)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SpendingAverageRowPreview() {
    SpendingAverageRow(
        label = "Housing/Rent",
        amount = "$1,200"
    )
}
