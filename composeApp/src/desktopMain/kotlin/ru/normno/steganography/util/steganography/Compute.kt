package ru.normno.steganography.util.steganography

import java.awt.image.BufferedImage
import kotlin.math.log10
import kotlin.math.pow

object Compute {
    fun computeCapacity(cover: BufferedImage): Int {
        val totalPixels = cover.width * cover.height
        val capacityBits = totalPixels
        return capacityBits
    }

    fun computePSNR(cover: BufferedImage, stego: BufferedImage): Double {
        val width = cover.width
        val height = cover.height
        var mse = 0.0

        for (y in 0 until height) {
            for (x in 0 until width) {
                val diff = (cover.getRGB(x, y) and 0xFF) - (stego.getRGB(x, y) and 0xFF)
                mse += diff.toDouble().pow(2)
            }
        }
        mse /= (width * height)
        return if (mse == 0.0) Double.POSITIVE_INFINITY else 10 * log10((255.0.pow(2)) / mse)
    }
}