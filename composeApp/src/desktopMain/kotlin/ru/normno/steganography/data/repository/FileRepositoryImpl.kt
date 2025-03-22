package ru.normno.steganography.data.repository

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.saveImageToGallery
import ru.normno.steganography.domain.model.FileInfo
import ru.normno.steganography.domain.repository.FileRepository
import java.io.FileInputStream

class FileRepositoryImpl(
    private val fileKit: FileKit,
) : FileRepository {
    override suspend fun getImage(): FileInfo? {
        val file = fileKit.openFilePicker(
            mode = FileKitMode.Single,
            type = FileKitType.Image,
        )?.file
        return file?.let { file ->
            FileInfo(
                filename = file.name,
                byteArray = file.let {
                    FileInputStream(file).use {
                        it.readBytes()
                    }
                }
            )
        }
    }

    override suspend fun saveImage(filename: String, byteArray: ByteArray) {
        fileKit.saveImageToGallery(byteArray, filename)
    }
}