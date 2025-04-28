package ru.normno.steganography.presentation.multi

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ru.normno.steganography.util.ImageFormat
import ru.normno.steganography.util.StegoMethod

@Composable
fun MultiScreen(
    state: MultiState,
    modifier: Modifier = Modifier,
    onPickImages: () -> Unit,
    onSelectImageFormat: (ImageFormat) -> Unit,
    onSelectStegoMethod: (StegoMethod) -> Unit,
) {
    var isSelectImageFormat by remember {
        mutableStateOf(false)
    }
    var isSelectStegoMethod by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Column {
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
            }
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
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(8.dp),
                ) {
                    items(
                        count = state.sourceFilesInfo.size,
                        key = {
                            state.sourceFilesInfo[it].filename
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainer)
                                .height(64.dp)
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            AsyncImage(
                                model = state.sourceFilesInfo[it].byteArray,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .aspectRatio(1f),
                            )
                            Spacer(
                                modifier = Modifier
                                    .width(4.dp),
                            )
                            Text(
                                text = state.sourceFilesInfo[it].filename,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            }
        }
    }
}