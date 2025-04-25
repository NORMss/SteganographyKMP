package ru.normno.steganography.presentation.multi

import ru.normno.steganography.domain.model.FileInfo
import ru.normno.steganography.util.ImageFormat
import ru.normno.steganography.util.StegoMethod

data class MultiState(
    val sourceFilesInfo: List<FileInfo> = emptyList(),
    val resultFilesInfo: List<FileInfo> = emptyList(),
    val visualAttackFilesInfo: List<FileInfo> = emptyList(),
    val selectedImageFormat: ImageFormat = ImageFormat.PNG(),
    val selectedStegoMethod: StegoMethod = StegoMethod.KJB,
    val embedText: String = "",
    val extractText: String = "",
    val isEbbing: Boolean = false,
    val isExtracting: Boolean = false,
)
