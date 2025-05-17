package ru.normno.steganography.util.steganography

object ZeroWidthSteganography {

    private const val ZWSP = '\u200B' // zero-width space = 0
    private const val ZWNJ = '\u200C' // zero-width non-joiner = 1

    fun encode(message: String, coverText: String): String {
        val binary = messageToBinary(message)
        val hidden = binary.map { if (it == '0') ZWSP else ZWNJ }.joinToString("")
        return coverText + hidden
    }

    fun decode(stegoText: String): String {
        val hidden = stegoText.takeLastWhile { it == ZWSP || it == ZWNJ }
        val bits = hidden.map {
            when (it) {
                ZWSP -> '0'
                ZWNJ -> '1'
                else -> error("Invalid character")
            }
        }.joinToString("")
        return binaryToMessage(bits)
    }

    private fun messageToBinary(message: String): String =
        message.toByteArray().joinToString("") {
            it.toString(2).padStart(8, '0')
        }

    private fun binaryToMessage(binary: String): String =
        binary.chunked(8)
            .map { it.toInt(2).toChar() }
            .joinToString("")
}