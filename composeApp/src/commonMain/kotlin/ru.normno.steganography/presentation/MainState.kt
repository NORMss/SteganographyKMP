package ru.normno.steganography.presentation

import ru.normno.steganography.domain.model.FileInfo
import ru.normno.steganography.util.ImageFormat
import ru.normno.steganography.util.StegoMethod

data class MainState(
    val sourceFileInfo: FileInfo? = null,
    val resultFileInfo: FileInfo? = null,
    val visualAttackFileInfo: FileInfo? = null,
    val selectedImageFormat: ImageFormat = ImageFormat.PNG(),
    val selectedStegoMethod: StegoMethod = StegoMethod.KJB,
    val psnrTotaldBm: Double? = null,
    val rsTotal: List<Double> = emptyList(),
    val chiSquareTotal: Double? = null,
    val aumpTotal: Double? = null,
    val compressionTotal: Double? = null,
    val capacityTotalKb: Double? = null,
    val embedText: String = "",
    val extractText: String = "",
    val isEbbing: Boolean = false,
    val isExtracting: Boolean = false,
)