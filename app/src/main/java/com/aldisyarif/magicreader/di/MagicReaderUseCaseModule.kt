package com.aldisyarif.magicreader.di

import com.aldisyarif.magicreader.repository.IMagicReaderRepository
import com.aldisyarif.magicreader.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object MagicReaderUseCaseModule {

    @Provides
    fun provideProcessImageUseCase(): ProcessImageUseCase =
        ProcessImageUseCase()

    @Provides
    fun providePostTextUseCase(
        repository: IMagicReaderRepository
    ): PostTextUseCase =
        PostTextUseCase(repository)

    @Provides
    fun provideUpdateTextUseCase(
        repository: IMagicReaderRepository
    ): UpdateTextUseCase =
        UpdateTextUseCase(repository)

    @Provides
    fun provideGetListTextNoteUseCase(
        repository: IMagicReaderRepository
    ): GetListTextNoteUseCase =
        GetListTextNoteUseCase(repository)

    @Provides
    fun provideDeleteTextUseCase(
        repository: IMagicReaderRepository
    ): DeleteTextUseCase =
        DeleteTextUseCase(repository)
}