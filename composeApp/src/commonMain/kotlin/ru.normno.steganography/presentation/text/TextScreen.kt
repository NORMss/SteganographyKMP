package ru.normno.steganography.presentation.text

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.normno.steganography.util.StegoTextMethod

@Composable
fun TextScreen(
    state: TextState,
    modifier: Modifier = Modifier,
    onEmbed: () -> Unit,
    onExtract: () -> Unit,
    onSelectStegoMethod: (StegoTextMethod) -> Unit,
    setSecretText: (String) -> Unit,
    setOriginalText: (String) -> Unit,
) {
    var isSelectStegoMethod by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = modifier
            .fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Column(
            modifier = Modifier
                .weight(0.5f),
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
                    color = if (state.originalText.isNotBlank())
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
                    .height(8.dp),
            )
            DropdownMenu(
                expanded = isSelectStegoMethod,
                onDismissRequest = { isSelectStegoMethod = false },
            ) {
                listOf(
                    StegoTextMethod.Whitespace,
                    StegoTextMethod.ZeroWidth,
                    StegoTextMethod.CyrillicLatin,
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
            Spacer(
                modifier = Modifier
                    .height(8.dp),
            )
            Button(
                onClick = onEmbed,
            ) {
                Text(
                    text = "Embed Data"
                )
            }
            Spacer(
                modifier = Modifier
                    .height(8.dp),
            )
            Button(
                onClick = onExtract,
            ) {
                Text(
                    text = "Extract Data"
                )
            }
        }
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TextField(
                value = state.secretText,
                onValueChange = setSecretText,
                placeholder = {
                    Text(
                        text = "Secret text"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
            TextField(
                value = state.originalText,
                onValueChange = setOriginalText,
                placeholder = {
                    Text(
                        text = "Original text"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
        }
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TextField(
                value = state.extractedSecretText,
                onValueChange = {

                },
                placeholder = {
                    Text(
                        text = "Extracted secret text"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
            TextField(
                value = state.textWithSecret,
                onValueChange = {

                },
                placeholder = {
                    Text(
                        text = "Text with secret"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
        }
    }
}