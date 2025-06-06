package ru.normno.steganography.presentation.multi

import ru.normno.steganography.domain.model.SecretFile
import ru.normno.steganography.domain.model.FileInfo
import ru.normno.steganography.domain.model.TestInfo
import ru.normno.steganography.util.ImageFormat
import ru.normno.steganography.util.StegoImageMethod

data class MultiState(
    val sourceFilesInfo: List<FileInfo> = emptyList(),
    val resultFilesInfo: List<FileInfo> = emptyList(),
    val testsInfo: List<TestInfo> = emptyList(),
    val visualAttackFilesInfo: List<FileInfo> = emptyList(),
    val selectedImageFormat: ImageFormat = ImageFormat.PNG(),
    val selectedStegoImageMethod: StegoImageMethod = StegoImageMethod.KJB,
    val embedText: String = "",
    val extractText: List<SecretFile> = emptyList(),
    val isEbbing: Boolean = false,
    val isExtracting: Boolean = false,
)
