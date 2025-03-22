package ru.normno.steganography.domain.model

data class FileInfo(
    val filename: String = "",
    val byteArray: ByteArray = byteArrayOf(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FileInfo

        if (filename != other.filename) return false
        if (!byteArray.contentEquals(other.byteArray)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = filename.hashCode()
        result = 31 * result + byteArray.contentHashCode()
        return result
    }
}