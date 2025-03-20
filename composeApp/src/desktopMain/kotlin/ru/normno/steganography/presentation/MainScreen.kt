package ru.normno.steganography.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import coil3.compose.AsyncImage

@Composable
fun MainScreen(
    onPickImage: () -> Unit,
    state: MainState,
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            onClick = {
                onPickImage()
            }
        ) {
            Text(
                text = "Pick image"
            )
        }
        state.imageBytes?.let { imageBytes ->
            Text(
                text = "File size ${imageBytes.size / 1024} kb"
            )
        }
        AsyncImage(
            model = state.imageBytes,
            contentDescription = null,
        )
    }
}