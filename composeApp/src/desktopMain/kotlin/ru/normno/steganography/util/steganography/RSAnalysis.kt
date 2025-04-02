package ru.normno.steganography.util.steganography

import java.awt.image.BufferedImage
import kotlin.math.abs

object RSAnalysis {

    private fun getRSGroups(image: BufferedImage): Pair<List<Int>, List<Int>> {
        val width = image.width
        val height = image.height
        val rGroup = mutableListOf<Int>()
        val sGroup = mutableListOf<Int>()

        for (x in 0 until width - 1) {
            for (y in 0 until height - 1) {
                val pixel = image.getRGB(x, y) and 0xFF
                val nextPixel = image.getRGB(x + 1, y) and 0xFF

                if (abs(pixel - nextPixel) < 20) {
                    rGroup.add(pixel)
                } else {
                    sGroup.add(pixel)
                }
            }
        }
        return Pair(rGroup, sGroup)
    }

    fun analyze(image: BufferedImage): Double {
        val (rGroup, sGroup) = getRSGroups(image)

        val rMean = rGroup.averageOrNull() ?: 0.0
        val sMean = sGroup.averageOrNull() ?: 0.0

        return if (sMean == 0.0) 0.0 else rMean / sMean
    }

    private fun List<Int>.averageOrNull(): Double? = if (isNotEmpty()) average() else null
}
