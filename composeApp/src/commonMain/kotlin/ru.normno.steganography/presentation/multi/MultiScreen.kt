package ru.normno.steganography.presentation.multi

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ru.normno.steganography.domain.model.FileInfo
import ru.normno.steganography.presentation.multi.component.ImageCard
import ru.normno.steganography.util.ImageFormat
import ru.normno.steganography.util.StegoMethod

@Composable
fun MultiScreen(
    state: MultiState,
    modifier: Modifier = Modifier,
    onPickImages: () -> Unit,
    onEmbedData: () -> Unit,
    setEmbedText: (String) -> Unit,
    onSaveModifiedImage: (FileInfo) -> Unit,
    onSaveModifiedImages: () -> Unit,
    onExtractAndSaveTexts: () -> Unit,
    onSelectImageFormat: (ImageFormat) -> Unit,
    onSelectStegoMethod: (StegoMethod) -> Unit,
) {
    var isSelectImageFormat by remember {
        mutableStateOf(false)
    }
    var isSelectStegoMethod by remember {
        mutableStateOf(false)
    }

    val isSelectedVisualAttack = rememberSaveable {
        mutableStateListOf<String>()
    }

    var showTestInfo by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(
                onClick = onPickImages,
            ) {
                Icon(
                    imageVector = Icons.Default.ImageSearch,
                    contentDescription = null,
                )
                Spacer(
                    modifier = Modifier
                        .width(4.dp),
                )
                Text(
                    text = "Pick Images"
                )
            }
            Spacer(
                modifier = Modifier
                    .height(8.dp),
            )
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
                enabled = !state.isEbbing && state.sourceFilesInfo.isNotEmpty()
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
                            isSelectStegoMethod = true
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = state.selectedStegoMethod::class.simpleName ?: "Unknown",
                        color = if (state.sourceFilesInfo.isEmpty())
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
                        StegoMethod.KJB,
                        StegoMethod.LSBMR,
                        StegoMethod.INMI,
                        StegoMethod.IMNP,
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
                            if (state.sourceFilesInfo.isEmpty()) {
                                isSelectImageFormat = true
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = state.selectedImageFormat.formatName,
                        color = if (state.sourceFilesInfo.isEmpty())
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
            Spacer(
                modifier = Modifier
                    .height(8.dp),
            )
            Button(
                onClick = onExtractAndSaveTexts,
                enabled = state.resultFilesInfo.isNotEmpty(),
            ) {
                Text(
                    text = "Extract & Save"
                )
            }
            Spacer(
                modifier = Modifier
                    .height(8.dp),
            )
            Button(
                onClick = onSaveModifiedImages,
                enabled = state.resultFilesInfo.isNotEmpty(),
            ) {
                Text(
                    text = "Save Images"
                )
            }
        }
        Column(
            modifier = Modifier
                .weight(2f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = 512.dp)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(8.dp),
            ) {
                items(
                    count = state.sourceFilesInfo.size,
                    key = {
                        state.sourceFilesInfo[it].filename
                    }
                ) {
                    ImageCard(
                        image = state.sourceFilesInfo[it].byteArray,
                        filename = state.sourceFilesInfo[it].filename,
                    ) {

                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .weight(2f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = 512.dp)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(8.dp),
            ) {
                items(
                    count = state.resultFilesInfo.size,
                    key = {
                        state.resultFilesInfo[it].filename
                    }
                ) {
                    val imageSelected =
                        isSelectedVisualAttack.contains(state.resultFilesInfo[it].filename)
                    ImageCard(
                        image = if (imageSelected) state.visualAttackFilesInfo[it].byteArray else state.resultFilesInfo[it].byteArray,
                        filename = if (imageSelected) state.visualAttackFilesInfo[it].filename else state.resultFilesInfo[it].filename,
                    ) {
                        IconButton(
                            onClick = {
                                onSaveModifiedImage(state.resultFilesInfo[it])
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = null,
                            )
                        }
                        Spacer(
                            modifier = Modifier
                                .width(4.dp),
                        )
                        Switch(
                            checked = imageSelected,
                            onCheckedChange = { check ->
                                if (imageSelected)
                                    isSelectedVisualAttack.remove(state.resultFilesInfo[it].filename)
                                else
                                    isSelectedVisualAttack.add(state.resultFilesInfo[it].filename)
                            }
                        )
                        Spacer(
                            modifier = Modifier
                                .width(4.dp),
                        )
                        IconButton(
                            onClick = {
                                showTestInfo = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                            )
                        }

                    }
                    if (showTestInfo) {
                        Dialog(
                            onDismissRequest = {
                                showTestInfo = false
                            },
                        ) {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(16.dp),
                                ) {
                                    Text(
                                        text = ("Maximum capacity: ${"%.2f".format(state.testsInfo[it].capacityTotalKb)} Kb\n" +
                                                "PSNR: ${"%.2f".format(state.testsInfo[it].psnrTotaldBm)} dBm\n" +
                                                "RS: ${
                                                    state.testsInfo[it].rsTotal.joinToString(
                                                        ", ",
                                                        transform = { "%.2f".format(it) })
                                                } " +
                                                "ChiSquare: ${"%.2f".format(state.testsInfo[it].chiSquareTotal)}\n" +
                                                "Aump: ${"%.2f".format(state.testsInfo[it].aumpTotal)}\n" +
                                                "Compression: ${"%.2f".format(state.testsInfo[it].compressionTotal)}").also {
                                            println(it)
                                        }
                                    )

                                }
                            }
                        }
                    }

                }
            }
        }
    }
}