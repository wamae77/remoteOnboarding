package com.cll.database.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    fun bindsFingerPrintRepository(
        fingerPrintRepositoryImpl: com.cll.database.repositories.FingerPrintRepositoryImpl
    ): com.cll.database.repositories.FingerPrintRepository

    @Binds
    fun bindsPersonsImageRepository(
        personsImagesRepositoryImpl: com.cll.database.repositories.PersonsImagesRepositoryImpl
    ): com.cll.database.repositories.PersonsImagesRepository
}