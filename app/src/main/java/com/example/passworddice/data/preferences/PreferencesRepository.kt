package com.example.passworddice.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.passworddice.data.model.PasswordConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "password_dice_settings")

/**
 * Repository interface for managing persisted application preferences via DataStore.
 */
interface PreferencesRepository {
    val preferencesFlow: Flow<AppPreferences>

    suspend fun updateThemeMode(themeMode: ThemeMode)
    suspend fun updateDefaultLength(length: Int)
    suspend fun updateDefaultIncludeLowercase(enabled: Boolean)
    suspend fun updateDefaultIncludeUppercase(enabled: Boolean)
    suspend fun updateDefaultIncludeDigits(enabled: Boolean)
    suspend fun updateDefaultIncludeSpecial(enabled: Boolean)
    suspend fun updateDefaultExcludeAmbiguous(enabled: Boolean)
    suspend fun updateDefaultRequireEachSelectedSet(enabled: Boolean)
    suspend fun updateDefaultBatchSize(batchSize: Int)
    suspend fun updateHidePasswordsByDefault(hide: Boolean)
    suspend fun updateHapticFeedbackEnabled(enabled: Boolean)
    suspend fun updateClearOnLeave(clear: Boolean)
    suspend fun resetToDefaults()
}

/**
 * Concrete implementation using Android DataStore Preferences.
 */
class PreferencesRepositoryImpl(private val context: Context) : PreferencesRepository {

    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DEFAULT_LENGTH = intPreferencesKey("default_length")
        val DEFAULT_INC_LOWERCASE = booleanPreferencesKey("default_inc_lowercase")
        val DEFAULT_INC_UPPERCASE = booleanPreferencesKey("default_inc_uppercase")
        val DEFAULT_INC_DIGITS = booleanPreferencesKey("default_inc_digits")
        val DEFAULT_INC_SPECIAL = booleanPreferencesKey("default_inc_special")
        val DEFAULT_EXCLUDE_AMBIGUOUS = booleanPreferencesKey("default_exclude_ambiguous")
        val DEFAULT_REQUIRE_EACH = booleanPreferencesKey("default_require_each")
        val DEFAULT_BATCH_SIZE = intPreferencesKey("default_batch_size")
        val HIDE_PASSWORDS_DEFAULT = booleanPreferencesKey("hide_passwords_default")
        val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
        val CLEAR_ON_LEAVE = booleanPreferencesKey("clear_on_leave")
    }

    override val preferencesFlow: Flow<AppPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val themeModeStr = preferences[PreferencesKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name
            val themeMode = try {
                ThemeMode.valueOf(themeModeStr)
            } catch (e: Exception) {
                ThemeMode.SYSTEM
            }

            AppPreferences(
                themeMode = themeMode,
                defaultLength = preferences[PreferencesKeys.DEFAULT_LENGTH] ?: PasswordConfig.DEFAULT_LENGTH,
                defaultIncludeLowercase = preferences[PreferencesKeys.DEFAULT_INC_LOWERCASE] ?: true,
                defaultIncludeUppercase = preferences[PreferencesKeys.DEFAULT_INC_UPPERCASE] ?: true,
                defaultIncludeDigits = preferences[PreferencesKeys.DEFAULT_INC_DIGITS] ?: true,
                defaultIncludeSpecial = preferences[PreferencesKeys.DEFAULT_INC_SPECIAL] ?: true,
                defaultExcludeAmbiguous = preferences[PreferencesKeys.DEFAULT_EXCLUDE_AMBIGUOUS] ?: false,
                defaultRequireEachSelectedSet = preferences[PreferencesKeys.DEFAULT_REQUIRE_EACH] ?: true,
                defaultBatchSize = preferences[PreferencesKeys.DEFAULT_BATCH_SIZE] ?: PasswordConfig.DEFAULT_BATCH_SIZE,
                hidePasswordsByDefault = preferences[PreferencesKeys.HIDE_PASSWORDS_DEFAULT] ?: false,
                hapticFeedbackEnabled = preferences[PreferencesKeys.HAPTIC_FEEDBACK] ?: true,
                clearOnLeave = preferences[PreferencesKeys.CLEAR_ON_LEAVE] ?: false
            )
        }

    override suspend fun updateThemeMode(themeMode: ThemeMode) {
        context.dataStore.edit { it[PreferencesKeys.THEME_MODE] = themeMode.name }
    }

    override suspend fun updateDefaultLength(length: Int) {
        val safeLength = length.coerceIn(PasswordConfig.MIN_LENGTH, PasswordConfig.MAX_LENGTH)
        context.dataStore.edit { it[PreferencesKeys.DEFAULT_LENGTH] = safeLength }
    }

    override suspend fun updateDefaultIncludeLowercase(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.DEFAULT_INC_LOWERCASE] = enabled }
    }

    override suspend fun updateDefaultIncludeUppercase(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.DEFAULT_INC_UPPERCASE] = enabled }
    }

    override suspend fun updateDefaultIncludeDigits(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.DEFAULT_INC_DIGITS] = enabled }
    }

    override suspend fun updateDefaultIncludeSpecial(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.DEFAULT_INC_SPECIAL] = enabled }
    }

    override suspend fun updateDefaultExcludeAmbiguous(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.DEFAULT_EXCLUDE_AMBIGUOUS] = enabled }
    }

    override suspend fun updateDefaultRequireEachSelectedSet(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.DEFAULT_REQUIRE_EACH] = enabled }
    }

    override suspend fun updateDefaultBatchSize(batchSize: Int) {
        val safeBatch = batchSize.coerceIn(PasswordConfig.MIN_BATCH_SIZE, PasswordConfig.MAX_BATCH_SIZE)
        context.dataStore.edit { it[PreferencesKeys.DEFAULT_BATCH_SIZE] = safeBatch }
    }

    override suspend fun updateHidePasswordsByDefault(hide: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.HIDE_PASSWORDS_DEFAULT] = hide }
    }

    override suspend fun updateHapticFeedbackEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.HAPTIC_FEEDBACK] = enabled }
    }

    override suspend fun updateClearOnLeave(clear: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.CLEAR_ON_LEAVE] = clear }
    }

    override suspend fun resetToDefaults() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
