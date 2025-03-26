package ru.normno.steganography.util.steganography

import ru.normno.steganography.util.TextManager.bitsToTextWithMarker
import ru.normno.steganography.util.TextManager.textToBitsWithMarker
import java.awt.image.BufferedImage

class LSBMatchingRevisited {
    fun embedData(cover: BufferedImage, message: String): BufferedImage? {
        val stegoImage = BufferedImage(
            cover.width,
            cover.height,
            BufferedImage.TYPE_INT_RGB
        )

        val messageBits = textToBitsWithMarker(message)

        val totalBits = cover.width * cover.height * 3
        if (messageBits.size > totalBits) {
            return null
        }

        var bitIndex = 0

        for (y in 0 until cover.height) {
            for (x in 0 until cover.width) {
                val rgb = cover.getRGB(x, y)
                var red = (rgb shr 16) and 0xFF
                var green = (rgb shr 8) and 0xFF
                var blue = rgb and 0xFF

                // Встраиваем биты в каждый цветовой канал
                if (bitIndex < messageBits.size) {
                    red = embedBit(red, messageBits[bitIndex++])
                }
                if (bitIndex < messageBits.size) {
                    green = embedBit(green, messageBits[bitIndex++])
                }
                if (bitIndex < messageBits.size) {
                    blue = embedBit(blue, messageBits[bitIndex++])
                }

                val newRgb = (red shl 16) or (green shl 8) or blue
                stegoImage.setRGB(x, y, newRgb)
            }
        }

        return stegoImage
    }

    fun extractData(stego: BufferedImage): String {
        val extractedBits = mutableListOf<Int>()

        for (y in 0 until stego.height) {
            for (x in 0 until stego.width) {
                val rgb = stego.getRGB(x, y)
                val red = (rgb shr 16) and 0xFF
                val green = (rgb shr 8) and 0xFF
                val blue = rgb and 0xFF

                extractedBits.add(extractBit(red))
                extractedBits.add(extractBit(green))
                extractedBits.add(extractBit(blue))
            }
        }

        return bitsToTextWithMarker(extractedBits)
    }

    private fun embedBit(value: Int, bit: Int): Int {
        val lsb = value and 1
        return if (lsb != bit) {
            if (value == 255) value - 1 else value + 1
        } else {
            value
        }
    }

    private fun extractBit(value: Int): Int {
        return value and 1
    }
}