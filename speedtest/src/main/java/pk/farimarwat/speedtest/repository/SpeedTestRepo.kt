package pk.farimarwat.speedtest.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

import okhttp3.ResponseBody
import pk.farimarwat.speedtest.network.SpeedTestServices
import retrofit2.Response


class SpeedTestRepo (val appServices: SpeedTestServices) {
    suspend fun getServersPremium(): Flow<Response<ResponseBody>> = flow {
        emit(appServices.getServersPremium())
    }.flowOn(Dispatchers.IO)

    suspend fun getServersPublic(): Flow<Response<ResponseBody>> = flow {
        emit(appServices.getServersPublic())
    }.flowOn(Dispatchers.IO)

    suspend fun getProvider():Flow<Response<ResponseBody>> = flow {
        emit(appServices.getProvider())
    }.flowOn(Dispatchers.IO)
}