package com.example.passworddice.data.model

import androidx.compose.ui.graphics.Color
import kotlin.math.ln
import kotlin.math.log2

/**
 * Representation of password strength based on Shannon entropy.
 *
 * Entropy formula:
 *   E = L * log2(R)
 * where L = password length, R = character pool size.
 *
 * Scale classification:
 * - < 36 bits: Very Weak (vulnerable to fast automated attacks)
 * - 36 to 59.9 bits: Weak (limited resistance)
 * - 60 to 79.9 bits: Fair (moderate security for standard accounts)
 * - 80 to 119.9 bits: Strong (high resistance against modern brute-force)
 * - 120+ bits: Very Strong (very high cryptographic margin)
 *
 * Note: All strength ratings are mathematical estimates assuming uniform random selection.
 * No password can be guaranteed unbreakable.
 */
enum class PasswordStrength(
    val label: String,
    val score: Int, // 1 to 5
    val fraction: Float, // 0.2 to 1.0
    val color: Color
) {
    VERY_WEAK(
        label = "Very Weak",
        score = 1,
        fraction = 0.2f,
        color = Color(0xFFE53935) // Red
    ),
    WEAK(
        label = "Weak",
        score = 2,
        fraction = 0.4f,
        color = Color(0xFFFB8C00) // Orange
    ),
    FAIR(
        label = "Fair",
        score = 3,
        fraction = 0.6f,
        color = Color(0xFFFDD835) // Amber
    ),
    STRONG(
        label = "Strong",
        score = 4,
        fraction = 0.8f,
        color = Color(0xFF43A047) // Green
    ),
    VERY_STRONG(
        label = "Very Strong",
        score = 5,
        fraction = 1.0f,
        color = Color(0xFF00897B) // Teal/Emerald
    );

    companion object {
        /**
         * Calculates Shannon entropy in bits: E = length * log2(poolSize).
         */
        fun calculateEntropy(length: Int, poolSize: Int): Double {
            if (length <= 0 || poolSize <= 1) return 0.0
            return length * log2(poolSize.toDouble())
        }

        /**
         * Evaluates strength based on estimated entropy bits.
         */
        fun fromEntropy(entropy: Double): PasswordStrength {
            return when {
                entropy < 36.0 -> VERY_WEAK
                entropy < 60.0 -> WEAK
                entropy < 80.0 -> FAIR
                entropy < 120.0 -> STRONG
                else -> VERY_STRONG
            }
        }
    }
}
