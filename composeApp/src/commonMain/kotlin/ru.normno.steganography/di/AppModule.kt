package ru.normno.steganography.di

import io.github.vinceglb.filekit.FileKit
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import ru.normno.steganography.data.repository.FileRepositoryImpl
import ru.normno.steganography.domain.repository.FileRepository
import ru.normno.steganography.presentation.home.MainViewModel
import ru.normno.steganography.presentation.multi.MultiViewModel
import ru.normno.steganography.presentation.text.TextViewModel

object AppModule {
    fun initializeKoin() {
        startKoin {
            modules(appModule)
        }
    }

    private val appModule = module {
        single<FileRepository> { FileRepositoryImpl(fileKit = fileKit) }
        viewModelOf(::MainViewModel)
        viewModelOf(::MultiViewModel)
        viewModelOf(::TextViewModel)
    }

    private val fileKit = FileKit
}