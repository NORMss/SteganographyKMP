package ru.normno.steganography.domain.repository

interface FileRepository {
    suspend fun getImage(): ByteArray?
}