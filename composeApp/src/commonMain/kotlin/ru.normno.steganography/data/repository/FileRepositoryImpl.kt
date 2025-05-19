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
import ru.normno.steganography.domain.model.TestInfo
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
                byteArray = file.readBytes(),
            )
        }
    }

    override suspend fun getImages(): List<FileInfo> {
        return fileKit.openFilePicker(
            mode = FileKitMode.Multiple(),
            type = FileKitType.Image,
        )?.map { file ->
            FileInfo(
                filename = file.name,
                byteArray = file.readBytes(),
            )
        } ?: emptyList()
    }

    override suspend fun saveImage(fileInfo: FileInfo) {
        fileKit.saveImageToGallery(fileInfo.byteArray, fileInfo.filename)
    }

    override suspend fun saveImages(images: List<FileInfo>) {
        images.map { image ->
            fileKit.saveImageToGallery(image.byteArray, image.filename)
        }
    }

    override suspend fun saveTextToFile(filename: String, text: String) {
        fileKit.openFileSaver(
            filename,
            extension = "txt",
        ).also { file ->
            file?.write(text.toByteArray())
        }
    }

    override suspend fun saveTestInfoToCsv(
        filename: String,
        data: List<TestInfo>
    ) {
        val headers = listOf(
            "psnrTotaldBm", "rsTotal", "chiSquareTotal",
            "aumpTotal", "compressionTotal", "capacityTotalKb"
        )

        val lines = buildList {
            add(headers.joinToString(","))
            data.forEach { item ->
                val row = listOf(
                    item.psnrTotaldBm?.toString() ?: "",
                    item.rsTotal.joinToString(";"),  // чтобы не путаться с запятой-разделителем CSV
                    item.chiSquareTotal?.toString() ?: "",
                    item.aumpTotal?.toString() ?: "",
                    item.compressionTotal?.toString() ?: "",
                    item.capacityTotalKb?.toString() ?: ""
                )
                add(row.joinToString(","))
            }
        }

        fileKit.openFileSaver(
            filename,
            "csv",
        ).also { file ->
            file.write(lines)
        }
    }
}