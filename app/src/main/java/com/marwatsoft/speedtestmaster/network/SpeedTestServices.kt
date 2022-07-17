package com.marwatsoft.speedtestmaster.network

import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET

interface SpeedTestServices {

    @GET("api/android/config.php")
    suspend fun getServers():Response<ResponseBody>
}