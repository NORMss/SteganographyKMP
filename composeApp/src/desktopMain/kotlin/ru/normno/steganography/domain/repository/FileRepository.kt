package ru.normno.steganography.domain.repository

interface FileRepository {
    suspend fun getImage(): ByteArray?
    suspend fun saveImage(folder: String, file: String, byteArray: ByteArray)
}