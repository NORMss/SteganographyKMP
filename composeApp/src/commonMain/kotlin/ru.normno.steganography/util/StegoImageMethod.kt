package ru.normno.steganography.util

sealed interface StegoImageMethod {
    object KJB : StegoImageMethod
    object LSBMR : StegoImageMethod
    object INMI: StegoImageMethod
    object IMNP: StegoImageMethod
}