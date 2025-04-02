package ru.normno.steganography.util

object TextManager {
    val endMarker = byteArrayOf(0xFE.toByte(), 0x00, 0xFF.toByte(), 0xFA.toByte())

    fun textToBitsWithMarker(text: String): List<Int> {
        val data = text.toByteArray(Charsets.UTF_8) + endMarker
        return data.flatMap { byte ->
            (7 downTo 0).map { bit -> (byte.toInt() shr bit) and 1 }
        }
    }

    fun bitsToTextWithMarker(bits: List<Int>): String {
        val bytes = bits.chunked(8).map { chunk ->
            chunk.fold(0) { acc, bit -> (acc shl 1) or bit }.toByte()
        }.toByteArray()

        val markerIndex = bytes.indices.find { i ->
            i + endMarker.size <= bytes.size && bytes.copyOfRange(i, i + endMarker.size)
                .contentEquals(endMarker)
        } ?: -1

        return if (markerIndex >= 0) {
            String(bytes.copyOfRange(0, markerIndex), Charsets.UTF_8)
        } else {
            ""
        }
    }
}