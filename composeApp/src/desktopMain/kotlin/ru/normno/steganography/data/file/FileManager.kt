package ru.normno.steganography.data.file

import java.io.File

class FileManager {
    suspend fun saveFile(folder: String, file: String, byteArray: ByteArray) {
        val folder = File(folder)
        val file = File(file)
        if (!folder.exists()) {
            folder.mkdirs()
        }
        file.createNewFile()
        file.writeBytes(byteArray)
    }
}