package ru.normno.steganography.util.steganography

object WhitespaceSteganography {

    fun encode(message: String, coverText: String): String {
        val binary = messageToBinary(message)
        val words = coverText.split(" ")
        if (binary.length > words.size - 1)
            error("Cover text too short for this message!")

        val result = StringBuilder()
        for (i in 0 until words.size - 1) {
            result.append(words[i])
            val bit = if (i < binary.length) binary[i] else null
            when (bit) {
                '0' -> result.append(' ')
                '1' -> result.append('\t')
                else -> result.append(' ')
            }
        }
        result.append(words.last())
        return result.toString()
    }

    fun decode(stegoText: String): String {
        val tokens = stegoText.split(Regex("(?<=\\s)"))
        val bits = tokens.mapNotNull {
            when {
                it == " " -> '0'
                it == "\t" -> '1'
                else -> null
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
