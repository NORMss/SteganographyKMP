package ru.normno.steganography.presentation.navigator

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.StackedBarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.StackedBarChart
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowWidthSizeClass
import org.koin.compose.viewmodel.koinViewModel
import ru.normno.steganography.nvgraph.Route
import ru.normno.steganography.presentation.about.AboutScreen
import ru.normno.steganography.presentation.home.MainScreen
import ru.normno.steganography.presentation.home.MainViewModel
import ru.normno.steganography.presentation.multi.MultiScreen
import ru.normno.steganography.presentation.multi.MultiViewModel

@Composable
fun Navigator() {
    val navController = rememberNavController()

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
                Screen.entries.forEachIndexed { index, screen ->
                    item(
                        selected = index == selectedItemIndex,
                        onClick = {
                            selectedItemIndex = index
                            when (index) {
                                0 -> navigateToTab(navController, Route.Home)
                                1 -> navigateToTab(navController, Route.Multi)
                                2 -> navigateToTab(navController, Route.About)
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (selectedItemIndex == index)
                                    screen.selectedIcon
                                else
                                    screen.unselectedIcon,
                                contentDescription = null,
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                            )
                        },
                    )
                }
            },
            modifier = Modifier
                .fillMaxSize(),
            layoutType = if (windowWithClass == WindowWidthSizeClass.EXPANDED) {
                NavigationSuiteType.NavigationDrawer
            } else {
                NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(
                    currentWindowAdaptiveInfo()
                )
            }
        ) {
            NavHost(
                navController = navController,
                startDestination = Route.Home,
            ) {
                composable<Route.Home> {
                    val viewModel = koinViewModel<MainViewModel>()
                    val state by viewModel.state.collectAsStateWithLifecycle()
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
                composable<Route.Multi> {
                    val viewModel = koinViewModel<MultiViewModel>()
                    val state by viewModel.state.collectAsStateWithLifecycle()
                    MultiScreen(
                        state = state,
                        onPickImages = viewModel::onPickSourceImages,
                        onSelectStegoMethod = viewModel::onSelectStegoMethod,
                        onSelectImageFormat = viewModel::onSelectImageFormat,
                        modifier = Modifier
                    )
                }
                composable<Route.About> {
                    AboutScreen()
                }
            }
        }
    }
}

private fun navigateToTab(
    navController: NavController,
    route: Route,
) {
    navController.navigate(route) {
        navController.graph.startDestinationRoute?.let { homeScreen ->
            popUpTo(homeScreen) {
                saveState = true
            }
            restoreState = true
            launchSingleTop = true
        }
    }
}


enum class Screen(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("Home", Icons.Default.Home, Icons.Outlined.Home),
    MULTI("Multi", Icons.Default.StackedBarChart, Icons.Outlined.StackedBarChart),
    ABOUT("About", Icons.Default.Info, Icons.Outlined.Info);
}