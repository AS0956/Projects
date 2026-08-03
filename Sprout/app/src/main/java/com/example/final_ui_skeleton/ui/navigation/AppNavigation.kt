package com.example.final_ui_skeleton.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.final_ui_skeleton.model.Spending
import com.example.final_ui_skeleton.ui.screens.AIRecommendationsScreen
import com.example.final_ui_skeleton.ui.screens.BudgetHistoryScreen
import com.example.final_ui_skeleton.ui.screens.SplashScreen
import com.example.final_ui_skeleton.ui.screens.AddExpenseScreen
import com.example.final_ui_skeleton.ui.screens.BasicInformationScreen
import com.example.final_ui_skeleton.ui.screens.BudgetSetupScreen
import com.example.final_ui_skeleton.ui.screens.BudgetSnapshotScreen
import com.example.final_ui_skeleton.ui.screens.DashboardScreen
import com.example.final_ui_skeleton.ui.screens.DemographicScreen
import com.example.final_ui_skeleton.ui.screens.PrivacyScreen
import com.example.final_ui_skeleton.ui.screens.SavingsGoalScreen
import com.example.final_ui_skeleton.ui.screens.SettingsScreen
import com.example.final_ui_skeleton.ui.screens.SignInScreen
import com.example.final_ui_skeleton.ui.screens.SpendingDetail
import com.example.final_ui_skeleton.ui.screens.SpendingHistoryScreen
import com.example.final_ui_skeleton.ui.screens.WelcomeScreen
import com.example.final_ui_skeleton.viewmodel.SproutViewModel

/**
 * 1. What: A tool used to help navigate the various screen and features of the app
 * 2. Who: Used by MainActivity
 * 3. When: During the onCreate of the app or the startup.
 */

@Composable
fun AppNavigation(sproutViewModel: SproutViewModel) {
    val navController = rememberNavController()

    // go straight to dashboard if already signed in
    val start = Splash

    NavHost(navController = navController, startDestination = start) {

        composable<Splash> {
            SplashScreen(onFinished = {
                val next = if (sproutViewModel.isLoggedIn) Dashboard else Welcome
                navController.navigate(next) {
                    popUpTo(Splash) { inclusive = true }
                }
            })
        }

        composable<Welcome> {
            WelcomeScreen(onContinue = { navController.navigate(SignIn) })
        }

        composable<SignIn> {
            SignInScreen(
                viewModel = sproutViewModel,
                onSignIn = {
                    navController.navigate(Dashboard) {
                        popUpTo(Welcome) { inclusive = true }
                    }
                },
                onCreateAccount = { navController.navigate(BasicInformation) },
                onBack = { navController.popBackStack() }
            )
        }

        composable<BasicInformation> {
            BasicInformationScreen(
                viewModel = sproutViewModel,
                onSuccess = { navController.navigate(Demographic) },
                onBack = { navController.popBackStack() }
            )
        }

        composable<Demographic> {
            DemographicScreen(
                viewModel = sproutViewModel,
                onGotchaClick = { navController.navigate(Goal) },
                onBack = { navController.popBackStack() }
            )
        }

        composable<Goal> {
            SavingsGoalScreen(
                viewModel = sproutViewModel,
                onNext = { navController.navigate(BudgetSetup) },
                onBack = { navController.popBackStack() }
            )
        }

        composable<BudgetSetup> {
            BudgetSetupScreen(
                viewModel = sproutViewModel,
                onNext = { navController.navigate(Privacy) },
                onBack = { navController.popBackStack() }
            )
        }

        composable<Privacy> {
            PrivacyScreen(
                onTakeHome = {
                    navController.navigate(Dashboard) {
                        popUpTo(Welcome) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable<Dashboard> {
            DashboardScreen(
                viewModel = sproutViewModel,
                onNavigateBudgets = { navController.navigate(BudgetSnapshot) },
                onNavigateRecommend = { navController.navigate(AiRecommendation) },
                onNavigateSettings = { navController.navigate(Setting) },
                onNavigateBudgetHistory = { navController.navigate(BudgetHistory) }
            )
        }

        composable<BudgetHistory> {
            BudgetHistoryScreen(
                viewModel = sproutViewModel,
                onBack = { navController.popBackStack() },
                onNavigateDashboard = { navController.navigate(Dashboard) },
                onNavigateBudgets = { navController.navigate(BudgetSnapshot) },
                onNavigateRecommend = { navController.navigate(AiRecommendation) },
                onNavigateSettings = { navController.navigate(Setting) }
            )
        }

        composable<BudgetSnapshot> {
            BudgetSnapshotScreen(
                viewModel = sproutViewModel,
                onNavigateDashboard = { navController.navigate(Dashboard) },
                onNavigateRecommend = { navController.navigate(AiRecommendation) },
                onNavigateSettings = { navController.navigate(Setting) },
                onNavigateAddExpense = { navController.navigate(AddExpense) },
                onNavigateSpendingDetail = { navController.navigate(SpendingDetail) },
                onNavigateBudgetHistory = { navController.navigate(BudgetHistory) }
            )
        }

        composable<SpendingHistory> {
            SpendingHistoryScreen(
                viewModel = sproutViewModel,
                onNavigateDashboard = { navController.navigate(Dashboard) },
                onNavigateRecommend = { navController.navigate(AiRecommendation) },
                onNavigateSettings = { navController.navigate(Setting) }
            )
        }

        composable<AiRecommendation> {
            AIRecommendationsScreen(
                viewModel = sproutViewModel,
                onNavigateDashboard = { navController.navigate(Dashboard) },
                onNavigateBudgets = { navController.navigate(BudgetSnapshot) },
                onNavigateSettings = { navController.navigate(Setting) }
            )
        }

        composable<Setting> {
            SettingsScreen(
                viewModel = sproutViewModel,
                onLogout = {
                    navController.navigate(Welcome) {
                        popUpTo(Dashboard) { inclusive = true }
                    }
                },
                onNavigateDashboard = { navController.navigate(Dashboard) },
                onNavigateBudgets = { navController.navigate(BudgetSnapshot) },
                onNavigateRecommend = { navController.navigate(AiRecommendation) }
            )
        }

        composable<AddExpense> {
            AddExpenseScreen(
                viewModel = sproutViewModel,
                onExpenseAdded = { navController.popBackStack() },
                onNavigateDashboard = { navController.navigate(Dashboard) },
                onNavigateBudgets = { navController.navigate(BudgetSnapshot) },
                onNavigateRecommend = { navController.navigate(AiRecommendation) },
                onNavigateSettings = { navController.navigate(Setting) },
                onBack = { navController.popBackStack() }
            )
        }

        composable<SpendingDetail> {
            val spending: Spending = sproutViewModel.selectedSpending
                ?: Spending(date = "", merchant = "", category = "", perOrShared = "", amount = 0.0, description = "")
            SpendingDetail(
                spending = spending,
                onNavigateDashboard = { navController.navigate(Dashboard) },
                onNavigateBudgets = { navController.navigate(BudgetSnapshot) },
                onNavigateRecommend = { navController.navigate(AiRecommendation) },
                onNavigateSettings = { navController.navigate(Setting) },
                onBack = { navController.popBackStack() },
                onDelete = {
                    sproutViewModel.selectedSpendingId?.let { sproutViewModel.deleteExpense(it) }
                    navController.popBackStack()
                },
                onEdit = { updated ->
                    sproutViewModel.selectedSpendingId?.let { docId ->
                        sproutViewModel.updateExpense(docId, updated)
                        sproutViewModel.selectSpending(updated, docId)
                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun PreviewAppNavigation() {
    AppNavigation(viewModel())
}
