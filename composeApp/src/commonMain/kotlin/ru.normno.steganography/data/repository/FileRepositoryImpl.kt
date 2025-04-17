package ru.normno.steganography.data.repository

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.dialogs.openFileSaver
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.saveImageToGallery
import io.github.vinceglb.filekit.write
import ru.normno.steganography.domain.model.FileInfo
import ru.normno.steganography.domain.repository.FileRepository

class FileRepositoryImpl(
    private val fileKit: FileKit,
) : FileRepository {
    override suspend fun getImage(): FileInfo? {
        return fileKit.openFilePicker(
            mode = FileKitMode.Single,
            type = FileKitType.Image,
        )?.let { file ->
            FileInfo(
                filename = file.name,
                byteArray = file.readBytes()
            )
        }
    }

    override suspend fun saveImage(filename: String, byteArray: ByteArray) {
        fileKit.saveImageToGallery(byteArray, filename)
    }

    override suspend fun saveTextToFile(filename: String, text: String) {
        fileKit.openFileSaver(
            filename,
            extension = "txt",
        ).also { file ->
            file?.write(text.toByteArray())
        }
    }
}