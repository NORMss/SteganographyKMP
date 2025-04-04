package ru.normno.steganography.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.normno.steganography.domain.model.FileInfo
import ru.normno.steganography.domain.repository.FileRepository
import ru.normno.steganography.util.ImageFormat
import ru.normno.steganography.util.ImageManager.byteArrayToImage
import ru.normno.steganography.util.ImageManager.imageToByteArray
import ru.normno.steganography.util.StegoMethod
import ru.normno.steganography.util.steganography.Compute
import ru.normno.steganography.util.steganography.Compute.computeCapacity
import ru.normno.steganography.util.steganography.IMNP
import ru.normno.steganography.util.steganography.INMI
import ru.normno.steganography.util.steganography.KJB
import ru.normno.steganography.util.steganography.LSBMatchingRevisited
import ru.normno.steganography.util.steganography.RSAnalysis
import java.awt.image.BufferedImage

class MainViewModel(
    private val fileRepository: FileRepository,
) : ViewModel() {
    private val kjb = KJB(0.5, 1)
    private val lsbmr = LSBMatchingRevisited()
    private val inmi = INMI()
    private val imnp = IMNP()

    val state: StateFlow<MainState>
        field = MutableStateFlow(MainState())

    init {
        state
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000L),
                MainState(),
            )
    }

    fun onSelectImageFormat(format: ImageFormat) {
        state.update {
            it.copy(
                selectedImageFormat = format,
            )
        }
    }

    fun onSelectStegoMethod(stegoMethod: StegoMethod) {
        state.update {
            it.copy(
                selectedStegoMethod = stegoMethod,
            )
        }
    }

    fun setEmbedText(text: String) {
        state.update {
            it.copy(
                embedText = text,
            )
        }
    }

    fun setFileName(text: String) {
        state.update {
            it.copy(
                resultFileInfo = it.resultFileInfo?.copy(filename = text)
            )
        }
    }

    fun onPickSourceImage() {
        viewModelScope.launch(Dispatchers.IO) {
            val fileInfo = fileRepository.getImage()
            state.update {
                it.copy(
                    sourceFileInfo = fileInfo,
                    selectedImageFormat = fileInfo?.filename?.let { filename ->
                        when (filename.substringAfterLast(".").uppercase()) {
                            "PNG" -> ImageFormat.PNG()
                            "JPEG" -> ImageFormat.JPEG()
                            "JPG" -> ImageFormat.JPG()
                            "BMP" -> ImageFormat.BMP()
                            else -> ImageFormat.PNG()
                        }
                    } ?: ImageFormat.PNG(),
                )
            }
        }
    }

    fun onRecoverOriginalImageINMI() {
        viewModelScope.launch(Dispatchers.IO) {
            state.value.resultFileInfo?.let { resultFileInfo ->
                byteArrayToImage(resultFileInfo.byteArray).also { image ->
                    state.update {
                        it.copy(
                            resultFileInfo = resultFileInfo.copy(
                                byteArray = imageToByteArray(
                                    image = inmi.recoverOriginalImage(image),
                                    format = state.value.selectedImageFormat,
                                )
                            ),
                        )
                    }
                }
            }
            compute()
        }
    }

    fun onPickModifiedImage() {
        viewModelScope.launch(Dispatchers.IO) {
            val fileInfo = fileRepository.getImage()
            state.update {
                it.copy(
                    resultFileInfo = fileInfo
                )
            }
        }
    }

    fun onSaveModifiedImage() {
        viewModelScope.launch(Dispatchers.IO) {
            state.value.resultFileInfo?.let { resultFileInfo ->
                fileRepository.saveImage(
                    filename = resultFileInfo.filename + ".${state.value.selectedImageFormat.formatName.lowercase()}",
                    byteArray = resultFileInfo.byteArray,
                )
            }
        }
    }

    fun onEmbedData() {
        viewModelScope.launch(Dispatchers.Default) {
            when (state.value.selectedStegoMethod) {
                StegoMethod.KJB -> {
                    embedDataKJB()
                }

                StegoMethod.LSBMR -> {
                    embedDataLSBMR()
                }

                StegoMethod.INMI -> {
                    embedDataINMI()
                }

                StegoMethod.IMNP -> {
                    embedDataIMNP()
                }
            }
            compute()
            visualAttack()
        }
    }

    fun onAnalysis() {
        viewModelScope.launch(Dispatchers.Default) {
            compute()
        }
    }

    fun onVisualAttack() {
        visualAttack()
    }

    fun onExtractData() {
        viewModelScope.launch(Dispatchers.Default) {
            when (state.value.selectedStegoMethod) {
                StegoMethod.KJB -> {
                    extractDataKJB()
                }

                StegoMethod.LSBMR -> {
                    extractDataLSBMR()
                }

                StegoMethod.INMI -> {
                    extractDataINMI()
                }

                StegoMethod.IMNP -> {
                    extractDataIMNP()
                }
            }
        }
    }

    private suspend fun embedDataKJB() {
        embedData(kjb::embedData)
    }

    private suspend fun embedDataLSBMR() {
        embedData(lsbmr::embedData)
    }

    private suspend fun embedDataINMI() {
        embedData(inmi::embedData)
    }

    private suspend fun embedDataIMNP() {
        embedData(imnp::embedData)
    }

    private suspend fun extractDataKJB() {
        extractData(kjb::extractData)
    }

    private suspend fun extractDataLSBMR() {
        extractData(lsbmr::extractData)
    }

    private suspend fun extractDataINMI() {
        extractData(inmi::extractData)
    }

    private suspend fun extractDataIMNP() {
        extractData(imnp::extractData)
    }

    private fun compute() {
        state.value.sourceFileInfo?.let { sourceFileInfo ->
            state.value.resultFileInfo?.let { resultFileInfo ->
                val cover = byteArrayToImage(sourceFileInfo.byteArray)
                val stego = byteArrayToImage(resultFileInfo.byteArray)

                state.update {
                    it.copy(
                        psnrTotaldBm = Compute.computePSNR(cover, stego),
                        capacityTotalKb = computeCapacity(cover) / 8.0,
                        rsTotal = RSAnalysis.analyze(stego),
                        chiSquareTotal = Compute.chiSquareTest(stego, 4),
                        aumpTotal = Compute.aumpTest(stego, 4, 2),
                        compressionTotal = Compute.compressionAnalysis(cover, stego),
                    )
                }
            }
        }
    }

    private fun visualAttack() {
        state.value.sourceFileInfo?.let { sourceFileInfo ->
            state.value.resultFileInfo?.let { resultFileInfo ->
                Compute.visualAttack(
                    coverImage = byteArrayToImage(sourceFileInfo.byteArray),
                    stegoImage = byteArrayToImage(resultFileInfo.byteArray),
                ).also { visualAttack ->
                    state.update {
                        it.copy(
                            visualAttackFileInfo = FileInfo(
                                filename = sourceFileInfo.filename + "_visual_attack",
                                byteArray = imageToByteArray(visualAttack)
                            )
                        )
                    }
                }
            }
        }
    }

    private suspend fun embedData(
        embedMethod: (BufferedImage, String) -> BufferedImage?,
    ) {
        state.update {
            it.copy(
                isEbbing = true,
            )
        }
        state.value.sourceFileInfo?.let { sourceFileInfo ->
            val cover = byteArrayToImage(sourceFileInfo.byteArray)
            embedMethod(cover, state.value.embedText)
                ?.also { bufferedImage ->
                    val image = imageToByteArray(
                        image = bufferedImage,
                        format = state.value.selectedImageFormat
                    )
                    state.update {
                        it.copy(
                            resultFileInfo = FileInfo(
                                filename = sourceFileInfo.filename.substringBeforeLast(".")
                                        + "_modified",
                                byteArray = image,
                            )
                        )
                    }
                }
        }
        state.update {
            it.copy(
                isEbbing = false,
            )
        }
    }

    private suspend fun extractData(
        extractMethod: (BufferedImage) -> String,
    ) {
        state.update {
            it.copy(
                isExtracting = true,
            )
        }
        state.value.resultFileInfo?.let { resultFileInfo ->
            val stegoImage = byteArrayToImage(resultFileInfo.byteArray)
            extractMethod(stegoImage).also { text ->
                state.update {
                    it.copy(
                        extractText = text,
                    )
                }
            }
        }
        state.update {
            it.copy(
                isExtracting = false,
            )
        }

    }
}