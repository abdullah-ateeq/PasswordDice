package com.example.passworddice.data.model

import java.util.UUID

/**
 * Encapsulates a generated password and its calculated security metadata.
 * Note: Passwords are held purely in memory and are never written to disk or logs.
 */
data class PasswordResult(
    val id: String = UUID.randomUUID().toString(),
    val value: String,
    val entropy: Double,
    val strength: PasswordStrength,
    val poolSize: Int,
    val length: Int
)
