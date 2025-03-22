package ru.normno.steganography.presentation

import ru.normno.steganography.domain.model.FileInfo

data class MainState(
    val sourceFileInfo: FileInfo? = null,
    val resultFileInfo: FileInfo? = null,
    val embedText: String = "",
    val extractText: String = "",
    val isEbbing: Boolean = false,
    val isExtracting: Boolean = false,
)