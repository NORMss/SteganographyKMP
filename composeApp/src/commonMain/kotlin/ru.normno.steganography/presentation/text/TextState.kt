package ru.normno.steganography.presentation.text

import ru.normno.steganography.util.StegoTextMethod

data class TextState(
    val selectedStegoMethod: StegoTextMethod = StegoTextMethod.Whitespace,
    val secretText: String = "",
    val textWithSecret: String = "",
    val originalText: String = "",
    val extractedSecretText: String = "",
)