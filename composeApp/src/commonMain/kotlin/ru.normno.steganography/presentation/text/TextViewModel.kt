package ru.normno.steganography.presentation.text

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.normno.steganography.util.StegoTextMethod
import ru.normno.steganography.util.steganography.CyrillicLatinStego
import ru.normno.steganography.util.steganography.WhitespaceSteganography
import ru.normno.steganography.util.steganography.ZeroWidthSteganography

class TextViewModel : ViewModel() {
    val state: StateFlow<TextState>
        field = MutableStateFlow(TextState())

    fun setSecretText(text: String) {
        state.update {
            it.copy(
                secretText = text,
            )
        }
    }

    fun setOriginalText(text: String) {
        state.update {
            it.copy(
                originalText = text,
            )
        }
    }

    fun onSelectMethod(method: StegoTextMethod) {
        state.update {
            it.copy(
                selectedStegoMethod = method,
            )
        }
    }

    fun onEmbed() {
        viewModelScope.launch {
            when (state.value.selectedStegoMethod) {
                StegoTextMethod.Whitespace -> {
                    whitespaceSteganographyEmbed()
                }

                StegoTextMethod.ZeroWidth -> {
                    zeroWidthSteganographyEmbed()
                }

                StegoTextMethod.CyrillicLatin -> {
                    cyrillicLatinSteganographyEmbed()
                }
            }
        }
    }

    fun onExtract() {
        viewModelScope.launch {
            when (state.value.selectedStegoMethod) {
                StegoTextMethod.Whitespace -> {
                    whitespaceSteganographyExtract()
                }

                StegoTextMethod.ZeroWidth -> {
                    zeroWidthSteganographyExtract()
                }

                StegoTextMethod.CyrillicLatin -> {
                    cyrillicLatinSteganographyExtract()
                }
            }
        }
    }

    private suspend fun cyrillicLatinSteganographyEmbed() {
        embedData(CyrillicLatinStego::encode)
    }

    private suspend fun cyrillicLatinSteganographyExtract() {
        extractData(CyrillicLatinStego::decode)
    }

    private suspend fun whitespaceSteganographyEmbed() {
        embedData(WhitespaceSteganography::encode)
    }

    private suspend fun whitespaceSteganographyExtract() {
        extractData(WhitespaceSteganography::decode)
    }

    private suspend fun zeroWidthSteganographyEmbed() {
        embedData(ZeroWidthSteganography::encode)
    }

    private suspend fun zeroWidthSteganographyExtract() {
        extractData(ZeroWidthSteganography::decode)
    }

    private suspend fun embedData(
        method: (message: String, coverText: String) -> String,
    ) {
        method(
            state.value.secretText,
            state.value.originalText,
        ).also { textWithSecret ->
            state.update {
                it.copy(
                    textWithSecret = textWithSecret,
                )
            }
        }
    }

    private suspend fun extractData(method: (String) -> String) {
        method(
            state.value.secretText,
        ).also { extractedSecretText ->
            state.update {
                it.copy(
                    extractedSecretText = extractedSecretText,
                ).also {
                    println("extractedSecretText: ${state.value.extractedSecretText}")
                }
            }
        }
    }
}