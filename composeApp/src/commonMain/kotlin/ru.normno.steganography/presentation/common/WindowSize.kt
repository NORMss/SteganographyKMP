package ru.normno.steganography.presentation.common

import androidx.compose.runtime.Composable

data class WindowSize(
    val with: WindowType,
    val height: WindowType,
)

enum class WindowType {
    Compact, Medium, Expanded
}

//@Composable
//fun rememberWindowSize(): WindowSize {
//    val configuration = LocalConfiguration.current
//
//    return WindowSize(
//        with = when {
//            configuration.screenWidthDp < 600 -> WindowType.Compact
//            configuration.screenWidthDp < 840 -> WindowType.Medium
//            else -> WindowType.Expanded
//        },
//        height = when {
//            configuration.screenHeightDp < 600 -> WindowType.Compact
//            configuration.screenHeightDp < 840 -> WindowType.Medium
//            else -> WindowType.Expanded
//        }
//    )
//}