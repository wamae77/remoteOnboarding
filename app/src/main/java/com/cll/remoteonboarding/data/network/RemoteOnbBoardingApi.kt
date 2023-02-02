package com.cll.remoteonboarding.data.network

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RemoteOnbBoardingApi {

    @POST("customer/create")
    suspend fun enrollUser():Call<String>
}