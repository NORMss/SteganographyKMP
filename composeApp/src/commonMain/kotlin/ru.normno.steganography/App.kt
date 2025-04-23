@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package ru.normno.steganography

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview
import ru.normno.steganography.presentation.navigator.Navigator

@Preview
@Composable
fun App() {
    MaterialTheme {
        Navigator()
    }
}