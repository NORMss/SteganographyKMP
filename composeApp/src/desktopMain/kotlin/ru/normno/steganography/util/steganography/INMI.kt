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
                val rgb = image.getRGB(x * 2, y * 2)
                scaled.setRGB(x, y, rgb)
            }
        }

        return scaled
    }

    private fun interpolateImage(image: BufferedImage): BufferedImage {
        val width = image.width * 2 - 1
        val height = image.height * 2 - 1
        val interpolated = BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)

        // Copy original pixels (anchor points)
        for (y in 0 until image.height) {
            for (x in 0 until image.width) {
                val rgb = image.getRGB(x, y)
                interpolated.setRGB(x * 2, y * 2, rgb)
            }
        }

        // Interpolate horizontal pixels
        for (y in 0 until height step 2) {
            for (x in 1 until width step 2) {
                val left = interpolated.getRGB(x - 1, y) and 0xFF
                val right = if (x + 1 < width) interpolated.getRGB(x + 1, y) and 0xFF else left
                val avg = ((left + right) / 2).toInt()
                val newPixel = (0xFF shl 24) or (avg shl 16) or (avg shl 8) or avg
                interpolated.setRGB(x, y, newPixel)
            }
        }

        // Interpolate vertical pixels
        for (y in 1 until height step 2) {
            for (x in 0 until width) {
                val top = interpolated.getRGB(x, y - 1) and 0xFF
                val bottom = if (y + 1 < height) interpolated.getRGB(x, y + 1) and 0xFF else top
                val avg = ((top + bottom) / 2).toInt()
                val newPixel = (0xFF shl 24) or (avg shl 16) or (avg shl 8) or avg
                interpolated.setRGB(x, y, newPixel)
            }
        }

        return interpolated
    }

    fun embedData(cover: BufferedImage, message: String): BufferedImage? {
        try {
            // Step 1: Scale down the cover image
            val scaledDown = scaleDownImage(cover)

            // Step 2: Interpolate to create cover image
            val interpolated = interpolateImage(scaledDown)

            // Step 3: Convert message to bits
            val bits = textToBitsWithMarker(message)
            var bitIndex = 0

            // Step 4: Create stego image
            val stego =
                BufferedImage(interpolated.width, interpolated.height, BufferedImage.TYPE_BYTE_GRAY)

            // Copy all pixels first
            for (y in 0 until interpolated.height) {
                for (x in 0 until interpolated.width) {
                    stego.setRGB(x, y, interpolated.getRGB(x, y))
                }
            }

            // Step 5: Embed data in interpolated pixels (avoiding anchor points)
            val raster = stego.raster
            for (y in 0 until stego.height) {
                for (x in 0 until stego.width) {
                    // Only embed in interpolated pixels (not in anchor points)
                    if (x % 2 != 0 || y % 2 != 0) {
                        val originalValue = raster.getSample(x, y, 0)
                        val newValue = if (bitIndex < bits.size) {
                            (originalValue and 0xFE) or bits[bitIndex++] // Replace LSB
                        } else {
                            originalValue
                        }
                        raster.setSample(x, y, 0, newValue)
                    }
                }
            }

            return stego
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun extractData(stego: BufferedImage): String {
        try {
            val bits = mutableListOf<Int>()
            val raster = stego.raster

            // Extract from interpolated pixels only
            for (y in 0 until stego.height) {
                for (x in 0 until stego.width) {
                    if (x % 2 != 0 || y % 2 != 0) { // Only check interpolated pixels
                        val value = raster.getSample(x, y, 0)
                        bits.add(value and 1) // Get LSB
                    }
                }
            }

            return bitsToTextWithMarker(bits)
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }
}