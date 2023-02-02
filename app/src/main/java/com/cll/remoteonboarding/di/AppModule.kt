package com.cll.remoteonboarding.di

import android.content.Context
import androidx.room.Room
import com.cll.remoteonboarding.data.local.ApplicationDataBase
import com.cll.remoteonboarding.data.local.dao.FingerPrintDao
import com.cll.remoteonboarding.data.local.dao.PersonDao
import com.cll.remoteonboarding.data.local.dao.PersonsImageDao
import com.cll.remoteonboarding.data.network.IprsApi
import com.cll.remoteonboarding.data.network.RemoteOnbBoardingApi
import com.cll.remoteonboarding.data.network.RetrofitOnBoardingNetwork
import com.cll.remoteonboarding.data.network.Tech5MbAPApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun providesAppDataBase(@ApplicationContext context: Context): ApplicationDataBase {
        return Room.databaseBuilder(
            context, ApplicationDataBase::class.java, ApplicationDataBase.NAME
        ).build()
    }

    @Provides
    @Singleton
    fun providesRemoteOnbBoardingApi(): RemoteOnbBoardingApi {
        return RetrofitOnBoardingNetwork.remoteOnbBoardingApi
    }

    @Provides
    @Singleton
    fun providesIpRsApi(): IprsApi {
        return RetrofitOnBoardingNetwork.iprsApi
    }

    @Provides
    @Singleton
    fun providesTech5MBAPApi(): Tech5MbAPApi {
        return RetrofitOnBoardingNetwork.tech5MbAPApi
    }


    //Dao s

    @Provides
    @Singleton
    fun providesPersonDao(applicationDataBase: ApplicationDataBase): PersonDao {
        return applicationDataBase.PersonDao()
    }

    @Provides
    @Singleton
    fun providesFingerPrintDao(applicationDataBase: ApplicationDataBase): FingerPrintDao {
        return applicationDataBase.FingerPrintDao()
    }

    @Provides
    @Singleton
    fun providesPersonsImageDao(applicationDataBase: ApplicationDataBase):PersonsImageDao{
        return applicationDataBase.PersonsImageDao()
    }
}