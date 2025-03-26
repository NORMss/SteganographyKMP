package ru.normno.steganography.util

import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

object ImageManager {
    fun imageToByteArray(image: BufferedImage, format: ImageFormat = ImageFormat.PNG()): ByteArray {
        val baos = ByteArrayOutputStream()
        ImageIO.write(image, format.formatName, baos)
        return baos.toByteArray()
    }

    fun byteArrayToImage(bytes: ByteArray): BufferedImage {
        val bais = ByteArrayInputStream(bytes)
        return ImageIO.read(bais)
    }
}