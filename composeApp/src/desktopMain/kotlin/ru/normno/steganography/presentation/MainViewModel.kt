package ru.normno.steganography.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.normno.steganography.domain.model.FileInfo
import ru.normno.steganography.domain.repository.FileRepository
import ru.normno.steganography.util.ImageFormat
import ru.normno.steganography.util.KJBSteganography

class MainViewModel(
    private val fileRepository: FileRepository,
) : ViewModel() {
    private val kjbSteganography = KJBSteganography(0.5, 1)

    val state: StateFlow<MainState>
        field = MutableStateFlow(MainState())

    init {
        state
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000L),
                MainState(),
            )
    }

    fun onSelectImageFormat(format: ImageFormat) {
        state.update {
            it.copy(
                selectedImageFormat = format,
            )
        }
    }

    fun setEmbedText(text: String) {
        state.update {
            it.copy(
                embedText = text,
            )
        }
    }

    fun setFileName(text: String) {
        state.update {
            it.copy(
                resultFileInfo = it.resultFileInfo?.copy(filename = text)
            )
        }
    }

    fun onPickSourceImage() {
        viewModelScope.launch(Dispatchers.IO) {
            val fileInfo = fileRepository.getImage()
            state.update {
                it.copy(
                    sourceFileInfo = fileInfo,
                    selectedImageFormat = fileInfo?.filename?.let { filename ->
                        when (filename.substringAfterLast(".").uppercase()) {
                            "PNG" -> ImageFormat.PNG()
                            "JPEG" -> ImageFormat.JPEG()
                            "JPG" -> ImageFormat.JPG()
                            else -> ImageFormat.PNG()
                        }
                    } ?: ImageFormat.PNG(),
                )
            }
        }
    }

    fun onPickModifiedImage() {
        viewModelScope.launch(Dispatchers.IO) {
            val fileInfo = fileRepository.getImage()
            state.update {
                it.copy(
                    resultFileInfo = fileInfo
                )
            }
        }
    }

    fun onEmbedData() {
        viewModelScope.launch(Dispatchers.Default) {
            state.update {
                it.copy(
                    isEbbing = true,
                )
            }
            state.value.sourceFileInfo?.let { sourceFileInfo ->
                val cover = kjbSteganography.byteArrayToImage(sourceFileInfo.byteArray)
                kjbSteganography.embedData(cover = cover, message = state.value.embedText)
                    ?.also { bufferedImage ->
                        val image = kjbSteganography.imageToByteArray(
                            image = bufferedImage,
                            format = state.value.selectedImageFormat
                        )
                        state.update {
                            it.copy(
                                resultFileInfo = FileInfo(
                                    filename = sourceFileInfo.filename.substringBeforeLast(".")
                                            + "_modified",
                                    byteArray = image,
                                )
                            )
                        }
                    }
                onSaveModifiedImage()
            }
            state.update {
                it.copy(
                    isEbbing = false,
                )
            }
        }
    }

    fun onExtractData() {
        viewModelScope.launch(Dispatchers.Default) {
            state.update {
                it.copy(
                    isExtracting = true,
                )
            }
            state.value.resultFileInfo?.let { resultFileInfo ->
                val stegoImage = kjbSteganography.byteArrayToImage(resultFileInfo.byteArray)
                kjbSteganography.extractData(stegoImage).also { text ->
                    state.update {
                        it.copy(
                            extractText = text,
                        )
                    }
                }
            }
            state.update {
                it.copy(
                    isExtracting = false,
                )
            }
        }
    }

    fun onSaveModifiedImage() {
        viewModelScope.launch(Dispatchers.IO) {
            state.value.resultFileInfo?.let { resultFileInfo ->
                fileRepository.saveImage(
                    filename = resultFileInfo.filename + ".${state.value.selectedImageFormat.formatName.lowercase()}",
                    byteArray = resultFileInfo.byteArray,
                )
            }
        }
    }
}