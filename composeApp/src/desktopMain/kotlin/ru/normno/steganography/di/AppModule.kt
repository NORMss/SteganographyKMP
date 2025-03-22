package ru.normno.steganography.di

import io.github.vinceglb.filekit.FileKit
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import ru.normno.steganography.data.file.FileManager
import ru.normno.steganography.data.repository.FileRepositoryImpl
import ru.normno.steganography.domain.repository.FileRepository
import ru.normno.steganography.presentation.MainViewModel

object AppModule {
    fun initializeKoin() {
        startKoin {
            modules(appModule)
        }
    }

    private val appModule = module {
        single<FileRepository> { FileRepositoryImpl(fileKit = fileKit, fileManager = fileManager) }
        viewModelOf(::MainViewModel)
    }

    private val fileKit = FileKit
    private val fileManager = FileManager()
}