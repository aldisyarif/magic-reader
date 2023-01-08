package com.aldisyarif.magicreader.di

import com.aldisyarif.magicreader.repository.IMagicReaderRepository
import com.aldisyarif.magicreader.repository.MagicReaderRepositoryImpl
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MagicReaderRepositoryModule {

    @Provides
    @Singleton
    fun provideMagicReaderRepository(): IMagicReaderRepository {
        val instanceFirebase = FirebaseDatabase.getInstance()
        return MagicReaderRepositoryImpl(instanceFirebase)
    }
}