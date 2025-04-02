package ru.normno.steganography

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import ru.normno.steganography.presentation.MainScreen
import ru.normno.steganography.presentation.MainViewModel

@Composable
fun App() {
    val viewModel = koinViewModel<MainViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    MaterialTheme {
        MainScreen(
            state = state,
            onPickSourceImage = viewModel::onPickSourceImage,
            onPickModifiedImage = viewModel::onPickModifiedImage,
            onEmbedData = viewModel::onEmbedData,
            onExtractData = viewModel::onExtractData,
            onSaveModifiedImage = viewModel::onSaveModifiedImage,
            setEmbedText = viewModel::setEmbedText,
            setFileName = viewModel::setFileName,
            onSelectImageFormat = viewModel::onSelectImageFormat,
            onSelectStegoMethod = viewModel::onSelectStegoMethod,
            onRecoverOriginalImageINMI = viewModel::onRecoverOriginalImageINMI,
        )
    }
}