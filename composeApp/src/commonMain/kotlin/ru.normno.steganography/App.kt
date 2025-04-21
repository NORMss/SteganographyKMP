@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package ru.normno.steganography

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
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
        val navigationSuiteState = rememberNavigationSuiteScaffoldState()
        var selectedItemIndex by remember {
            mutableIntStateOf(0)
        }
        val windowWithClass =
            currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
        ) { paddingValues ->
            NavigationSuiteScaffold(
                navigationSuiteItems = {

                },
                modifier = Modifier
                    .fillMaxSize(),
                state = navigationSuiteState,
                layoutType = if (windowWithClass == WindowWidthSizeClass.EXPANDED) {
                    NavigationSuiteType.NavigationDrawer
                } else {
                    NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(
                        currentWindowAdaptiveInfo()
                    )
                }
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
        }
    }
}