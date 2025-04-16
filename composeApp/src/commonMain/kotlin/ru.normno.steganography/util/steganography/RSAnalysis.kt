package ru.normno.steganography.util.steganography

import java.awt.image.BufferedImage
import kotlin.math.abs
import kotlin.math.sqrt

object RSAnalysis {

    const val ANALYSIS_COLOUR_RED = 0
    const val ANALYSIS_COLOUR_GREEN = 1
    const val ANALYSIS_COLOUR_BLUE = 2

    private fun getRed(pixel: Int): Int = (pixel shr 16) and 0xff
    private fun getGreen(pixel: Int): Int = (pixel shr 8) and 0xff
    private fun getBlue(pixel: Int): Int = pixel and 0xff

    private fun getPixelColour(pixel: Int, colour: Int): Int {
        return when (colour) {
            ANALYSIS_COLOUR_GREEN -> getGreen(pixel)
            ANALYSIS_COLOUR_BLUE -> getBlue(pixel)
            else -> getRed(pixel)
        }
    }

    private fun negateLSB(byte: Int): Int = if ((byte and 1) == 0) byte or 1 else byte and 0xFE

    private fun invertLSB(byte: Int): Int {
        if (byte == 255) return 256
        if (byte == 256) return 255
        return negateLSB(byte + 1) - 1
    }

    private fun invertMask(mask: List<Int>): List<Int> = mask.map { -it }

    private fun flipBlock(block: List<Int>, mask: List<Int>): List<Int> {
        return block.mapIndexed { i, pixel ->
            var valByte = getRed(pixel) // Always red for LSB flip
            valByte = when (mask[i]) {
                1 -> negateLSB(valByte)
                -1 -> invertLSB(valByte)
                else -> valByte
            }
            (0xFF shl 24) or (valByte shl 16) or (valByte shl 8) or valByte
        }
    }

    private fun getVariation(block: List<Int>, colour: Int): Double {
        var varSum = 0.0
        for (i in block.indices step 4) {
            if (i + 3 < block.size) {
                val c = List(4) { getPixelColour(block[i + it], colour) }
                varSum += abs(c[0] - c[1]) + abs(c[3] - c[2]) + abs(c[1] - c[3]) + abs(c[2] - c[0])
            }
        }
        return varSum
    }

    private fun getNegativeVariation(block: List<Int>, colour: Int, mask: List<Int>): Double {
        var varSum = 0.0
        for (i in block.indices step 4) {
            if (i + 3 < block.size) {
                val c = List(4) { idx ->
                    var valC = getPixelColour(block[i + idx], colour)
                    if (mask[i + idx] == -1) valC = invertLSB(valC)
                    valC
                }
                varSum += abs(c[0] - c[1]) + abs(c[3] - c[2]) + abs(c[1] - c[3]) + abs(c[2] - c[0])
            }
        }
        return varSum
    }

    private fun createMasksStatic(m: Int, n: Int): Pair<List<Int>, List<Int>> {
        val maskPos = mutableListOf<Int>()
        val maskNeg = mutableListOf<Int>()
        for (i in 0 until n) {
            for (j in 0 until m) {
                if ((j % 2 == 0 && i % 2 == 0) || (j % 2 == 1 && i % 2 == 1)) {
                    maskPos.add(1)
                    maskNeg.add(0)
                } else {
                    maskPos.add(0)
                    maskNeg.add(1)
                }
            }
        }
        return maskPos to maskNeg
    }

    private fun getX(r: Double, rm: Double, r1: Double, rm1: Double, s: Double, sm: Double, s1: Double, sm1: Double): Double {
        val dzero = r - s
        val dminuszero = rm - sm
        val done = r1 - s1
        val dminusone = rm1 - sm1
        val a = 2 * (done + dzero)
        val b = dminuszero - dminusone - done - (3 * dzero)
        val c = dzero - dminuszero
        val x = if (a == 0.0) {
            c / b
        } else {
            val discriminant = b * b - 4 * a * c
            if (discriminant >= 0) {
                val rootPos = (-b + sqrt(discriminant)) / (2 * a)
                val rootNeg = (-b - sqrt(discriminant)) / (2 * a)
                if (abs(rootPos) <= abs(rootNeg)) rootPos else rootNeg
            } else {
                val cr = if ((r1 - r + rm - rm1) != 0.0) (rm - r) / (r1 - r + rm - rm1) else 0.0
                val cs = if ((s1 - s + sm - sm1) != 0.0) (sm - s) / (s1 - s + sm - sm1) else 0.0
                (cr + cs) / 2
            }
        }

        return if (x == 0.0) {
            val cr = if ((r1 - r + rm - rm1) != 0.0) (rm - r) / (r1 - r + rm - rm1) else 0.0
            val cs = if ((s1 - s + sm - sm1) != 0.0) (sm - s) / (s1 - s + sm - sm1) else 0.0
            (cr + cs) / 2
        } else x
    }

    fun doAnalysis(image: BufferedImage, colour: Int = ANALYSIS_COLOUR_RED, overlap: Boolean = false): DoubleArray {
        val m = 2
        val n = 2
        val mask = createMasksStatic(m, n)
        val maskPos = mask.first
        val maskNeg = mask.second

        val imgx = image.width
        val imgy = image.height
        val blockSize = m * n

        var numRegular = 0.0
        var numSingular = 0.0
        var numNegReg = 0.0
        var numNegSing = 0.0
        var numUnusable = 0.0

        var startX = 0
        var startY = 0

        while (startX < imgx && startY < imgy) {
            for (mIdx in 0..1) {
                val block = mutableListOf<Int>()
                for (i in 0 until n) {
                    for (j in 0 until m) {
                        val x = startX + j
                        val y = startY + i
                        if (x < imgx && y < imgy) {
                            block.add(image.getRGB(x, y))
                        }
                    }
                }
                if (block.size < blockSize) continue

                val variationB = getVariation(block, colour)
                val blockFlipped = flipBlock(block, if (mIdx == 0) maskPos else maskNeg)
                val variationP = getVariation(blockFlipped, colour)

                val blockRestored = flipBlock(blockFlipped, if (mIdx == 0) maskPos else maskNeg)
                val variationN = getNegativeVariation(blockRestored, colour, invertMask(if (mIdx == 0) maskPos else maskNeg))

                when {
                    variationP > variationB -> numRegular++
                    variationP < variationB -> numSingular++
                    else -> numUnusable++
                }

                if (variationN > variationB) numNegReg++
                else if (variationN < variationB) numNegSing++
            }

            startX += if (overlap) 1 else m
            if (startX >= imgx - 1) {
                startX = 0
                startY += if (overlap) 1 else n
            }
            if (startY >= imgy - 1) break
        }

        val totalGroups = numRegular + numSingular + numUnusable + 1e-6
        val x = getX(numRegular, numNegReg, numRegular, numNegReg, numSingular, numNegSing, numSingular, numNegSing)
        val epf = if ((2 * (x - 1)) == 0.0) 0.0 else abs(x / (2 * (x - 1)))
        val ml = if ((x - 0.5) == 0.0) 0.0 else abs(x / (x - 0.5))

        return doubleArrayOf(
            numRegular, numSingular, numNegReg, numNegSing,
            abs(numRegular - numNegReg), abs(numSingular - numNegSing),
            (numRegular / totalGroups) * 100, (numSingular / totalGroups) * 100,
            (numNegReg / totalGroups) * 100, (numNegSing / totalGroups) * 100,
            epf, ml, ((imgx * imgy) * ml) / 8
        )
    }
}
