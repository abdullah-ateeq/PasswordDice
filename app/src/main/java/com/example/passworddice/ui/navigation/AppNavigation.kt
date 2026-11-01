package com.example.passworddice.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.passworddice.ui.about.AboutScreen
import com.example.passworddice.ui.home.HomeScreen
import com.example.passworddice.ui.home.HomeViewModel
import com.example.passworddice.ui.settings.SettingsScreen
import com.example.passworddice.ui.settings.SettingsViewModel
import com.example.passworddice.ui.splash.SplashScreen

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    homeViewModel: HomeViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        // Screen 1: Splash
        composable(
            route = Screen.Splash.route,
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Screen 2: Home
        composable(
            route = Screen.Home.route,
            enterTransition = { fadeIn(animationSpec = tween(300)) }
        ) {
            val uiState by homeViewModel.uiState.collectAsState()

            HomeScreen(
                uiState = uiState,
                onLengthChanged = homeViewModel::onLengthChanged,
                onIncludeLowercaseChanged = homeViewModel::onIncludeLowercaseChanged,
                onIncludeUppercaseChanged = homeViewModel::onIncludeUppercaseChanged,
                onIncludeDigitsChanged = homeViewModel::onIncludeDigitsChanged,
                onIncludeSpecialChanged = homeViewModel::onIncludeSpecialChanged,
                onExcludeAmbiguousChanged = homeViewModel::onExcludeAmbiguousChanged,
                onRequireEachSelectedSetChanged = homeViewModel::onRequireEachSelectedSetChanged,
                onBatchSizeChanged = homeViewModel::onBatchSizeChanged,
                onSelectAllCharsets = homeViewModel::onSelectAllCharsets,
                onClearAllCharsets = homeViewModel::onClearAllCharsets,
                onToggleVisibility = homeViewModel::togglePasswordVisibility,
                onGenerate = homeViewModel::generate,
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToAbout = { navController.navigate(Screen.About.route) },
                onMessageShown = homeViewModel::onMessageShown
            )
        }

        // Screen 3: Settings
        composable(
            route = Screen.Settings.route,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }
        ) {
            val preferences by settingsViewModel.preferencesState.collectAsState()

            SettingsScreen(
                preferences = preferences,
                onThemeModeChanged = settingsViewModel::setThemeMode,
                onDefaultLengthChanged = settingsViewModel::setDefaultLength,
                onDefaultIncludeLowercaseChanged = settingsViewModel::setDefaultIncludeLowercase,
                onDefaultIncludeUppercaseChanged = settingsViewModel::setDefaultIncludeUppercase,
                onDefaultIncludeDigitsChanged = settingsViewModel::setDefaultIncludeDigits,
                onDefaultIncludeSpecialChanged = settingsViewModel::setDefaultIncludeSpecial,
                onDefaultExcludeAmbiguousChanged = settingsViewModel::setDefaultExcludeAmbiguous,
                onDefaultRequireEachChanged = settingsViewModel::setDefaultRequireEachSelectedSet,
                onDefaultBatchSizeChanged = settingsViewModel::setDefaultBatchSize,
                onHidePasswordsByDefaultChanged = settingsViewModel::setHidePasswordsByDefault,
                onHapticFeedbackChanged = settingsViewModel::setHapticFeedbackEnabled,
                onClearOnLeaveChanged = settingsViewModel::setClearOnLeave,
                onResetToDefaults = settingsViewModel::resetToDefaults,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Screen 4: About
        composable(
            route = Screen.About.route,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }
        ) {
            AboutScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
