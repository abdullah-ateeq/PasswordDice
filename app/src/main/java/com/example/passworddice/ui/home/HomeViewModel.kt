package com.example.passworddice.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.passworddice.data.generator.SecurePasswordGenerator
import com.example.passworddice.data.model.PasswordConfig
import com.example.passworddice.data.model.PasswordResult
import com.example.passworddice.data.model.ValidationResult
import com.example.passworddice.data.preferences.AppPreferences
import com.example.passworddice.data.preferences.PreferencesRepository
import com.example.passworddice.data.preferences.PreferencesRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val config: PasswordConfig = PasswordConfig(),
    val passwords: List<PasswordResult> = emptyList(),
    val isPasswordVisible: Boolean = true,
    val validationResult: ValidationResult = ValidationResult.Valid,
    val isGenerating: Boolean = false,
    val userMessage: String? = null,
    val hapticEnabled: Boolean = true,
    val clearOnLeave: Boolean = false,
    val isInitialLoaded: Boolean = false
) {
    val primaryPassword: PasswordResult?
        get() = passwords.firstOrNull()
}

class HomeViewModel(
    application: Application,
    private val preferencesRepository: PreferencesRepository = PreferencesRepositoryImpl(application),
    private val passwordGenerator: SecurePasswordGenerator = SecurePasswordGenerator()
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Load initial preferences from DataStore
            val prefs = preferencesRepository.preferencesFlow.first()
            val initialConfig = prefs.toInitialConfig()
            val validation = passwordGenerator.validate(initialConfig)

            _uiState.update {
                it.copy(
                    config = initialConfig,
                    isPasswordVisible = !prefs.hidePasswordsByDefault,
                    validationResult = validation,
                    hapticEnabled = prefs.hapticFeedbackEnabled,
                    clearOnLeave = prefs.clearOnLeave,
                    isInitialLoaded = true
                )
            }

            // Automatically generate the first initial password batch
            if (validation.isValid) {
                generate()
            }

            // Continue observing preferences for real-time haptic & clear-on-leave updates
            preferencesRepository.preferencesFlow.collect { updatedPrefs ->
                _uiState.update { current ->
                    current.copy(
                        hapticEnabled = updatedPrefs.hapticFeedbackEnabled,
                        clearOnLeave = updatedPrefs.clearOnLeave
                    )
                }
            }
        }
    }

    fun onLengthChanged(length: Int) {
        val safeLength = length.coerceIn(PasswordConfig.MIN_LENGTH, PasswordConfig.MAX_LENGTH)
        updateConfig { it.copy(length = safeLength) }
    }

    fun onIncludeLowercaseChanged(enabled: Boolean) {
        updateConfig { it.copy(includeLowercase = enabled) }
    }

    fun onIncludeUppercaseChanged(enabled: Boolean) {
        updateConfig { it.copy(includeUppercase = enabled) }
    }

    fun onIncludeDigitsChanged(enabled: Boolean) {
        updateConfig { it.copy(includeDigits = enabled) }
    }

    fun onIncludeSpecialChanged(enabled: Boolean) {
        updateConfig { it.copy(includeSpecial = enabled) }
    }

    fun onExcludeAmbiguousChanged(exclude: Boolean) {
        updateConfig { it.copy(excludeAmbiguous = exclude) }
    }

    fun onRequireEachSelectedSetChanged(require: Boolean) {
        updateConfig { it.copy(requireEachSelectedSet = require) }
    }

    fun onBatchSizeChanged(batchSize: Int) {
        val safeBatch = batchSize.coerceIn(PasswordConfig.MIN_BATCH_SIZE, PasswordConfig.MAX_BATCH_SIZE)
        updateConfig { it.copy(batchSize = safeBatch) }
    }

    fun onSelectAllCharsets() {
        updateConfig {
            it.copy(
                includeLowercase = true,
                includeUppercase = true,
                includeDigits = true,
                includeSpecial = true
            )
        }
    }

    fun onClearAllCharsets() {
        updateConfig {
            it.copy(
                includeLowercase = false,
                includeUppercase = false,
                includeDigits = false,
                includeSpecial = false
            )
        }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun generate() {
        val currentConfig = _uiState.value.config
        val validation = passwordGenerator.validate(currentConfig)
        if (!validation.isValid) {
            _uiState.update { it.copy(validationResult = validation) }
            return
        }

        try {
            val results = passwordGenerator.generateBatch(currentConfig)
            _uiState.update {
                it.copy(
                    passwords = results,
                    validationResult = ValidationResult.Valid
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    validationResult = ValidationResult.Invalid(e.localizedMessage ?: "Generation failed")
                )
            }
        }
    }

    fun clearPasswordsIfConfigured() {
        if (_uiState.value.clearOnLeave) {
            _uiState.update { it.copy(passwords = emptyList()) }
        }
    }

    fun showMessage(message: String) {
        _uiState.update { it.copy(userMessage = message) }
    }

    fun onMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }

    private fun updateConfig(transform: (PasswordConfig) -> PasswordConfig) {
        val newConfig = transform(_uiState.value.config)
        val validation = passwordGenerator.validate(newConfig)
        _uiState.update {
            it.copy(
                config = newConfig,
                validationResult = validation
            )
        }
    }
}
