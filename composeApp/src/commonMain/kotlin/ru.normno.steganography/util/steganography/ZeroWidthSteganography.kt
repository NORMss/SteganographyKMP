package ru.normno.steganography.util.steganography

object ZeroWidthSteganography {

    private val ZWSP = '\u200B' // 0
    private val ZWNJ = '\u200C' // 1

    fun encode(message: String, coverText: String): String {
        val bits = message.toByteArray(Charsets.UTF_8).joinToString("") {
            it.toUByte().toString(2).padStart(8, '0')
        }

        val result = StringBuilder()
        var bitIndex = 0

        var i = 0
        while (i < coverText.length) {
            val ch = coverText[i]
            if (ch == ' ' && bitIndex < bits.length) {
                val leftBit = bits[bitIndex++]
                val leftChar = if (leftBit == '1') ZWNJ else ZWSP

                val rightChar = if (bitIndex < bits.length) {
                    val rightBit = bits[bitIndex++]
                    if (rightBit == '1') ZWNJ else ZWSP
                } else null

                result.append(leftChar)
                result.append(' ')
                rightChar?.let { result.append(it) }
            } else {
                result.append(ch)
            }
            i++
        }

        return result.toString()
    }

    fun decode(stegoText: String): String {
        val bits = StringBuilder()
        var i = 0

        while (i < stegoText.length) {
            val ch = stegoText[i]
            if (ch == ZWSP || ch == ZWNJ) {
                val leftBit = if (ch == ZWNJ) '1' else '0'
                if (i + 2 < stegoText.length && stegoText[i + 1] == ' ' &&
                    (stegoText[i + 2] == ZWSP || stegoText[i + 2] == ZWNJ)
                ) {
                    val rightBit = if (stegoText[i + 2] == ZWNJ) '1' else '0'
                    bits.append(leftBit).append(rightBit)
                    i += 3
                } else {
                    bits.append(leftBit)
                    i++
                }
            } else {
                i++
            }
        }

        val bitString = bits.toString()
        val byteChunks = bitString.chunked(8).filter { it.length == 8 }
        val bytes = byteChunks.map { it.toInt(2).toByte() }.toByteArray()

        return bytes.toString(Charsets.UTF_8)
    }
}