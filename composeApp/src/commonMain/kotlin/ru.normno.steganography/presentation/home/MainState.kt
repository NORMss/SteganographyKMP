package ru.normno.steganography.presentation.home

import ru.normno.steganography.domain.model.FileInfo
import ru.normno.steganography.domain.model.TestInfo
import ru.normno.steganography.util.ImageFormat
import ru.normno.steganography.util.StegoImageMethod

data class MainState(
    val sourceFileInfo: FileInfo? = null,
    val resultFileInfo: FileInfo? = null,
    val visualAttackFileInfo: FileInfo? = null,
    val selectedImageFormat: ImageFormat = ImageFormat.PNG(),
    val selectedStegoImageMethod: StegoImageMethod = StegoImageMethod.KJB,
    val testInfo: TestInfo? = null,
    val embedText: String = "",
    val extractText: String = "",
    val isEbbing: Boolean = false,
    val isExtracting: Boolean = false,
)