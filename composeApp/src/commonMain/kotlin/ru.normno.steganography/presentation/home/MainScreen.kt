@file:OptIn(ExperimentalFoundationApi::class)

package ru.normno.steganography.presentation.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.jetbrains.compose.ui.tooling.preview.Preview
import ru.normno.steganography.domain.model.FileInfo
import ru.normno.steganography.util.ImageFormat
import ru.normno.steganography.util.StegoImageMethod

@Composable
fun MainScreen(
    state: MainState,
    onAnalysis: () -> Unit,
    onPickSourceImage: () -> Unit,
    onPickModifiedImage: () -> Unit,
    onEmbedData: () -> Unit,
    onExtractData: () -> Unit,
    onSaveModifiedImage: () -> Unit,
    onSelectImageFormat: (ImageFormat) -> Unit,
    onSelectStegoMethod: (StegoImageMethod) -> Unit,
    onRecoverOriginalImageINMI: () -> Unit,
    onSaveExtractedText: () -> Unit,
    setEmbedText: (String) -> Unit,
    setFileName: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isSelectImageFormat by remember {
        mutableStateOf(false)
    }
    var isSelectStegoMethod by remember {
        mutableStateOf(false)
    }

    var isCheckedSwitch by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp,
            ),
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
                    enabled = !state.isEbbing && state.sourceFileInfo != null
                ) {
                    Text(
                        text = "Embed text"
                    )
                }
                Spacer(
                    modifier = Modifier
                        .height(8.dp),
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (state.sourceFileInfo != null) {
                                    isSelectImageFormat = true
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = state.selectedImageFormat.formatName,
                            color = if (state.sourceFileInfo != null)
                                MaterialTheme.colorScheme.onBackground
                            else
                                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(
                            modifier = Modifier
                                .width(4.dp),
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier
                                .rotate(if (isSelectImageFormat) 180f else 0f),
                        )
                    }
                    DropdownMenu(
                        expanded = isSelectImageFormat,
                        onDismissRequest = { isSelectImageFormat = false },
                    ) {
                        listOf(
                            ImageFormat.PNG(),
                            ImageFormat.JPG(),
                            ImageFormat.JPEG(),
                            ImageFormat.BMP(),
                        ).forEach { format ->
                            DropdownMenuItem(
                                onClick = {
                                    onSelectImageFormat(format)
                                    isSelectImageFormat = false
                                },
                                text = {
                                    Text(text = format.formatName)
                                },
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                isSelectStegoMethod = true
                            },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = state.selectedStegoImageMethod::class.simpleName ?: "Unknown",
                            color = if (state.sourceFileInfo != null)
                                MaterialTheme.colorScheme.onBackground
                            else
                                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(
                            modifier = Modifier
                                .width(4.dp),
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier
                                .rotate(if (isSelectStegoMethod) 180f else 0f),
                        )
                    }
                    Spacer(
                        modifier = Modifier
                            .height(8.dp)
                    )
                    DropdownMenu(
                        expanded = isSelectStegoMethod,
                        onDismissRequest = { isSelectStegoMethod = false },
                    ) {
                        listOf(
                            StegoImageMethod.KJB,
                            StegoImageMethod.LSBMR,
                            StegoImageMethod.INMI,
                            StegoImageMethod.IMNP,
                        ).forEach { method ->
                            DropdownMenuItem(
                                onClick = {
                                    onSelectStegoMethod(method)
                                    isSelectStegoMethod = false
                                },
                                text = {
                                    Text(text = method::class.simpleName ?: "Unknown")
                                }
                            )
                        }
                    }
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
                Text(
                    text = state.sourceFileInfo?.filename ?: "",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Box(
                    modifier = Modifier
                        .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                        .aspectRatio(1f)
                        .heightIn(max = 720.dp)
                        .fillMaxSize()
                        .clickable {
                            onPickSourceImage()
                        },
                ) {
                    Text(
                        text = "Click to select photo",
                        modifier = Modifier
                            .align(Alignment.Center),
                    )
                    AsyncImage(
                        model = state.sourceFileInfo?.byteArray,
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
                    text = state.sourceFileInfo?.byteArray?.let { "File size ${it.size / 1024} kb" }
                        ?: ""
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
                Text(
                    text = state.resultFileInfo?.filename ?: "",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Box(
                    modifier = Modifier
                        .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                        .aspectRatio(1f)
                        .heightIn(max = 720.dp)
                        .fillMaxSize()
                        .clickable {
                            onPickModifiedImage()
                        },
                ) {
                    Text(
                        text = "Click to select an image with embedded data",
                        modifier = Modifier
                            .align(Alignment.Center),
                    )
                    AsyncImage(
                        model = if (isCheckedSwitch) state.visualAttackFileInfo?.byteArray else state.resultFileInfo?.byteArray,
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .graphicsLayer {
                                clip = true
                            },
                    )
                    if (state.resultFileInfo != null && state.selectedStegoImageMethod is StegoImageMethod.INMI) {
                        IconButton(
                            onClick = {
                                onRecoverOriginalImageINMI()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                            )
                        }
                    }
                    Switch(
                        checked = isCheckedSwitch,
                        onCheckedChange = {
                            isCheckedSwitch = !isCheckedSwitch
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd),
                    )
                }
                Text(
                    text = state.resultFileInfo?.byteArray?.let { "File size ${it.size / 1024} kb" }
                        ?: ""
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
                        .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8))
                        .fillMaxWidth()
                        .heightIn(min = 128.dp, max = 256.dp)
                        .verticalScroll(rememberScrollState())
                        .padding(8.dp),
                ) {
                    Text(
                        text = state.extractText.ifBlank { "Extracted text" },
                        color = if (state.extractText.isBlank())
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        else
                            MaterialTheme.colorScheme.primary,
                    )
                }
                Spacer(
                    modifier = Modifier
                        .height(8.dp),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Button(
                        modifier = Modifier
                            .weight(1f),
                        onClick = onExtractData,
                        enabled = state.resultFileInfo != null && !state.isExtracting,
                    ) {
                        Text(
                            text = "Extract text"
                        )
                    }
                    IconButton(
                        onClick = onSaveExtractedText,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null,
                        )
                    }
                }
                Spacer(
                    modifier = Modifier
                        .height(8.dp),
                )
                TextField(
                    value = state.resultFileInfo?.filename ?: "",
                    onValueChange = setFileName,
                    enabled = state.resultFileInfo != null,
                    modifier = Modifier
                        .fillMaxWidth(),
                    singleLine = true,
                )
                Spacer(
                    modifier = Modifier
                        .height(8.dp),
                )
                Button(
                    onClick = onSaveModifiedImage,
                    enabled = state.resultFileInfo != null,
                ) {
                    Text(
                        text = "Save"
                    )
                }
                Spacer(
                    modifier = Modifier
                        .height(8.dp),
                )
                Button(
                    onClick = onAnalysis,
                    enabled = state.resultFileInfo != null,
                ) {
                    Text(
                        text = "Analysis"
                    )
                }
                if (state.testInfo != null) {
                    Spacer(
                        modifier = Modifier
                            .height(8.dp),
                    )
                    Text(
                        text = ("Maximum capacity: ${"%.2f".format(state.testInfo.capacityTotalKb)} Kb\n" +
                                "PSNR: ${"%.2f".format(state.testInfo.psnrTotaldBm)} dBm\n" +
                                "RS: ${
                                    state.testInfo.rsTotal.joinToString(
                                        ", ",
                                        transform = { "%.2f".format(it) })
                                } " +
                                "ChiSquare: ${"%.2f".format(state.testInfo.chiSquareTotal)}\n" +
                                "Aump: ${"%.2f".format(state.testInfo.aumpTotal)}\n" +
                                "Compression: ${"%.2f".format(state.testInfo.compressionTotal)}").also {
                            println(it)
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    val state = MainState(
        sourceFileInfo = FileInfo("TestName.png", byteArrayOf()),
        resultFileInfo = FileInfo("TestName.png", byteArrayOf()),
    )
    MainScreen(
        state = state,
        onAnalysis = {},
        onPickSourceImage = {},
        onPickModifiedImage = {},
        onEmbedData = {},
        onExtractData = {},
        onSelectImageFormat = {},
        onSelectStegoMethod = {},
        setEmbedText = {},
        setFileName = {},
        onSaveModifiedImage = {},
        onRecoverOriginalImageINMI = {},
        onSaveExtractedText = {},
    )
}