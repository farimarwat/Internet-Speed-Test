package pk.farimarwat.speedtest.network

import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET

interface SpeedTestServices {

    @GET("api/android/config.php")
    suspend fun getServersPremium():Response<ResponseBody>

    @GET("speedtest-servers-static.php")
    suspend fun getServersPublic():Response<ResponseBody>

    @GET("speedtest-config.php")
    suspend fun getProvider():Response<ResponseBody>
}