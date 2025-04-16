package ru.normno.steganography.util.steganography

import ru.normno.steganography.util.TextManager.bitsToTextWithMarker
import ru.normno.steganography.util.TextManager.textToBitsWithMarker

import java.awt.image.BufferedImage
import kotlin.math.floor
import kotlin.math.log2
import kotlin.math.max
import kotlin.math.min

class IMNP() {
    fun embedData(cover: BufferedImage, message: String): BufferedImage {
        val messageBits = textToBitsWithMarker(message)
        var bitIndex = 0

        val stego = BufferedImage(cover.width, cover.height, BufferedImage.TYPE_INT_RGB)
        val graphics = stego.createGraphics()
        graphics.drawImage(cover, 0, 0, null)
        graphics.dispose()

        for (h in 0 until cover.height / 2) {
            for (l in 0 until cover.width / 2) {
                val x = 2 * h
                val y = 2 * l

                val c00 = cover.getRGB(x, y) and 0xFF
                val c02 = if (y + 2 < cover.width) cover.getRGB(x, y + 2) and 0xFF else c00
                val c20 = if (x + 2 < cover.height) cover.getRGB(x + 2, y) and 0xFF else c00
                val c22 = if (x + 2 < cover.height && y + 2 < cover.width) cover.getRGB(x + 2, y + 2) and 0xFF else c00

                val Omin = min(min(c00, c02), min(c20, c22))
                val Omax = max(max(c00, c02), max(c20, c22))

                val c01 = (Omax + (c00 + c02) / 2) / 2
                val c10 = (Omax + (c00 + c20) / 2) / 2
                val c11 = (c10 + c01) / 2

                val v1 = c01 - Omin
                val v2 = c10 - Omin
                val v3 = c11 - Omin

                val a1 = floor(log2(v1.toDouble())).toInt()
                val a2 = floor(log2(v2.toDouble())).toInt()
                val a3 = floor(log2(v3.toDouble())).toInt()

                stego.setRGB(x, y, cover.getRGB(x, y))

                if (bitIndex < messageBits.size && a1 > 0) {
                    val bitsToEmbed = min(a1, messageBits.size - bitIndex)
                    val secretBits = messageBits.subList(bitIndex, bitIndex + bitsToEmbed)
                    val R1 = secretBits.fold(0) { acc, bit -> (acc shl 1) or bit }

                    val newC01 = Omax - R1
                    stego.setRGB(x, y + 1, (newC01 shl 16) or (newC01 shl 8) or newC01)
                    bitIndex += bitsToEmbed
                } else {
                    stego.setRGB(x, y + 1, cover.getRGB(x, y + 1))
                }

                if (bitIndex < messageBits.size && a2 > 0) {
                    val bitsToEmbed = min(a2, messageBits.size - bitIndex)
                    val secretBits = messageBits.subList(bitIndex, bitIndex + bitsToEmbed)
                    val R2 = secretBits.fold(0) { acc, bit -> (acc shl 1) or bit }

                    val newC10 = Omax - R2
                    stego.setRGB(x + 1, y, (newC10 shl 16) or (newC10 shl 8) or newC10)
                    bitIndex += bitsToEmbed
                } else {
                    stego.setRGB(x + 1, y, cover.getRGB(x + 1, y))
                }

                if (bitIndex < messageBits.size && a3 > 0) {
                    val bitsToEmbed = min(a3, messageBits.size - bitIndex)
                    val secretBits = messageBits.subList(bitIndex, bitIndex + bitsToEmbed)
                    val R3 = secretBits.fold(0) { acc, bit -> (acc shl 1) or bit }

                    val newC11 = Omin + R3
                    stego.setRGB(x + 1, y + 1, (newC11 shl 16) or (newC11 shl 8) or newC11)
                    bitIndex += bitsToEmbed
                } else {
                    stego.setRGB(x + 1, y + 1, cover.getRGB(x + 1, y + 1))
                }
            }
        }

        return stego
    }

    fun extractData(stego: BufferedImage): String {
        val extractedBits = mutableListOf<Int>()

        for (h in 0 until stego.height / 2) {
            for (l in 0 until stego.width / 2) {
                val x = 2 * h
                val y = 2 * l

                val c00 = stego.getRGB(x, y) and 0xFF
                val c02 = if (y + 2 < stego.width) stego.getRGB(x, y + 2) and 0xFF else c00
                val c20 = if (x + 2 < stego.height) stego.getRGB(x + 2, y) and 0xFF else c00
                val c22 = if (x + 2 < stego.height && y + 2 < stego.width) stego.getRGB(x + 2, y + 2) and 0xFF else c00

                val Omin = min(min(c00, c02), min(c20, c22))
                val Omax = max(max(c00, c02), max(c20, c22))

                val s01 = stego.getRGB(x, y + 1) and 0xFF
                val s10 = stego.getRGB(x + 1, y) and 0xFF
                val s11 = stego.getRGB(x + 1, y + 1) and 0xFF

                val v1 = if (s01 <= Omax) Omax - s01 else 0
                val v2 = if (s10 <= Omax) Omax - s10 else 0
                val v3 = if (s11 >= Omin) s11 - Omin else 0

                val a1 = if (v1 > 0) floor(log2(v1.toDouble())).toInt() else 0
                val a2 = if (v2 > 0) floor(log2(v2.toDouble())).toInt() else 0
                val a3 = if (v3 > 0) floor(log2(v3.toDouble())).toInt() else 0

                if (a1 > 0) {
                    val R1 = Omax - s01
                    for (i in a1 - 1 downTo 0) {
                        extractedBits.add((R1 shr i) and 1)
                    }
                }

                if (a2 > 0) {
                    val R2 = Omax - s10
                    for (i in a2 - 1 downTo 0) {
                        extractedBits.add((R2 shr i) and 1)
                    }
                }

                if (a3 > 0) {
                    val R3 = s11 - Omin
                    for (i in a3 - 1 downTo 0) {
                        extractedBits.add((R3 shr i) and 1)
                    }
                }
            }
        }

        return bitsToTextWithMarker(extractedBits)
    }
}