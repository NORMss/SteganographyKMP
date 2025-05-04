package ru.normno.steganography.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SecretFile(
    val fileName: String = "",
    val secretText: String = "",
)
