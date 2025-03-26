package ru.normno.steganography.util.steganography

import ru.normno.steganography.util.ImageFormat
import ru.normno.steganography.util.TextManager.bitsToTextWithMarker
import ru.normno.steganography.util.TextManager.textToBitsWithMarker
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.random.Random

class KJB(private val lambda: Double, private val seed: Int) {
    private fun brightness(r: Int, g: Int, b: Int): Double {
        return 0.299 * r + 0.587 * g + 0.114 * b
    }

    fun embedData(cover: BufferedImage, message: String): BufferedImage? {
        val bits = textToBitsWithMarker(message)
        val width = cover.width
        val height = cover.height
        val totalPixels = width * height

        if (bits.size > totalPixels) return null

        val rng = Random(seed)
        val indices = (0 until totalPixels).shuffled(rng).take(bits.size)

        val stegoImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        val graphics = stegoImage.createGraphics()
        graphics.drawImage(cover, 0, 0, null)
        graphics.dispose()

        for (i in bits.indices) {
            val linearIndex = indices[i]
            val x = linearIndex % width
            val y = linearIndex / width

            val rgb = stegoImage.getRGB(x, y)
            val r = (rgb shr 16) and 0xFF
            val g = (rgb shr 8) and 0xFF
            val b = rgb and 0xFF

            val yBrightness = brightness(r, g, b)
            val newB = if (bits[i] == 1) b + lambda * yBrightness else b - lambda * yBrightness
            val clampedB = min(255, max(0, newB.roundToInt()))

            stegoImage.setRGB(x, y, (r shl 16) or (g shl 8) or clampedB)
        }

        return stegoImage
    }

    fun extractData(stegoImage: BufferedImage): String {
        val width = stegoImage.width
        val height = stegoImage.height
        val totalPixels = width * height

        val rng = Random(seed)
        val indices = (0 until totalPixels).shuffled(rng)
        val bits = mutableListOf<Int>()

        for (index in indices) {
            val x = index % width
            val y = index / width

            val rgb = stegoImage.getRGB(x, y)
            val r = (rgb shr 16) and 0xFF
            val g = (rgb shr 8) and 0xFF
            val b = rgb and 0xFF

            val yBrightness = brightness(r, g, b)
            bits.add(if (b >= yBrightness) 1 else 0)
        }

        return bitsToTextWithMarker(bits)
    }
}