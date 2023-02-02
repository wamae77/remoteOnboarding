package com.cll.remoteonboarding.data.remote

import com.cll.remoteonboarding.data.network.RemoteOnbBoardingApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnBoardingRepositoryImpl @Inject constructor(
    private val onbBoardingApi: RemoteOnbBoardingApi
) : OnBoardingRepository {

    override suspend fun enrollNewUser(
        id: Int,
        onSuccess: () -> Unit,
        onFailed: (Any?) -> Unit
    ) {
        onbBoardingApi.enrollUser().enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    onSuccess.invoke()
                }
                TODO("Not yet implemented")
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                t.printStackTrace()
                onFailed.invoke(t.message ?: "")
            }

        })
    }
}