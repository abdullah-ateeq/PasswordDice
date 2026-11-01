package com.example.passworddice.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.passworddice.data.model.PasswordConfig
import com.example.passworddice.data.preferences.AppPreferences
import com.example.passworddice.data.preferences.ThemeMode
import com.example.passworddice.ui.components.FeedbackUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    preferences: AppPreferences,
    onThemeModeChanged: (ThemeMode) -> Unit,
    onDefaultLengthChanged: (Int) -> Unit,
    onDefaultIncludeLowercaseChanged: (Boolean) -> Unit,
    onDefaultIncludeUppercaseChanged: (Boolean) -> Unit,
    onDefaultIncludeDigitsChanged: (Boolean) -> Unit,
    onDefaultIncludeSpecialChanged: (Boolean) -> Unit,
    onDefaultExcludeAmbiguousChanged: (Boolean) -> Unit,
    onDefaultRequireEachChanged: (Boolean) -> Unit,
    onDefaultBatchSizeChanged: (Int) -> Unit,
    onHidePasswordsByDefaultChanged: (Boolean) -> Unit,
    onHapticFeedbackChanged: (Boolean) -> Unit,
    onClearOnLeaveChanged: (Boolean) -> Unit,
    onResetToDefaults: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showResetDialog by remember { mutableStateOf(false) }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset All Settings?") },
            text = { Text("This will restore all configuration and display preferences to their factory defaults.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        FeedbackUtils.triggerHaptic(context, preferences.hapticFeedbackEnabled)
                        onResetToDefaults()
                        showResetDialog = false
                    }
                ) {
                    Text("Reset", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section: Appearance / Theme
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Appearance",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ThemeChip(
                                label = "System",
                                icon = Icons.Filled.BrightnessAuto,
                                selected = preferences.themeMode == ThemeMode.SYSTEM,
                                onClick = {
                                    FeedbackUtils.triggerHaptic(context, preferences.hapticFeedbackEnabled)
                                    onThemeModeChanged(ThemeMode.SYSTEM)
                                },
                                modifier = Modifier.weight(1f)
                            )
                            ThemeChip(
                                label = "Light",
                                icon = Icons.Filled.LightMode,
                                selected = preferences.themeMode == ThemeMode.LIGHT,
                                onClick = {
                                    FeedbackUtils.triggerHaptic(context, preferences.hapticFeedbackEnabled)
                                    onThemeModeChanged(ThemeMode.LIGHT)
                                },
                                modifier = Modifier.weight(1f)
                            )
                            ThemeChip(
                                label = "Dark",
                                icon = Icons.Filled.DarkMode,
                                selected = preferences.themeMode == ThemeMode.DARK,
                                onClick = {
                                    FeedbackUtils.triggerHaptic(context, preferences.hapticFeedbackEnabled)
                                    onThemeModeChanged(ThemeMode.DARK)
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Section: Default Password Parameters
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Default Generator Settings",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Default Length
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Default Length",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${preferences.defaultLength} chars",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Slider(
                            value = preferences.defaultLength.toFloat(),
                            onValueChange = {
                                onDefaultLengthChanged(it.toInt())
                            },
                            valueRange = PasswordConfig.MIN_LENGTH.toFloat()..PasswordConfig.MAX_LENGTH.toFloat(),
                            steps = PasswordConfig.MAX_LENGTH - PasswordConfig.MIN_LENGTH - 1,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Default Batch Size
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Default Batch Size",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${preferences.defaultBatchSize}",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Slider(
                            value = preferences.defaultBatchSize.toFloat(),
                            onValueChange = {
                                onDefaultBatchSizeChanged(it.toInt())
                            },
                            valueRange = 1f..10f,
                            steps = 8,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Default Character Sets",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        SettingsCheckboxRow(
                            title = "Lowercase (a-z)",
                            checked = preferences.defaultIncludeLowercase,
                            onCheckedChange = onDefaultIncludeLowercaseChanged
                        )
                        SettingsCheckboxRow(
                            title = "Uppercase (A-Z)",
                            checked = preferences.defaultIncludeUppercase,
                            onCheckedChange = onDefaultIncludeUppercaseChanged
                        )
                        SettingsCheckboxRow(
                            title = "Numbers (0-9)",
                            checked = preferences.defaultIncludeDigits,
                            onCheckedChange = onDefaultIncludeDigitsChanged
                        )
                        SettingsCheckboxRow(
                            title = "Special characters (!@#$...)",
                            checked = preferences.defaultIncludeSpecial,
                            onCheckedChange = onDefaultIncludeSpecialChanged
                        )
                    }
                }
            }

            // Section: Security & Privacy Behavior
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Security & Behavior",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        SettingsSwitchRow(
                            title = "Exclude ambiguous characters by default",
                            subtitle = "Omits 0, O, 1, I, l from initial generation",
                            checked = preferences.defaultExcludeAmbiguous,
                            onCheckedChange = onDefaultExcludeAmbiguousChanged
                        )

                        SettingsSwitchRow(
                            title = "Require every selected set by default",
                            subtitle = "Enforces representation of all active sets",
                            checked = preferences.defaultRequireEachSelectedSet,
                            onCheckedChange = onDefaultRequireEachChanged
                        )

                        SettingsSwitchRow(
                            title = "Hide generated passwords by default",
                            subtitle = "Masks passwords until toggle is tapped",
                            checked = preferences.hidePasswordsByDefault,
                            onCheckedChange = onHidePasswordsByDefaultChanged
                        )

                        SettingsSwitchRow(
                            title = "Haptic feedback",
                            subtitle = "Tactile confirmation on taps and copies",
                            checked = preferences.hapticFeedbackEnabled,
                            onCheckedChange = onHapticFeedbackChanged
                        )

                        SettingsSwitchRow(
                            title = "Clear passwords when leaving app",
                            subtitle = "Wipes generated passwords from memory on app switch or exit",
                            checked = preferences.clearOnLeave,
                            onCheckedChange = onClearOnLeaveChanged
                        )
                    }
                }
            }

            // Section: Reset
            item {
                OutlinedButton(
                    onClick = { showResetDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.RestartAlt,
                        contentDescription = "Reset",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reset All Settings to Defaults")
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ThemeChip(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = modifier
    )
}

@Composable
private fun SettingsCheckboxRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}
