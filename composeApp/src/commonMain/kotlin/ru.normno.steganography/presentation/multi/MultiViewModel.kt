package ru.normno.steganography.presentation.multi

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
import ru.normno.steganography.presentation.home.MainState
import ru.normno.steganography.util.ImageFormat
import ru.normno.steganography.util.StegoMethod

class MultiViewModel(
    private val fileRepository: FileRepository,
) : ViewModel() {
    val state: StateFlow<MultiState>
        field = MutableStateFlow(MultiState())

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

    fun onSelectStegoMethod(stegoMethod: StegoMethod) {
        state.update {
            it.copy(
                selectedStegoMethod = stegoMethod,
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

    fun onPickSourceImages() {
        viewModelScope.launch(Dispatchers.IO) {
            val fileInfo = fileRepository.getImages()
            state.update {
                it.copy(
                    sourceFilesInfo = fileInfo,
                )
            }
        }
    }
}