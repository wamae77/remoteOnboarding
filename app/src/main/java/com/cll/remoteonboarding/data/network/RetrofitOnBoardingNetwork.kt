package com.cll.remoteonboarding.data.network

import com.cll.remoteonboarding.BuildConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Singleton
object RetrofitOnBoardingNetwork {

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(2, TimeUnit.MINUTES)
        .readTimeout(1, TimeUnit.MINUTES)
        .writeTimeout(1, TimeUnit.MINUTES)
        .build()

    private val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    val remoteOnbBoardingApi: RemoteOnbBoardingApi = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL_ONBOARDING)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(client)
        .build()
        .create(RemoteOnbBoardingApi::class.java)

    val iprsApi: IprsApi = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL_IPRS)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(client)
        .build()
        .create(IprsApi::class.java)

    val tech5MbAPApi: Tech5MbAPApi =  Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL_TECH5MBAP)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(client)
        .build()
        .create(Tech5MbAPApi::class.java)
}