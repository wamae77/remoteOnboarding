package com.cll.remoteonboarding.data.remote

interface OnBoardingRepository {

    suspend fun enrollNewUser(
        id: Int,
        onSuccess: () -> Unit,
        onFailed: (Any?) -> Unit
    )
}