package com.cll.database.di

import android.content.Context
import androidx.room.Room
import com.cll.database.ApplicationDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun providesAppDataBase(@ApplicationContext context: Context): ApplicationDataBase {
        return Room.databaseBuilder(
            context, ApplicationDataBase::class.java, ApplicationDataBase.NAME
        ).build()
    }

    @Provides
    @Singleton
    fun providesPersonDao(applicationDataBase: ApplicationDataBase): com.cll.database.dao.PersonDao {
        return applicationDataBase.PersonDao()
    }

    @Provides
    @Singleton
    fun providesFingerPrintDao(applicationDataBase: ApplicationDataBase): com.cll.database.dao.FingerPrintDao {
        return applicationDataBase.FingerPrintDao()
    }

    @Provides
    @Singleton
    fun providesPersonsImageDao(applicationDataBase: ApplicationDataBase): com.cll.database.dao.PersonsImageDao {
        return applicationDataBase.PersonsImageDao()
    }
}