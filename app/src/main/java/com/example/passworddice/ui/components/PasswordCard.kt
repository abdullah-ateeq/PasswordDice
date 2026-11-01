package com.example.passworddice.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.passworddice.ui.theme.MonospacePasswordStyle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PasswordCard(
    password: String,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier
) {
    var justCopied by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val copyBgColor by animateColorAsState(
        targetValue = if (justCopied) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        label = "copyBgColor"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Password display area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(84.dp)
                    .horizontalScroll(scrollState),
                contentAlignment = Alignment.CenterStart
            ) {
                if (password.isEmpty()) {
                    Text(
                        text = "Press 'Roll Dice' to generate",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                } else {
                    SelectionContainer {
                        AnimatedContent(
                            targetState = isVisible,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "passwordVisibility"
                        ) { visible ->
                            val displayText = if (visible) {
                                password
                            } else {
                                "•".repeat(password.length.coerceAtMost(36))
                            }
                            Text(
                                text = displayText,
                                style = MonospacePasswordStyle,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action buttons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${password.length} chars",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Show/hide toggle button
                    IconButton(
                        onClick = onToggleVisibility,
                        enabled = password.isNotEmpty(),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        )
                    ) {
                        Icon(
                            imageVector = if (isVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (isVisible) "Hide password" else "Show password",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Copy button
                    FilledTonalIconButton(
                        onClick = {
                            if (password.isNotEmpty()) {
                                onCopy()
                                justCopied = true
                                scope.launch {
                                    delay(1800)
                                    justCopied = false
                                }
                            }
                        },
                        enabled = password.isNotEmpty(),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = copyBgColor,
                            contentColor = if (justCopied) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Icon(
                            imageVector = if (justCopied) Icons.Filled.Check else Icons.Filled.ContentCopy,
                            contentDescription = "Copy password",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
