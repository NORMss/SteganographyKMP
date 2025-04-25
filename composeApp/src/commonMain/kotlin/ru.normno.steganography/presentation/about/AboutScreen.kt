package ru.normno.steganography.presentation.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ru.normno.steganography.presentation.multi.MultiState

@Composable
fun MultiScreen(
    state: MultiState,
    modifier: Modifier = Modifier,
    onPickImages: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
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
        LazyRow(
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
                        .size(32.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AsyncImage(
                        model = state.sourceFilesInfo[it].byteArray,
                        contentDescription = null,
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
                    )
                }
            }
        }
    }
}