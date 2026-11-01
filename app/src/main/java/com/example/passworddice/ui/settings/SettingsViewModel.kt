package com.example.passworddice.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.passworddice.data.preferences.AppPreferences
import com.example.passworddice.data.preferences.PreferencesRepository
import com.example.passworddice.data.preferences.PreferencesRepositoryImpl
import com.example.passworddice.data.preferences.ThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    application: Application,
    private val preferencesRepository: PreferencesRepository = PreferencesRepositoryImpl(application)
) : AndroidViewModel(application) {

    val preferencesState: StateFlow<AppPreferences> = preferencesRepository.preferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppPreferences()
        )

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            preferencesRepository.updateThemeMode(mode)
        }
    }

    fun setDefaultLength(length: Int) {
        viewModelScope.launch {
            preferencesRepository.updateDefaultLength(length)
        }
    }

    fun setDefaultIncludeLowercase(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateDefaultIncludeLowercase(enabled)
        }
    }

    fun setDefaultIncludeUppercase(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateDefaultIncludeUppercase(enabled)
        }
    }

    fun setDefaultIncludeDigits(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateDefaultIncludeDigits(enabled)
        }
    }

    fun setDefaultIncludeSpecial(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateDefaultIncludeSpecial(enabled)
        }
    }

    fun setDefaultExcludeAmbiguous(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateDefaultExcludeAmbiguous(enabled)
        }
    }

    fun setDefaultRequireEachSelectedSet(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateDefaultRequireEachSelectedSet(enabled)
        }
    }

    fun setDefaultBatchSize(batchSize: Int) {
        viewModelScope.launch {
            preferencesRepository.updateDefaultBatchSize(batchSize)
        }
    }

    fun setHidePasswordsByDefault(hide: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateHidePasswordsByDefault(hide)
        }
    }

    fun setHapticFeedbackEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateHapticFeedbackEnabled(enabled)
        }
    }

    fun setClearOnLeave(clear: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateClearOnLeave(clear)
        }
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            preferencesRepository.resetToDefaults()
        }
    }
}
