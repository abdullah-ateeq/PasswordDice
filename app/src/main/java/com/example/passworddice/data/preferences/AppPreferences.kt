package com.example.passworddice.data.preferences

import com.example.passworddice.data.model.PasswordConfig

/**
 * Persisted application settings.
 * Sensitive data such as generated passwords are NEVER stored.
 */
data class AppPreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val defaultLength: Int = PasswordConfig.DEFAULT_LENGTH,
    val defaultIncludeLowercase: Boolean = true,
    val defaultIncludeUppercase: Boolean = true,
    val defaultIncludeDigits: Boolean = true,
    val defaultIncludeSpecial: Boolean = true,
    val defaultExcludeAmbiguous: Boolean = false,
    val defaultRequireEachSelectedSet: Boolean = true,
    val defaultBatchSize: Int = PasswordConfig.DEFAULT_BATCH_SIZE,
    val hidePasswordsByDefault: Boolean = false,
    val hapticFeedbackEnabled: Boolean = true,
    val clearOnLeave: Boolean = false
) {
    /**
     * Converts current default preferences into a fresh [PasswordConfig].
     */
    fun toInitialConfig(): PasswordConfig {
        return PasswordConfig(
            length = defaultLength,
            includeLowercase = defaultIncludeLowercase,
            includeUppercase = defaultIncludeUppercase,
            includeDigits = defaultIncludeDigits,
            includeSpecial = defaultIncludeSpecial,
            excludeAmbiguous = defaultExcludeAmbiguous,
            requireEachSelectedSet = defaultRequireEachSelectedSet,
            batchSize = defaultBatchSize
        )
    }
}
