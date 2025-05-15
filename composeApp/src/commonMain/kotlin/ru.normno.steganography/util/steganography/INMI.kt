package ru.normno.steganography.util.steganography

import ru.normno.steganography.util.TextManager.bitsToTextWithMarker
import ru.normno.steganography.util.TextManager.textToBitsWithMarker
import java.awt.image.BufferedImage

class INMI() {

    private fun scaleDownImage(image: BufferedImage): BufferedImage {
        val width = image.width / 2
        val height = image.height / 2
        val scaled = BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)

        for (y in 0 until height) {
            for (x in 0 until width) {
                var sum = 0
                for (dy in 0..1) {
                    for (dx in 0..1) {
                        sum += image.getRGB(x * 2 + dx, y * 2 + dy) and 0xFF
                    }
                }
                val avg = sum / 4
                val gray = (0xFF shl 24) or (avg shl 16) or (avg shl 8) or avg
                scaled.setRGB(x, y, gray)
            }
        }
        return scaled
    }

    private fun interpolateImage(image: BufferedImage, targetWidth: Int, targetHeight: Int): BufferedImage {
        val interpolated = BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_BYTE_GRAY)
        val xRatio = image.width.toFloat() / targetWidth
        val yRatio = image.height.toFloat() / targetHeight

        for (y in 0 until targetHeight) {
            for (x in 0 until targetWidth) {
                val srcX = (x * xRatio).toInt().coerceAtMost(image.width - 1)
                val srcY = (y * yRatio).toInt().coerceAtMost(image.height - 1)
                interpolated.setRGB(x, y, image.getRGB(srcX, srcY))
            }
        }
        return interpolated
    }

    fun embedData(cover: BufferedImage, message: String): BufferedImage {
        val targetWidth = cover.width
        val targetHeight = cover.height
        val scaledDown = scaleDownImage(cover)
        val interpolated = interpolateImage(scaledDown, targetWidth, targetHeight)
        val bits = textToBitsWithMarker(message)
        var bitIndex = 0

        val stego = BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_BYTE_GRAY)
        for (y in 0 until targetHeight) {
            for (x in 0 until targetWidth) {
                val originalValue = interpolated.getRGB(x, y) and 0xFF
                val newValue = if (bitIndex < bits.size) (originalValue and 0xFE) or bits[bitIndex++] else originalValue
                stego.setRGB(x, y, (0xFF shl 24) or (newValue shl 16) or (newValue shl 8) or newValue)
            }
        }
        return stego
    }

    fun recoverOriginalImage(stego: BufferedImage): BufferedImage {
        val width = stego.width / 2
        val height = stego.height / 2
        val recovered = BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)

        for (y in 0 until height) {
            for (x in 0 until width) {
                var sum = 0
                for (dy in 0..1) {
                    for (dx in 0..1) {
                        sum += stego.getRGB(x * 2 + dx, y * 2 + dy) and 0xFF
                    }
                }
                val avg = sum / 4
                val gray = (0xFF shl 24) or (avg shl 16) or (avg shl 8) or avg
                recovered.setRGB(x, y, gray)
            }
        }
        return recovered
    }

    fun extractData(stego: BufferedImage): String {
        val bits = mutableListOf<Int>()
        for (y in 0 until stego.height) {
            for (x in 0 until stego.width) {
                bits.add(stego.getRGB(x, y) and 1)
            }
        }
        return bitsToTextWithMarker(bits)
    }
}
