package com.example.passworddice.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.passworddice.data.model.PasswordConfig
import com.example.passworddice.data.model.PasswordResult
import com.example.passworddice.data.model.PasswordStrength
import com.example.passworddice.data.model.ValidationResult
import com.example.passworddice.ui.components.DiceIcon
import com.example.passworddice.ui.components.FeedbackUtils
import com.example.passworddice.ui.components.PasswordCard
import com.example.passworddice.ui.components.StrengthMeter
import com.example.passworddice.ui.theme.MonospacePasswordSmallStyle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onLengthChanged: (Int) -> Unit,
    onIncludeLowercaseChanged: (Boolean) -> Unit,
    onIncludeUppercaseChanged: (Boolean) -> Unit,
    onIncludeDigitsChanged: (Boolean) -> Unit,
    onIncludeSpecialChanged: (Boolean) -> Unit,
    onExcludeAmbiguousChanged: (Boolean) -> Unit,
    onRequireEachSelectedSetChanged: (Boolean) -> Unit,
    onBatchSizeChanged: (Int) -> Unit,
    onSelectAllCharsets: () -> Unit,
    onClearAllCharsets: () -> Unit,
    onToggleVisibility: () -> Unit,
    onGenerate: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onMessageShown: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var diceRotation by remember { mutableFloatStateOf(0f) }
    val animatedRotation by animateFloatAsState(
        targetValue = diceRotation,
        animationSpec = tween(durationMillis = 400),
        label = "diceRoll"
    )

    // Handle incoming user messages (e.g., from clipboard copies)
    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            onMessageShown()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DiceIcon(size = 28.dp, tint = MaterialTheme.colorScheme.primary)
                        Column {
                            Text(
                                text = "PasswordDice",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Cryptographically Strong",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onNavigateToAbout) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "About",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
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
            // Validation Warning Alert (if any)
            if (uiState.validationResult is ValidationResult.Invalid) {
                item {
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = "Warning",
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = (uiState.validationResult as ValidationResult.Invalid).reason,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }

            // Primary Password Card
            item {
                val primary = uiState.primaryPassword
                val passwordText = primary?.value ?: ""

                PasswordCard(
                    password = passwordText,
                    isVisible = uiState.isPasswordVisible,
                    onToggleVisibility = {
                        FeedbackUtils.triggerHaptic(context, uiState.hapticEnabled)
                        onToggleVisibility()
                    },
                    onCopy = {
                        FeedbackUtils.copyToClipboard(context, "Password", passwordText)
                        FeedbackUtils.triggerHaptic(context, uiState.hapticEnabled)
                        scope.launch {
                            snackbarHostState.showSnackbar("Password copied to clipboard")
                        }
                    }
                )
            }

            // Strength Meter (Entropy & Pool breakdown)
            item {
                val primary = uiState.primaryPassword
                val entropy = primary?.entropy ?: PasswordStrength.calculateEntropy(
                    uiState.config.length,
                    uiState.config.poolSize
                )
                val strength = primary?.strength ?: PasswordStrength.fromEntropy(entropy)

                StrengthMeter(
                    strength = strength,
                    entropy = entropy,
                    poolSize = uiState.config.poolSize,
                    length = uiState.config.length
                )
            }

            // Generate Button
            item {
                Button(
                    onClick = {
                        diceRotation += 360f
                        FeedbackUtils.triggerHaptic(context, uiState.hapticEnabled)
                        onGenerate()
                    },
                    enabled = uiState.validationResult.isValid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DiceIcon(
                            size = 22.dp,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            pipColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.rotate(animatedRotation)
                        )
                        Text(
                            text = if (uiState.config.batchSize > 1) {
                                "Roll Dice (Generate ${uiState.config.batchSize} Passwords)"
                            } else {
                                "Roll Dice (Generate Password)"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Configuration Section: Password Length
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Password Length",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Decrement button
                                FilledTonalIconButton(
                                    onClick = {
                                        if (uiState.config.length > PasswordConfig.MIN_LENGTH) {
                                            FeedbackUtils.triggerHaptic(context, uiState.hapticEnabled)
                                            onLengthChanged(uiState.config.length - 1)
                                        }
                                    },
                                    enabled = uiState.config.length > PasswordConfig.MIN_LENGTH,
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(Icons.Filled.Remove, contentDescription = "Decrease length")
                                }

                                Text(
                                    text = "${uiState.config.length}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.width(42.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )

                                // Increment button
                                FilledTonalIconButton(
                                    onClick = {
                                        if (uiState.config.length < PasswordConfig.MAX_LENGTH) {
                                            FeedbackUtils.triggerHaptic(context, uiState.hapticEnabled)
                                            onLengthChanged(uiState.config.length + 1)
                                        }
                                    },
                                    enabled = uiState.config.length < PasswordConfig.MAX_LENGTH,
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(Icons.Filled.Add, contentDescription = "Increase length")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Slider(
                            value = uiState.config.length.toFloat(),
                            onValueChange = {
                                onLengthChanged(it.toInt())
                            },
                            valueRange = PasswordConfig.MIN_LENGTH.toFloat()..PasswordConfig.MAX_LENGTH.toFloat(),
                            steps = PasswordConfig.MAX_LENGTH - PasswordConfig.MIN_LENGTH - 1,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Min (${PasswordConfig.MIN_LENGTH})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Max (${PasswordConfig.MAX_LENGTH})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Configuration Section: Character Sets
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Character Sets",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )

                            Row {
                                TextButton(onClick = {
                                    FeedbackUtils.triggerHaptic(context, uiState.hapticEnabled)
                                    onSelectAllCharsets()
                                }) {
                                    Text("Select All", style = MaterialTheme.typography.labelMedium)
                                }
                                TextButton(onClick = {
                                    FeedbackUtils.triggerHaptic(context, uiState.hapticEnabled)
                                    onClearAllCharsets()
                                }) {
                                    Text("Clear", style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Lowercase
                        CharsetRow(
                            title = "Lowercase letters",
                            subtitle = if (uiState.config.excludeAmbiguous) "a-z (excluding 'l')" else "a-z",
                            checked = uiState.config.includeLowercase,
                            onCheckedChange = {
                                FeedbackUtils.triggerHaptic(context, uiState.hapticEnabled)
                                onIncludeLowercaseChanged(it)
                            }
                        )

                        // Uppercase
                        CharsetRow(
                            title = "Uppercase letters",
                            subtitle = if (uiState.config.excludeAmbiguous) "A-Z (excluding 'I', 'O')" else "A-Z",
                            checked = uiState.config.includeUppercase,
                            onCheckedChange = {
                                FeedbackUtils.triggerHaptic(context, uiState.hapticEnabled)
                                onIncludeUppercaseChanged(it)
                            }
                        )

                        // Numbers
                        CharsetRow(
                            title = "Numbers",
                            subtitle = if (uiState.config.excludeAmbiguous) "0-9 (excluding '0', '1')" else "0-9",
                            checked = uiState.config.includeDigits,
                            onCheckedChange = {
                                FeedbackUtils.triggerHaptic(context, uiState.hapticEnabled)
                                onIncludeDigitsChanged(it)
                            }
                        )

                        // Special characters
                        CharsetRow(
                            title = "Special characters",
                            subtitle = "! @ # $ % & * ...",
                            checked = uiState.config.includeSpecial,
                            onCheckedChange = {
                                FeedbackUtils.triggerHaptic(context, uiState.hapticEnabled)
                                onIncludeSpecialChanged(it)
                            }
                        )
                    }
                }
            }

            // Configuration Section: Security Rules
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Security Rules",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Ambiguous characters
                        ToggleOptionRow(
                            title = "Exclude ambiguous characters",
                            subtitle = "Avoids confusing characters: 0, O, 1, I, l",
                            checked = uiState.config.excludeAmbiguous,
                            onCheckedChange = {
                                FeedbackUtils.triggerHaptic(context, uiState.hapticEnabled)
                                onExcludeAmbiguousChanged(it)
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Require at least one character from each set
                        ToggleOptionRow(
                            title = "Require each selected set",
                            subtitle = "Guarantees at least 1 character from every active set",
                            checked = uiState.config.requireEachSelectedSet,
                            onCheckedChange = {
                                FeedbackUtils.triggerHaptic(context, uiState.hapticEnabled)
                                onRequireEachSelectedSetChanged(it)
                            }
                        )
                    }
                }
            }

            // Configuration Section: Batch Generation
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Batch Generation",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${uiState.config.batchSize} ${if (uiState.config.batchSize == 1) "password" else "passwords"}",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Slider(
                            value = uiState.config.batchSize.toFloat(),
                            onValueChange = {
                                FeedbackUtils.triggerHaptic(context, uiState.hapticEnabled)
                                onBatchSizeChanged(it.toInt())
                            },
                            valueRange = 1f..10f,
                            steps = 8,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }

            // Multiple Passwords Batch Results List
            if (uiState.passwords.size > 1) {
                item {
                    Text(
                        text = "Generated Batch (${uiState.passwords.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                itemsIndexed(uiState.passwords) { index, item ->
                    BatchPasswordItem(
                        index = index + 1,
                        result = item,
                        isVisible = uiState.isPasswordVisible,
                        hapticEnabled = uiState.hapticEnabled,
                        onCopy = {
                            FeedbackUtils.copyToClipboard(context, "Password #${index + 1}", item.value)
                            FeedbackUtils.triggerHaptic(context, uiState.hapticEnabled)
                            scope.launch {
                                snackbarHostState.showSnackbar("Password #${index + 1} copied")
                            }
                        }
                    )
                }
            }

            // Bottom space for scrolling comfort
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun CharsetRow(
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
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
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
    }
}

@Composable
private fun ToggleOptionRow(
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
            .padding(vertical = 4.dp),
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

@Composable
private fun BatchPasswordItem(
    index: Int,
    result: PasswordResult,
    isVisible: Boolean,
    hapticEnabled: Boolean,
    onCopy: () -> Unit
) {
    var copied by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "#$index",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = result.strength.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = result.strength.color
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isVisible) result.value else "•".repeat(result.value.length.coerceAtMost(32)),
                    style = MonospacePasswordSmallStyle,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            IconButton(
                onClick = {
                    onCopy()
                    copied = true
                    scope.launch {
                        delay(1500)
                        copied = false
                    }
                }
            ) {
                Icon(
                    imageVector = if (copied) Icons.Filled.Check else Icons.Filled.ContentCopy,
                    contentDescription = "Copy password #$index",
                    tint = if (copied) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
