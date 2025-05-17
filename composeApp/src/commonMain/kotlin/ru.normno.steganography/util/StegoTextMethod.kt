package ru.normno.steganography.util

sealed class StegoTextMethod {
    object Whitespace : StegoTextMethod()
    object ZeroWidth : StegoTextMethod()
}