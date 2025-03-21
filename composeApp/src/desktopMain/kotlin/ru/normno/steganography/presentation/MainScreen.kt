@file:OptIn(ExperimentalFoundationApi::class)

package ru.normno.steganography.presentation

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun MainScreen(
    onPickImage: () -> Unit,
    state: MainState,
) {
    var textEmbedding by remember {
        mutableStateOf("")
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TextField(
                    value = textEmbedding,
                    onValueChange = {
                        textEmbedding = it
                    },
                    minLines = 5,
                    maxLines = 10,
                    label = {
                        Text(
                            text = "Text for embedding"
                        )
                    },
                )
                Button(
                    onClick = {

                    }
                ) {
                    Text(
                        text = "Embed text"
                    )
                }
            }
            Spacer(
                modifier = Modifier
                    .width(16.dp),
            )
            Column(
                modifier = Modifier
                    .weight(2f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                        .aspectRatio(1f)
                        .heightIn(max = 720.dp)
                        .fillMaxSize()
                        .onClick {
                            onPickImage()
                        },
                ) {
                    Text(
                        text = "Click to select photo",
                        modifier = Modifier
                            .align(Alignment.Center),
                    )
                    AsyncImage(
                        model = state.imageBytes,
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .graphicsLayer {
                                clip = true
                            },
                    )
                }
                Text(
                    text = state.imageBytes?.let { "File size ${it.size / 1024} kb" } ?: ""
                )
            }
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    val state = MainState(imageBytes = byteArrayOf(1, 2, 3))
    MainScreen(onPickImage = {
    }, state = state)
}