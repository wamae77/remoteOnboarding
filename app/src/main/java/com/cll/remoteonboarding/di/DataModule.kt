package com.cll.remoteonboarding.di

import com.cll.remoteonboarding.data.local.repositories.FingerPrintRepository
import com.cll.remoteonboarding.data.local.repositories.FingerPrintRepositoryImpl
import com.cll.remoteonboarding.data.local.repositories.PersonsImagesRepository
import com.cll.remoteonboarding.data.local.repositories.PersonsImagesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    fun bindsFingerPrintRepository(
        fingerPrintRepositoryImpl: FingerPrintRepositoryImpl
    ): FingerPrintRepository

    @Binds
    fun bindsPersonsImageRepository(
        personsImagesRepositoryImpl: PersonsImagesRepositoryImpl
    ): PersonsImagesRepository
}