package ru.normno.steganography.presentation

data class MainState(
    val sourceImageBytes: ByteArray? = null,
    val resultImageBytes: ByteArray? = null,
    val embedText: String = "",
    val extractText: String = "",
    val isEbbing: Boolean = false,
    val isExtracting: Boolean = false,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MainState

        if (isEbbing != other.isEbbing) return false
        if (isExtracting != other.isExtracting) return false
        if (!sourceImageBytes.contentEquals(other.sourceImageBytes)) return false
        if (!resultImageBytes.contentEquals(other.resultImageBytes)) return false
        if (embedText != other.embedText) return false
        if (extractText != other.extractText) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isEbbing.hashCode()
        result = 31 * result + isExtracting.hashCode()
        result = 31 * result + (sourceImageBytes?.contentHashCode() ?: 0)
        result = 31 * result + (resultImageBytes?.contentHashCode() ?: 0)
        result = 31 * result + embedText.hashCode()
        result = 31 * result + extractText.hashCode()
        return result
    }
}