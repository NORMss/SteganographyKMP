package ru.normno.steganography

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ru.normno.steganography.di.AppModule.initializeKoin

fun main() = application {
    initializeKoin()
    Window(
        onCloseRequest = ::exitApplication,
        title = "Steganography",
    ) {
        App()
    }
}