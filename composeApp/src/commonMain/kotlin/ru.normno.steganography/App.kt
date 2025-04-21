@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package ru.normno.steganography

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import ru.normno.steganography.presentation.MainScreen
import ru.normno.steganography.presentation.MainViewModel

@Preview
@Composable
fun App() {
    val viewModel = koinViewModel<MainViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    MaterialTheme {
        val navigator = rememberSupportingPaneScaffoldNavigator<Nothing>()

        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
        ) { paddingValues ->
            SupportingPaneScaffold(
                directive = navigator.scaffoldDirective,
                scaffoldState = navigator.scaffoldState,
                mainPane = {
                    AnimatedPane(
                        modifier = Modifier
                            .safeContentPadding()
                            .background(Color.Red),
                    ) {
                        MainScreen(
                            state = state,
                            onAnalysis = viewModel::onAnalysis,
                            onPickSourceImage = viewModel::onPickSourceImage,
                            onPickModifiedImage = viewModel::onPickModifiedImage,
                            onEmbedData = viewModel::onEmbedData,
                            onExtractData = viewModel::onExtractData,
                            onSaveModifiedImage = viewModel::onSaveModifiedImage,
                            setEmbedText = viewModel::setEmbedText,
                            setFileName = viewModel::setFileName,
                            onSelectImageFormat = viewModel::onSelectImageFormat,
                            onSaveExtractedText = viewModel::onSaveExtractedText,
                            onSelectStegoMethod = viewModel::onSelectStegoMethod,
                            onRecoverOriginalImageINMI = viewModel::onRecoverOriginalImageINMI,
                            modifier = Modifier
                                .padding(paddingValues),
                        )
                    }
                },
                supportingPane = {},
                modifier = Modifier
                    .padding(paddingValues),
                extraPane = {},
            )
        }
    }
}