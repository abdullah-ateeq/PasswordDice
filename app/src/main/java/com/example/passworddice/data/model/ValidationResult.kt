package com.example.passworddice.data.model

/**
 * Result of validating a [PasswordConfig].
 */
sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val reason: String) : ValidationResult()

    val isValid: Boolean
        get() = this is Valid

    val errorMessage: String?
        get() = (this as? Invalid)?.reason
}
