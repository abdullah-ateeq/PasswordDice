package com.example.passworddice.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A crisp, polished 2D dice icon drawn with Compose Canvas.
 */
@Composable
fun DiceIcon(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = MaterialTheme.colorScheme.primary,
    pipColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val corner = w * 0.22f

        // Dice Body
        drawRoundRect(
            color = tint,
            topLeft = Offset(0f, 0f),
            size = Size(w, h),
            cornerRadius = CornerRadius(corner, corner)
        )

        // Dice 5-pip pattern (classic high-entropy face)
        val pipRadius = w * 0.08f
        val left = w * 0.28f
        val center = w * 0.5f
        val right = w * 0.72f
        val top = h * 0.28f
        val middle = h * 0.5f
        val bottom = h * 0.72f

        // Top-left
        drawCircle(color = pipColor, radius = pipRadius, center = Offset(left, top))
        // Top-right
        drawCircle(color = pipColor, radius = pipRadius, center = Offset(right, top))
        // Center
        drawCircle(color = pipColor, radius = pipRadius, center = Offset(center, middle))
        // Bottom-left
        drawCircle(color = pipColor, radius = pipRadius, center = Offset(left, bottom))
        // Bottom-right
        drawCircle(color = pipColor, radius = pipRadius, center = Offset(right, bottom))
    }
}
