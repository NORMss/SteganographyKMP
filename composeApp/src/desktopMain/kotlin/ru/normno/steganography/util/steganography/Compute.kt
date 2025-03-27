package ru.normno.steganography.util.steganography

import java.awt.image.BufferedImage
import kotlin.math.log10
import kotlin.math.pow

object Compute {
    fun computeCapacity(cover: BufferedImage): Int {
        return (cover.width * cover.height) / 4 * 3
    }

    fun computePSNR(original: BufferedImage, recovered: BufferedImage): Double {
        require(original.width == recovered.width && original.height == recovered.height) { "Размеры изображений должны совпадать!" }

        var mse = 0.0
        val width = original.width
        val height = original.height

        for (y in 0 until height) {
            for (x in 0 until width) {
                val origPixel = original.getRGB(x, y) and 0xFF
                val recPixel = recovered.getRGB(x, y) and 0xFF
                mse += (origPixel - recPixel).toDouble().pow(2)
            }
        }

        mse /= (width * height)
        return if (mse == 0.0) Double.POSITIVE_INFINITY else 10 * log10(255.0.pow(2) / mse)
    }
}