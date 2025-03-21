package ru.normno.steganography.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.normno.steganography.domain.repository.FileRepository
import ru.normno.steganography.util.KJBSteganography

class MainViewModel(
    private val fileRepository: FileRepository,
) : ViewModel() {
    private val kjbSteganography = KJBSteganography(1.0, 1)

    val state: StateFlow<MainState>
        field = MutableStateFlow(MainState())

    fun onPickImage() {
        viewModelScope.launch(Dispatchers.IO) {
            val imageBytes = fileRepository.getImage()
            state.update {
                it.copy(
                    imageBytes = imageBytes,
                )
            }
        }
    }
}