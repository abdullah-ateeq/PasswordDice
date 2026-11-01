package com.example.passworddice.data.generator

import com.example.passworddice.data.model.CharacterSet
import com.example.passworddice.data.model.PasswordConfig
import com.example.passworddice.data.model.PasswordResult
import com.example.passworddice.data.model.PasswordStrength
import com.example.passworddice.data.model.ValidationResult
import java.security.SecureRandom

/**
 * Cryptographically secure password generator.
 *
 * Employs [SecureRandom] exclusively, guarantees unbiased selection,
 * implements Fisher-Yates shuffle for position randomization,
 * and calculates Shannon entropy estimates.
 */
class SecurePasswordGenerator(
    private val secureRandom: SecureRandom = SecureRandom()
) {

    /**
     * Validates if the given [config] is mathematically and logically feasible.
     */
    fun validate(config: PasswordConfig): ValidationResult {
        if (config.enabledSets.isEmpty()) {
            return ValidationResult.Invalid("Please select at least one character set.")
        }

        val pool = config.characterPool
        if (pool.isEmpty()) {
            return ValidationResult.Invalid("Selected character sets contain no usable characters under current filters.")
        }

        if (config.requireEachSelectedSet && config.length < config.enabledSets.size) {
            return ValidationResult.Invalid(
                "Password length (${config.length}) is too short to include at least one character from each of the ${config.enabledSets.size} selected sets."
            )
        }

        // Verify that every required set actually has at least 1 character available after filtering
        if (config.requireEachSelectedSet) {
            for (set in config.enabledSets) {
                val setChars = CharacterSet.getCharacters(set, config.excludeAmbiguous)
                if (setChars.isEmpty()) {
                    return ValidationResult.Invalid("Set '${set.displayName}' has no remaining characters after excluding ambiguous characters.")
                }
            }
        }

        if (config.length !in PasswordConfig.MIN_LENGTH..PasswordConfig.MAX_LENGTH) {
            return ValidationResult.Invalid(
                "Password length must be between ${PasswordConfig.MIN_LENGTH} and ${PasswordConfig.MAX_LENGTH}."
            )
        }

        return ValidationResult.Valid
    }

    /**
     * Generates a single password conforming to [config].
     * Throws [IllegalArgumentException] if [config] fails validation.
     */
    fun generatePassword(config: PasswordConfig): PasswordResult {
        val validation = validate(config)
        if (validation is ValidationResult.Invalid) {
            throw IllegalArgumentException(validation.reason)
        }

        val chars = CharArray(config.length)
        var writeIndex = 0

        if (config.requireEachSelectedSet) {
            // Step 1: Ensure at least one character from each enabled set is chosen
            for (set in config.enabledSets) {
                val available = CharacterSet.getCharacters(set, config.excludeAmbiguous)
                chars[writeIndex++] = selectRandomChar(available)
            }
        }

        // Step 2: Fill remaining positions securely from the complete pool
        val pool = config.characterPool
        while (writeIndex < config.length) {
            chars[writeIndex++] = selectRandomChar(pool)
        }

        // Step 3: Cryptographically secure Fisher-Yates shuffle
        // This ensures the guaranteed characters are distributed uniformly across all positions.
        fisherYatesShuffle(chars)

        val passwordValue = String(chars)
        val poolSize = config.poolSize
        val entropy = PasswordStrength.calculateEntropy(config.length, poolSize)
        val strength = PasswordStrength.fromEntropy(entropy)

        return PasswordResult(
            value = passwordValue,
            entropy = entropy,
            strength = strength,
            poolSize = poolSize,
            length = config.length
        )
    }

    /**
     * Generates a batch of passwords matching the [config.batchSize].
     * Tries to avoid duplicates within the single batch when practical without compromising randomness.
     */
    fun generateBatch(config: PasswordConfig): List<PasswordResult> {
        val count = config.batchSize
        val results = ArrayList<PasswordResult>(count)
        val seen = HashSet<String>(count)

        var attempts = 0
        val maxAttempts = count * 25 // Reasonable safety ceiling to prevent infinite loops in low-entropy pools

        while (results.size < count && attempts < maxAttempts) {
            attempts++
            val candidate = generatePassword(config)
            if (seen.add(candidate.value)) {
                results.add(candidate)
            }
        }

        // If duplicate avoidance reached ceiling (e.g. tiny pool or duplicate collision), fill remainder directly
        while (results.size < count) {
            results.add(generatePassword(config))
        }

        return results
    }

    /**
     * Selects a single character from [source] with uniform distribution using [SecureRandom].
     * SecureRandom.nextInt(bound) uses internal rejection sampling to eliminate modulo bias.
     */
    private fun selectRandomChar(source: CharSequence): Char {
        require(source.isNotEmpty()) { "Cannot select from an empty character sequence" }
        val index = secureRandom.nextInt(source.length)
        return source[index]
    }

    /**
     * In-place cryptographically secure Fisher-Yates shuffle using [SecureRandom].
     */
    private fun fisherYatesShuffle(array: CharArray) {
        for (i in array.size - 1 downTo 1) {
            val j = secureRandom.nextInt(i + 1)
            val temp = array[i]
            array[i] = array[j]
            array[j] = temp
        }
    }
}
