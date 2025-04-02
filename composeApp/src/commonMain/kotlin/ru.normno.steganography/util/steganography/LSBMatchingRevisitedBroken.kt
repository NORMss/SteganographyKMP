package ru.normno.steganography.util.steganography

import ru.normno.steganography.util.TextManager.bitsToTextWithMarker
import ru.normno.steganography.util.TextManager.textToBitsWithMarker
import java.awt.image.BufferedImage

class LSBMatchingRevisitedBroken {
    private fun f(yi: Int, yiPlus: Int): Int {
        // Усложнённая функция для надёжного извлечения
        return (yi xor yiPlus) and 1
    }

    private fun adjustPixel(pixel: Int, targetBit: Int): Int {
        // Гарантированно изменяем LSB пикселя
        return if ((pixel and 1) != targetBit) {
            pixel xor 1  // Инвертируем младший бит
        } else {
            pixel
        }
    }

    fun embedData(cover: BufferedImage, message: String): BufferedImage {
        val bits = textToBitsWithMarker(message)
        val result = BufferedImage(cover.width, cover.height, cover.type)
        result.graphics.drawImage(cover, 0, 0, null)

        var bitIndex = 0
        for (y in 0 until cover.height) {
            for (x in 0 until cover.width step 2) {
                if (bitIndex >= bits.size) return result

                // Получаем текущие пиксели
                val rgb1 = result.getRGB(x, y)
                val pixel1 = rgb1 and 0xFF
                val pixel2 = if (x + 1 < cover.width) result.getRGB(x + 1, y) and 0xFF else 0

                // Бит для встраивания
                val m1 = bits[bitIndex++]
                val m2 = if (bitIndex < bits.size) bits[bitIndex++] else 0

                // Модифицируем первый пиксель
                val newPixel1 = adjustPixel(pixel1, m1)
                result.setRGB(x, y, (rgb1 and 0xFFFFFF00.toInt()) or newPixel1)

                // Модифицируем второй пиксель для соответствия функции f
                if (x + 1 < cover.width) {
                    val rgb2 = result.getRGB(x + 1, y)
                    var newPixel2 = pixel2
                    if (f(newPixel1, newPixel2) != m2) {
                        newPixel2 = adjustPixel(newPixel2, m2 xor f(newPixel1, newPixel2))
                    }
                    result.setRGB(x + 1, y, (rgb2 and 0xFFFFFF00.toInt()) or newPixel2)
                }
            }
        }
        return result
    }

    fun extractData(stego: BufferedImage): String {
        val bits = mutableListOf<Int>()

        for (y in 0 until stego.height) {
            for (x in 0 until stego.width step 2) {
                val pixel1 = stego.getRGB(x, y) and 0xFF
                val pixel2 = if (x + 1 < stego.width) stego.getRGB(x + 1, y) and 0xFF else 0

                bits.add(pixel1 and 1)  // LSB первого пикселя
                bits.add(f(pixel1, pixel2))  // Результат функции f
            }
        }

        return bitsToTextWithMarker(bits)
    }
}