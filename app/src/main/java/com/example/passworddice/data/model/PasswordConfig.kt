package com.example.passworddice.data.model

/**
 * Configuration options for generating one or more passwords.
 */
data class PasswordConfig(
    val length: Int = 16,
    val includeLowercase: Boolean = true,
    val includeUppercase: Boolean = true,
    val includeDigits: Boolean = true,
    val includeSpecial: Boolean = true,
    val excludeAmbiguous: Boolean = false,
    val requireEachSelectedSet: Boolean = true,
    val batchSize: Int = 1
) {
    init {
        require(length in MIN_LENGTH..MAX_LENGTH) {
            "Length must be between $MIN_LENGTH and $MAX_LENGTH (got $length)"
        }
        require(batchSize in MIN_BATCH_SIZE..MAX_BATCH_SIZE) {
            "Batch size must be between $MIN_BATCH_SIZE and $MAX_BATCH_SIZE (got $batchSize)"
        }
    }

    /**
     * Set of CharacterSets that are enabled by this configuration.
     */
    val enabledSets: Set<CharacterSet>
        get() = buildSet {
            if (includeLowercase) add(CharacterSet.LOWERCASE)
            if (includeUppercase) add(CharacterSet.UPPERCASE)
            if (includeDigits) add(CharacterSet.DIGITS)
            if (includeSpecial) add(CharacterSet.SPECIAL)
        }

    /**
     * Character pool aggregated from all enabled sets, taking ambiguous filter into account.
     */
    val characterPool: String
        get() = enabledSets.joinToString(separator = "") {
            CharacterSet.getCharacters(it, excludeAmbiguous)
        }

    /**
     * Distinct character pool size.
     */
    val poolSize: Int
        get() = characterPool.toSet().size

    companion object {
        const val MIN_LENGTH = 8
        const val MAX_LENGTH = 128
        const val DEFAULT_LENGTH = 16
        const val MIN_BATCH_SIZE = 1
        const val MAX_BATCH_SIZE = 10
        const val DEFAULT_BATCH_SIZE = 1
    }
}
