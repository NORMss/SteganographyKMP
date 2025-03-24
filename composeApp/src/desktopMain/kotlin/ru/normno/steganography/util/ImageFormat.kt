package ru.normno.steganography.util

sealed class ImageFormat(val formatName: String) {
    class PNG : ImageFormat("PNG")
    class JPG : ImageFormat("JPG")
    class JPEG : ImageFormat("JPEG")
    class BMP : ImageFormat("BMP")
}