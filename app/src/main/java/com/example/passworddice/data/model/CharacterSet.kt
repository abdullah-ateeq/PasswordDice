package com.example.passworddice.data.model

/**
 * Character sets available for password generation.
 */
enum class CharacterSet(
    val displayName: String,
    val exampleText: String,
    val characters: String
) {
    LOWERCASE(
        displayName = "Lowercase (a-z)",
        exampleText = "a b c d ...",
        characters = "abcdefghijklmnopqrstuvwxyz"
    ),
    UPPERCASE(
        displayName = "Uppercase (A-Z)",
        exampleText = "A B C D ...",
        characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    ),
    DIGITS(
        displayName = "Numbers (0-9)",
        exampleText = "0 1 2 3 ...",
        characters = "0123456789"
    ),
    SPECIAL(
        displayName = "Special characters (!@#$...)",
        exampleText = "! @ # $ % & * ...",
        characters = "!@#$%^&*()_+-=[]{}|;:,.<>?/~"
    );

    companion object {
        /**
         * Characters commonly confused visually:
         * 0 (zero) / O (capital o), 1 (one) / I (capital i) / l (lowercase L).
         */
        val AMBIGUOUS_CHARACTERS = setOf('0', 'O', '1', 'I', 'l')

        /**
         * Returns characters for this set, filtering ambiguous characters if requested.
         */
        fun getCharacters(set: CharacterSet, excludeAmbiguous: Boolean): String {
            return if (excludeAmbiguous) {
                set.characters.filter { it !in AMBIGUOUS_CHARACTERS }
            } else {
                set.characters
            }
        }
    }
}
