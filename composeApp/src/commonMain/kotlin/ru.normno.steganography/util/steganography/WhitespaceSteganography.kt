package ru.normno.steganography.util.steganography

object WhitespaceSteganography {

    fun encode(message: String, coverText: String): String {
        val binary = messageToBinary(message)
        val lines = coverText.lines()
        val embeddedLines = lines.mapIndexed { index, line ->
            if (index < binary.length) {
                val hiddenChar = if (binary[index] == '0') ' ' else '\t'
                line + hiddenChar
            } else line
        }
        return embeddedLines.joinToString("\n")
    }

    fun decode(stegoText: String): String {
        val bits = stegoText.lines().mapNotNull { line ->
            when (line.lastOrNull()) {
                ' ' -> '0'
                '\t' -> '1'
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
