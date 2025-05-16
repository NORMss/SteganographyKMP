package ru.normno.steganography.presentation.text

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TextViewModel : ViewModel() {
    val state: StateFlow<TextState>
        field = MutableStateFlow(TextState())
}