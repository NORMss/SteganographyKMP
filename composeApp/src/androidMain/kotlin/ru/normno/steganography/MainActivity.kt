package ru.normno.steganography

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init
import org.jetbrains.compose.ui.tooling.preview.Preview
import ru.normno.steganography.di.AppModule.initializeKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            initializeKoin()
            FileKit.init(this)
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}