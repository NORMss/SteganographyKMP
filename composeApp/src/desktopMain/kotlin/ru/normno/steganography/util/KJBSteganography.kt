package ru.normno.steganography.util

import java.awt.image.BufferedImage
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.random.Random

class KJBSteganography(private val lambda: Double, private val seed: Int) {
    private val endMarker = byteArrayOf(0xFE.toByte(), 0x00, 0xFF.toByte(), 0xFA.toByte())

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

    private fun textToBitsWithMarker(text: String): List<Int> {
        val data = text.toByteArray(Charsets.UTF_8) + endMarker
        return data.flatMap { byte ->
            (7 downTo 0).map { bit -> (byte.toInt() shr bit) and 1 }
        }
    }

    private fun bitsToTextWithMarker(bits: List<Int>): String {
        val bytes = bits.chunked(8).map { chunk ->
            chunk.fold(0) { acc, bit -> (acc shl 1) or bit }.toByte()
        }.toByteArray()

        val markerIndex = bytes.indices.find { i ->
            i + endMarker.size <= bytes.size && bytes.copyOfRange(i, i + endMarker.size)
                .contentEquals(endMarker)
        } ?: -1

        return if (markerIndex >= 0) {
            String(bytes.copyOfRange(0, markerIndex), Charsets.UTF_8)
        } else {
            ""
        }
    }
}
