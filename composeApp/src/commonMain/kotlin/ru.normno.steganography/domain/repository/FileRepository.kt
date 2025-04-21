package ru.normno.steganography.domain.repository

import ru.normno.steganography.domain.model.FileInfo

interface FileRepository {
    suspend fun getImage(): FileInfo?
    suspend fun getImages(): List<FileInfo>
    suspend fun saveImage(fileInfo: FileInfo)
    suspend fun saveImages(images: List<FileInfo>)
    suspend fun saveTextToFile(filename: String, text: String)
}