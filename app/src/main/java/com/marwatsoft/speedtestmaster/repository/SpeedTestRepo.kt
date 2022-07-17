package com.marwatsoft.speedtestmaster.repository

import com.marwatsoft.speedtestmaster.network.SpeedTestServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.Dispatcher
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class SpeedTestRepo @Inject constructor(val appServices: SpeedTestServices) {
    suspend fun getServers(): Flow<Response<ResponseBody>> = flow {
        emit(appServices.getServers())
    }.flowOn(Dispatchers.IO)
}