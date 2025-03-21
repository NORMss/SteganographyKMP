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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun MainScreen(
    state: MainState,
    onPickImage: () -> Unit,
    onEmbedData: () -> Unit,
    onExtractData: () -> Unit,
    setEmbedText: (String) -> Unit,
) {
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
                    value = state.embedText,
                    onValueChange = setEmbedText,
                    minLines = 5,
                    maxLines = 10,
                    label = {
                        Text(
                            text = "Text for embedding"
                        )
                    },
                )
                Button(
                    onClick = onEmbedData,
                    enabled = !state.isEbbing && state.sourceImageBytes != null
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
                        model = state.sourceImageBytes,
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
                    text = state.sourceImageBytes?.let { "File size ${it.size / 1024} kb" } ?: ""
                )
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
                        .fillMaxSize(),
                ) {
                    Text(
                        text = "Image after data injection",
                        modifier = Modifier
                            .align(Alignment.Center),
                    )
                    AsyncImage(
                        model = state.resultImageBytes,
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
                    text = state.resultImageBytes?.let { "File size ${it.size / 1024} kb" } ?: ""
                )
            }
            Spacer(
                modifier = Modifier
                    .width(16.dp),
            )
            Column(
                modifier = Modifier
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    modifier = Modifier
                        .border(1.dp, MaterialTheme.colors.primary, RoundedCornerShape(8))
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .heightIn(min = 128.dp, max = 256.dp)
                        .padding(8.dp),
                ) {
                    Text(
                        text = state.extractText.ifBlank { "Extracted text" },
                        color = if (state.extractText.isBlank())
                            MaterialTheme.colors.primary.copy(alpha = 0.5f)
                        else
                            MaterialTheme.colors.primary,
                    )
                }
                Spacer(
                    modifier = Modifier
                        .height(8.dp),
                )
                Button(
                    onClick = onExtractData,
                    enabled = state.resultImageBytes != null && !state.isExtracting
                ) {
                    Text(
                        text = "Extract text"
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    val state = MainState(sourceImageBytes = byteArrayOf(1, 2, 3))
    MainScreen(
        state = state,
        onPickImage = {},
        onEmbedData = {},
        onExtractData = {},
        setEmbedText = {},
    )
}