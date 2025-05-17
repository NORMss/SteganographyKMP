package ru.normno.steganography.nvgraph

import kotlinx.serialization.Serializable

@Serializable
sealed class Route {
    @Serializable
    data object Home : Route()

    @Serializable
    data object Multi : Route()

    @Serializable
    data object Text : Route()

    @Serializable
    data object About : Route()
}