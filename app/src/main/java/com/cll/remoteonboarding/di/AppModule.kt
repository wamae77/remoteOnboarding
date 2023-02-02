package com.cll.remoteonboarding.di

import com.cll.remoteonboarding.data.network.IprsApi
import com.cll.remoteonboarding.data.network.RemoteOnbBoardingApi
import com.cll.remoteonboarding.data.network.RetrofitOnBoardingNetwork
import com.cll.remoteonboarding.data.network.Tech5MbAPApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


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


}