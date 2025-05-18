package ru.normno.steganography.util.steganography

object CyrillicLatinStego {

    private val cyrToLat = mapOf(
        'А' to 'A', 'В' to 'B', 'Е' to 'E',
        'К' to 'K', 'М' to 'M', 'Н' to 'H',
        'О' to 'O', 'Р' to 'P', 'С' to 'C',
        'Т' to 'T', 'Х' to 'X',
        'а' to 'a', 'е' to 'e', 'о' to 'o',
        'р' to 'p', 'с' to 'c', 'х' to 'x',
        'у' to 'y',
    )
    private val latToCyr = cyrToLat.entries.associate { (k, v) -> v to k }

    fun encode(message: String, coverText: String): String {
        val binary = messageToBinary(message)
        var bitIndex = 0

        val result = StringBuilder()
        for (char in coverText) {
            if (bitIndex >= binary.length) {
                result.append(char)
                continue
            }

            val bit = binary[bitIndex]

            val substitute = when {
                cyrToLat.containsKey(char) && bit == '1' -> {
                    cyrToLat[char].also { bitIndex++ }
                }

                latToCyr.containsKey(char) && bit == '0' -> {
                    latToCyr[char].also { bitIndex++ }
                }

                else -> char
            }

            result.append(substitute)
        }

        if (bitIndex < binary.length)
            error("There are not enough replacement characters to encode the entire message.")

        return result.toString()
    }

    fun decode(stegoText: String): String {
        val bits = stegoText.mapNotNull {
            when {
                latToCyr.containsKey(it) -> '1'
                cyrToLat.containsKey(it) -> '0'
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
