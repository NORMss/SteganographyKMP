package ru.normno.steganography.domain.model

data class TestInfo(
    val psnrTotaldBm: Double? = null,
    val rsTotal: List<Double> = emptyList(),
    val chiSquareTotal: Double? = null,
    val aumpTotal: Double? = null,
    val compressionTotal: Double? = null,
    val capacityTotalKb: Double? = null,
)
