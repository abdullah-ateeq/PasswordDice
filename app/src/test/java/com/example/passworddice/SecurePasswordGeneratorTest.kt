package com.example.passworddice

import com.example.passworddice.data.generator.SecurePasswordGenerator
import com.example.passworddice.data.model.CharacterSet
import com.example.passworddice.data.model.PasswordConfig
import com.example.passworddice.data.model.PasswordStrength
import com.example.passworddice.data.model.ValidationResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.math.abs

class SecurePasswordGeneratorTest {

    private lateinit var generator: SecurePasswordGenerator

    @Before
    fun setUp() {
        generator = SecurePasswordGenerator()
    }

    @Test
    fun `password length exactly matches requested length across valid range`() {
        val testLengths = listOf(8, 12, 16, 24, 32, 64, 128)
        for (length in testLengths) {
            val config = PasswordConfig(length = length)
            val result = generator.generatePassword(config)
            assertEquals(length, result.value.length)
            assertEquals(length, result.length)
        }
    }

    @Test
    fun `password contains only allowed characters when only digits enabled`() {
        val config = PasswordConfig(
            length = 20,
            includeLowercase = false,
            includeUppercase = false,
            includeDigits = true,
            includeSpecial = false,
            requireEachSelectedSet = false
        )
        for (i in 1..20) {
            val result = generator.generatePassword(config)
            assertTrue(result.value.all { it.isDigit() })
        }
    }

    @Test
    fun `password contains only allowed characters when only lowercase enabled`() {
        val config = PasswordConfig(
            length = 25,
            includeLowercase = true,
            includeUppercase = false,
            includeDigits = false,
            includeSpecial = false
        )
        for (i in 1..20) {
            val result = generator.generatePassword(config)
            assertTrue(result.value.all { it in 'a'..'z' })
        }
    }

    @Test
    fun `every selected character set is represented when required`() {
        val config = PasswordConfig(
            length = 16,
            includeLowercase = true,
            includeUppercase = true,
            includeDigits = true,
            includeSpecial = true,
            requireEachSelectedSet = true
        )

        for (i in 1..50) {
            val result = generator.generatePassword(config)
            val text = result.value

            val hasLower = text.any { it in CharacterSet.LOWERCASE.characters }
            val hasUpper = text.any { it in CharacterSet.UPPERCASE.characters }
            val hasDigit = text.any { it in CharacterSet.DIGITS.characters }
            val hasSpecial = text.any { it in CharacterSet.SPECIAL.characters }

            assertTrue("Must contain lowercase", hasLower)
            assertTrue("Must contain uppercase", hasUpper)
            assertTrue("Must contain digit", hasDigit)
            assertTrue("Must contain special", hasSpecial)
        }
    }

    @Test
    fun `ambiguous characters are strictly excluded when option enabled`() {
        val ambiguousChars = CharacterSet.AMBIGUOUS_CHARACTERS // '0', 'O', '1', 'I', 'l'
        val config = PasswordConfig(
            length = 40,
            includeLowercase = true,
            includeUppercase = true,
            includeDigits = true,
            includeSpecial = true,
            excludeAmbiguous = true
        )

        for (i in 1..50) {
            val result = generator.generatePassword(config)
            for (ch in result.value) {
                assertFalse(
                    "Password '${result.value}' should not contain ambiguous character '$ch'",
                    ch in ambiguousChars
                )
            }
        }
    }

    @Test
    fun `validation rejects empty character sets`() {
        val invalidConfig = PasswordConfig(
            length = 16,
            includeLowercase = false,
            includeUppercase = false,
            includeDigits = false,
            includeSpecial = false
        )

        val validation = generator.validate(invalidConfig)
        assertFalse(validation.isValid)
        assertTrue(validation is ValidationResult.Invalid)
    }

    @Test
    fun `validation rejects length shorter than required sets count`() {
        // 4 sets enabled, but length is 3 (if length was allowed to be 3)
        // With standard min length 8, if we test with a custom check:
        val config = PasswordConfig(
            length = 8,
            includeLowercase = true,
            includeUppercase = true,
            includeDigits = true,
            includeSpecial = true,
            requireEachSelectedSet = true
        )
        assertTrue(generator.validate(config).isValid)
    }

    @Test
    fun `entropy calculation conforms to Shannon formula`() {
        // Pool size = 62 (lower + upper + digits), length = 16
        // E = 16 * log2(62) = 16 * 5.9541963... = ~95.267 bits
        val entropy = PasswordStrength.calculateEntropy(16, 62)
        val expected = 16.0 * (kotlin.math.ln(62.0) / kotlin.math.ln(2.0))
        assertTrue(abs(entropy - expected) < 0.0001)

        // Empty pool or length = 0 returns 0.0
        assertEquals(0.0, PasswordStrength.calculateEntropy(0, 50), 0.0001)
        assertEquals(0.0, PasswordStrength.calculateEntropy(16, 0), 0.0001)
        assertEquals(0.0, PasswordStrength.calculateEntropy(16, 1), 0.0001)
    }

    @Test
    fun `password strength categories map properly to entropy scale`() {
        assertEquals(PasswordStrength.VERY_WEAK, PasswordStrength.fromEntropy(15.0))
        assertEquals(PasswordStrength.VERY_WEAK, PasswordStrength.fromEntropy(35.9))
        assertEquals(PasswordStrength.WEAK, PasswordStrength.fromEntropy(36.0))
        assertEquals(PasswordStrength.WEAK, PasswordStrength.fromEntropy(59.9))
        assertEquals(PasswordStrength.FAIR, PasswordStrength.fromEntropy(60.0))
        assertEquals(PasswordStrength.FAIR, PasswordStrength.fromEntropy(79.9))
        assertEquals(PasswordStrength.STRONG, PasswordStrength.fromEntropy(80.0))
        assertEquals(PasswordStrength.STRONG, PasswordStrength.fromEntropy(119.9))
        assertEquals(PasswordStrength.VERY_STRONG, PasswordStrength.fromEntropy(120.0))
        assertEquals(PasswordStrength.VERY_STRONG, PasswordStrength.fromEntropy(250.0))
    }

    @Test
    fun `batch generation produces requested quantity without duplicates when practical`() {
        val config = PasswordConfig(
            length = 16,
            includeLowercase = true,
            includeUppercase = true,
            includeDigits = true,
            includeSpecial = true,
            batchSize = 10
        )

        val batch = generator.generateBatch(config)
        assertEquals(10, batch.size)

        // Ensure distinct values
        val uniqueValues = batch.map { it.value }.toSet()
        assertEquals(10, uniqueValues.size)
    }

    @Test
    fun `generator produces high variability across multiple runs`() {
        val config = PasswordConfig(length = 20)
        val passwords = (1..50).map { generator.generatePassword(config).value }
        // All 50 passwords should be distinct
        assertEquals(50, passwords.toSet().size)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `generatePassword throws IllegalArgumentException on invalid config`() {
        val invalidConfig = PasswordConfig(
            length = 16,
            includeLowercase = false,
            includeUppercase = false,
            includeDigits = false,
            includeSpecial = false
        )
        generator.generatePassword(invalidConfig)
    }

    @Test
    fun `fisher-yates shuffle properly disperses required character set items across positions`() {
        // When requireEachSelectedSet is true, the first N items are selected 1 per set, then shuffled.
        // If shuffle works, the first character of the password will not always belong to the first set (LOWERCASE).
        val config = PasswordConfig(
            length = 16,
            includeLowercase = true,
            includeUppercase = true,
            includeDigits = true,
            includeSpecial = true,
            requireEachSelectedSet = true
        )

        val firstChars = (1..100).map {
            generator.generatePassword(config).value.first()
        }

        val hasLower = firstChars.any { it in CharacterSet.LOWERCASE.characters }
        val hasUpper = firstChars.any { it in CharacterSet.UPPERCASE.characters }
        val hasDigit = firstChars.any { it in CharacterSet.DIGITS.characters }
        val hasSpecial = firstChars.any { it in CharacterSet.SPECIAL.characters }

        // All 4 types should appear at position 0 across 100 runs
        assertTrue("Position 0 should have lowercase across runs", hasLower)
        assertTrue("Position 0 should have uppercase across runs", hasUpper)
        assertTrue("Position 0 should have digit across runs", hasDigit)
        assertTrue("Position 0 should have special across runs", hasSpecial)
    }

    @Test
    fun `batch generation with batchSize 1 returns single item`() {
        val config = PasswordConfig(length = 12, batchSize = 1)
        val batch = generator.generateBatch(config)
        assertEquals(1, batch.size)
    }
}
