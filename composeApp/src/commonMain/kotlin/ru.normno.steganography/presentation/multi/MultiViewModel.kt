package ru.normno.steganography.presentation.multi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import ru.normno.steganography.domain.model.FileInfo
import ru.normno.steganography.domain.model.SecretFile
import ru.normno.steganography.domain.model.TestInfo
import ru.normno.steganography.domain.repository.FileRepository
import ru.normno.steganography.presentation.home.MainState
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
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class MultiViewModel(
    private val fileRepository: FileRepository,
) : ViewModel() {
    private val kjb = KJB(0.5, 1)
    private val lsbmr = LSBMatchingRevisited()
    private val inmi = INMI()
    private val imnp = IMNP()

    val state: StateFlow<MultiState>
        field = MutableStateFlow(MultiState())

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

    fun onPickSourceImages() {
        viewModelScope.launch(Dispatchers.IO) {
            val fileInfo = fileRepository.getImages()
            state.update {
                it.copy(
                    sourceFilesInfo = fileInfo,
                )
            }
        }
    }

    fun onSaveModifiedImages() {
        viewModelScope.launch(Dispatchers.IO) {
            state.value.resultFilesInfo.map { resultFileInfo ->
                fileRepository.saveImage(
                    FileInfo(
                        filename = resultFileInfo.filename + ".${state.value.selectedImageFormat.formatName.lowercase()}",
                        byteArray = resultFileInfo.byteArray,
                    )
                )
            }
        }
    }

    fun onSaveModifiedImage(fileInfo: FileInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            fileRepository.saveImage(
                FileInfo(
                    filename = fileInfo.filename + ".${state.value.selectedImageFormat.formatName.lowercase()}",
                    byteArray = fileInfo.byteArray,
                )
            )
        }
    }

    fun onExtractAndSaveTexts() {
        viewModelScope.launch(Dispatchers.IO) {
            onExtractData()
            onSaveExtractedTexts()
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
            visualAttack()
            compute()
        }
    }

    fun onAnalysis() {
        viewModelScope.launch(Dispatchers.Default) {
            compute()
        }
    }

    private suspend fun onExtractData() {
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

    private suspend fun onSaveExtractedTexts() {
        fileRepository.saveTextToFile(
            filename = "DDMMYYYY_HHmmss".format(Clock.System.now()) + "_images_${state.value.resultFilesInfo.size}",
            text = Json.encodeToString(state.value.extractText),
        )
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
        state.value.sourceFilesInfo.forEachIndexed { index, sourceFileInfo ->
            state.value.resultFilesInfo[index].let { resultFileInfo ->
                val cover = byteArrayToImage(sourceFileInfo.byteArray)
                val stego = byteArrayToImage(resultFileInfo.byteArray)
                state.update {
                    it.copy(
                        testsInfo = state.value.testsInfo + TestInfo(
                            psnrTotaldBm = Compute.computePSNR(cover, stego),
                            capacityTotalKb = computeCapacity(cover) / 8.0,
                            rsTotal = RSAnalysis.doAnalysis(stego).toList(),
                            chiSquareTotal = Compute.chiSquareTest(stego, 16),
                            aumpTotal = Compute.aumpTest(stego, 4, 2),
                            compressionTotal = Compute.compressionAnalysis(cover, stego),
                        )
                    )
                }
            }
        }
        println(state.value.testsInfo.first())
    }

    private fun visualAttack() {
        state.value.sourceFilesInfo.forEachIndexed { index, sourceFileInfo ->
            state.value.resultFilesInfo[index].let { resultFileInfo ->
                Compute.visualAttack(
                    coverImage = byteArrayToImage(sourceFileInfo.byteArray),
                    stegoImage = byteArrayToImage(resultFileInfo.byteArray),
                ).also { visualAttack ->
                    state.update {
                        it.copy(
                            visualAttackFilesInfo = state.value.visualAttackFilesInfo + FileInfo(
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
        state.value.sourceFilesInfo.map { file ->
            file.let { sourceFileInfo ->
                val cover = byteArrayToImage(sourceFileInfo.byteArray)
                embedMethod(cover, state.value.embedText)
                    ?.let { bufferedImage ->
                        val image = imageToByteArray(
                            image = bufferedImage,
                            format = state.value.selectedImageFormat
                        )
                        state.update {
                            it.copy(
                                resultFilesInfo = state.value.resultFilesInfo + FileInfo(
                                    filename = sourceFileInfo.filename.substringBeforeLast(".")
                                            + "_modified",
                                    byteArray = image,
                                )
                            )
                        }
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
        state.value.resultFilesInfo.map { file ->
            file.let { resultFileInfo ->
                val stegoImage = byteArrayToImage(resultFileInfo.byteArray)
                extractMethod(stegoImage).also { text ->
                    state.update {
                        it.copy(
                            extractText = state.value.extractText + SecretFile(
                                fileName = file.filename,
                                secretText = text,
                            ),
                        )
                    }
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