package ru.normno.steganography.util.steganography

import java.awt.image.BufferedImage
import kotlin.math.log10
import kotlin.math.pow

object Compute {
    fun computeCapacity(cover: BufferedImage): Int {
        return (cover.width * cover.height) / 4 * 3
    }

    fun computeEnhancedPSNR(cover: BufferedImage, stego: BufferedImage): Map<String, Double> {
        require(cover.width == stego.width && cover.height == stego.height)

        val results = mutableMapOf<String, Double>()
        val channelMSE = DoubleArray(3)

        for (y in 0 until cover.height) {
            for (x in 0 until cover.width) {
                val cVal = cover.getRGB(x, y).toRGBComponents()
                val sVal = stego.getRGB(x, y).toRGBComponents()

                for (i in 0..2) {
                    channelMSE[i] += (cVal[i] - sVal[i]).pow(2)
                }
            }
        }

        for (i in 0..2) {
            channelMSE[i] /= (cover.width * cover.height)
            results["PSNR_${when(i) {
                0 -> "R"
                1 -> "G"
                else -> "B"
            }}"] = 10 * log10(255.0.pow(2) / channelMSE[i])
        }

        val avgMSE = channelMSE.average()
        results["PSNR_Avg"] = 10 * log10(255.0.pow(2) / avgMSE)

        return results
    }

    private fun Int.toRGBComponents(): FloatArray {
        return floatArrayOf(
            (this shr 16 and 0xFF).toFloat(),
            (this shr 8 and 0xFF).toFloat(),
            (this and 0xFF).toFloat()
        )
    }
}