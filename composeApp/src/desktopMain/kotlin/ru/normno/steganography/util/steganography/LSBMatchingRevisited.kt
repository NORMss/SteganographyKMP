package ru.normno.steganography.util.steganography

import java.awt.image.BufferedImage
import kotlin.math.log10
import kotlin.math.pow

class LSBMatchingRevisited {
    private val endMarker = byteArrayOf(0xFE.toByte(), 0x00, 0xFF.toByte(), 0xFA.toByte())

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

    private fun f(yi: Int, yiPlus: Int) = ((yi / 2) + yiPlus) and 1

    private fun adjustPixel(pixel: Int, constant: Int, targetM: Int, isFirst: Boolean): Int {
        val candidateMinus = if (pixel > 0) pixel - 1 else null
        val candidatePlus = if (pixel < 255) pixel + 1 else null

        return when {
            isFirst && candidateMinus != null && f(
                candidateMinus,
                constant
            ) == targetM -> candidateMinus

            isFirst && candidatePlus != null && f(
                candidatePlus,
                constant
            ) == targetM -> candidatePlus

            !isFirst && pixel % 2 == 0 && candidatePlus != null && f(
                constant,
                candidatePlus
            ) == targetM -> candidatePlus

            !isFirst && candidateMinus != null && f(
                constant,
                candidateMinus
            ) == targetM -> candidateMinus

            else -> pixel
        }
    }

    fun embedLSBMatchingRevisited(cover: BufferedImage, message: String): BufferedImage? {
        val bits = textToBitsWithMarker(message)
        val width = cover.width
        val height = cover.height
        var bitIndex = 0

        val result = BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)
        for (y in 0 until height) {
            for (x in 0 until width step 2) {
                if (bitIndex >= bits.size) return result

                val pixel1 = cover.getRGB(x, y) and 0xFF
                val pixel2 = cover.getRGB(x + 1, y) and 0xFF

                val m1 = bits[bitIndex++]
                val m2 = if (bitIndex < bits.size) bits[bitIndex++] else 0

                val newPixel1 =
                    if ((pixel1 and 1) != m1) adjustPixel(pixel1, pixel2, m2, true) else pixel1
                val newPixel2 = if (f(newPixel1, pixel2) == m2) pixel2 else adjustPixel(
                    pixel2,
                    newPixel1,
                    m2,
                    false
                )

                result.setRGB(x, y, newPixel1)
                result.setRGB(x + 1, y, newPixel2)
            }
        }
        return result
    }

    fun extractLSBMatchingRevisited(stego: BufferedImage): List<Int> {
        val width = stego.width
        val height = stego.height
        val extractedBits = mutableListOf<Int>()

        for (y in 0 until height) {
            for (x in 0 until width step 2) {
                val pixel1 = stego.getRGB(x, y) and 0xFF
                val pixel2 = stego.getRGB(x + 1, y) and 0xFF

                extractedBits.add(pixel1 and 1)
                extractedBits.add(f(pixel1, pixel2))
            }
        }
        return extractedBits
    }

    fun computeCapacity(cover: BufferedImage): Pair<Int, Int> {
        val totalPixels = cover.width * cover.height
        val capacityBits = totalPixels
        return Pair(capacityBits, capacityBits / 8)
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