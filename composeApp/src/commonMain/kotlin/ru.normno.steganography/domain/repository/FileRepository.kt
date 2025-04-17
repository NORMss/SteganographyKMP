package ru.normno.steganography.domain.repository

import ru.normno.steganography.domain.model.FileInfo

interface FileRepository {
    suspend fun getImage(): FileInfo?
    suspend fun saveImage(filename: String, byteArray: ByteArray)
    suspend fun saveTextToFile(filename: String, text: String)
}