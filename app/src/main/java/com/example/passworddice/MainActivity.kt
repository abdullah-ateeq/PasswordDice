package com.example.passworddice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.passworddice.ui.home.HomeViewModel
import com.example.passworddice.ui.navigation.AppNavigation
import com.example.passworddice.ui.settings.SettingsViewModel
import com.example.passworddice.ui.theme.PasswordDiceTheme

class MainActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val preferences by settingsViewModel.preferencesState.collectAsState()

            PasswordDiceTheme(themeMode = preferences.themeMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(
                        homeViewModel = homeViewModel,
                        settingsViewModel = settingsViewModel
                    )
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        // If the user enabled "Clear passwords when leaving the app", wipe passwords from memory
        homeViewModel.clearPasswordsIfConfigured()
    }
}
