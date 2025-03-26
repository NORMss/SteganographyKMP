package ru.normno.steganography.util

sealed interface StegoMethod {
    object KJB : StegoMethod
    object LSBMR : StegoMethod
    object INMI: StegoMethod
}