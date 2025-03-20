package ru.normno.steganography.data.repository

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import ru.normno.steganography.domain.repository.FileRepository
import java.io.FileInputStream

class FileRepositoryImpl(
    private val fileKit: FileKit,
) : FileRepository {
    override suspend fun getImage(): ByteArray? {
        val file = fileKit.openFilePicker(
            mode = FileKitMode.Single,
            type = FileKitType.Image,
        )?.file
        return file?.let {
            FileInputStream(file).use {
                it.readBytes()
            }
        }
    }
}