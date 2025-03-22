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
import ru.normno.steganography.domain.repository.FileRepository
import ru.normno.steganography.util.KJBSteganography
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

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

    fun setEmbedText(text: String) {
        state.update {
            it.copy(
                embedText = text,
            )
        }
    }

    fun onPickImage() {
        viewModelScope.launch(Dispatchers.IO) {
            val imageBytes = fileRepository.getImage()
            state.update {
                it.copy(
                    sourceImageBytes = imageBytes,
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
            val cover = ByteArrayInputStream(state.value.sourceImageBytes).let {
                ImageIO.read(it)
            }
            kjbSteganography.embedData(cover = cover, message = state.value.embedText)
                .also { bufferedImage ->
                    val baos = ByteArrayOutputStream()
                    ImageIO.write(bufferedImage, "PNG", baos).run {
                        state.update {
                            it.copy(
                                resultImageBytes = baos.toByteArray(),
                            )
                        }
                    }
                }
            onSaveFile()
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
            state.value.resultImageBytes?.let { stegoBytes ->
                val stegoImage = kjbSteganography.byteArrayToImage(stegoBytes)
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

    fun onSaveFile() {
        viewModelScope.launch(Dispatchers.IO) {
            state.value.resultImageBytes?.let { imageBytes ->
                fileRepository.saveImage(
                    folder = "",
                    file = "photo.png",
                    byteArray = imageBytes,
                )
            }
        }
    }
}