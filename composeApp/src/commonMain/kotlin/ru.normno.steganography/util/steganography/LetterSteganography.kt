package ru.normno.steganography.util.steganography

object LetterSteganography {

    private val letterMap = mapOf(
        'A' to Pair('A', 'А'), 'B' to Pair('B', 'В'), 'C' to Pair('C', 'С'), 'E' to Pair('E', 'Е'),
        'H' to Pair('H', 'Н'), 'K' to Pair('K', 'К'), 'M' to Pair('M', 'М'), 'O' to Pair('O', 'О'),
        'P' to Pair('P', 'Р'), 'T' to Pair('T', 'Т'), 'X' to Pair('X', 'Х'),
        'a' to Pair('a', 'а'), 'c' to Pair('c', 'с'), 'e' to Pair('e', 'е'),
        'o' to Pair('o', 'о'), 'p' to Pair('p', 'р'), 'x' to Pair('x', 'х')
    )

    private val reverseLetterMap = mutableMapOf<Char, Char>()

    init {
        for ((_, pair) in letterMap) {
            reverseLetterMap[pair.first] = '0'
            reverseLetterMap[pair.second] = '1'
        }
    }

    fun encode(message: String, coverText: String): String {
        val bits = message.toByteArray(Charsets.UTF_8).joinToString("") {
            it.toUByte().toString(2).padStart(8, '0')
        }

        val textChars = coverText.toCharArray()
        var bitIndex = 0

        for (i in textChars.indices) {
            val ch = textChars[i]
            if (ch in letterMap && bitIndex < bits.length) {
                val bit = bits[bitIndex++]
                val replacement = if (bit == '0') letterMap[ch]!!.first else letterMap[ch]!!.second
                textChars[i] = replacement
            }
            if (bitIndex >= bits.length) break
        }

        return textChars.concatToString()
    }

    fun decode(stegoText: String): String {
        val bits = StringBuilder()

        for (ch in stegoText) {
            if (reverseLetterMap.containsKey(ch)) {
                bits.append(reverseLetterMap[ch])
            }
        }

        val bitString = bits.toString()
        val byteChunks = bitString.chunked(8).filter { it.length == 8 }
        val bytes = byteChunks.map { it.toInt(2).toByte() }.toByteArray()

        return bytes.toString(Charsets.UTF_8)
    }
}
