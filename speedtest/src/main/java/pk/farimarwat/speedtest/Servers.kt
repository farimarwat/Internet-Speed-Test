package pk.farimarwat.speedtest

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import org.jsoup.select.Elements
import pk.farimarwat.speedtest.models.STProvider
import pk.farimarwat.speedtest.models.STServer
import pk.farimarwat.speedtest.models.ServersResponse
import pk.farimarwat.speedtest.network.ApiStatus
import pk.farimarwat.speedtest.network.SpeedTestServices
import pk.farimarwat.speedtest.repository.SpeedTestRepo
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Servers private constructor(builder: Builder) {
    companion object {
        val BASE_URL_SPEEDTEST = "https://www.speedtest.net/"
        val SERVERS_PREMIUM = "premium"
        val Servers_PUBLIC = "public"
    }

    private var mServersType = Servers_PUBLIC

    class Builder() {
        private var mServersType = Servers_PUBLIC

        fun setServerType(type: String) = apply {
            this.mServersType = type
        }
        fun getServerType():String = this.mServersType
        fun build(): Servers = Servers(this)
    }


    init {
        mServersType = builder.getServerType()
    }

    fun listServers(listener: ServerStatusListener) {
        listener.onLoading()
        CoroutineScope(Dispatchers.IO).launch {
            val certificatePinner = CertificatePinner.Builder()
                .add("www.speedtest.net","sha256/AwZuXoBD+efE/tN4oCYOJ3EY+PNbizd4riD5eAWG3tQ=")
                .add("www.speedtest.net","sha256/FEzVOUp4dF3gI0ZVPRJhFbSJVXR+uQmMH65xhs1glH4=")
                .add("www.speedtest.net","sha256/Y9mvm0exBk1JoQ57f9Vm28jKo5lFm/woKcVxrYxu80o=")
                .build()
            val okHttpClient = OkHttpClient.Builder()
                .certificatePinner(certificatePinner)
                .build()
            val services = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BASE_URL_SPEEDTEST)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SpeedTestServices::class.java)
            val repo = SpeedTestRepo(services)
            when (mServersType) {
                SERVERS_PREMIUM -> {
                    repo.getServersPremium()
                        .catch { ex ->
                            listener.onError(ex.message.toString())
                        }
                        .collect { response ->
                            if (response.isSuccessful) {
                                val body = response.body()?.string()
                                val doc = Jsoup.parse(body, Parser.xmlParser())
                                val client = doc.select("client")
                                val stprovider = STProvider(
                                    client.attr("isp"),
                                    client.attr("providerName"),
                                    client.attr("lat"),
                                    client.attr("lon")
                                )
                                val servers = doc.getElementsByTag("server")
                                if (servers.isNotEmpty()) {
                                    val list = getServers(servers, stprovider)
                                    val resp = ServersResponse(
                                        stprovider,
                                        list
                                    )
                                    listener.onSuccess(resp)
                                } else {
                                    listener.onError("No servers found")
                                }
                            } else {
                                listener.onError(response.message().toString())
                            }
                        }
                }
                Servers_PUBLIC -> {
                    //First collecting Provider
                    var provider: STProvider? = null
                    val differed = CoroutineScope(Dispatchers.IO).async {
                        repo.getProvider()
                            .catch {
                                provider = null
                            }
                            .collect { response ->
                                if (response.isSuccessful) {
                                    val body = response.body()?.string()
                                    val doc = Jsoup.parse(body, Parser.xmlParser())
                                    val client = doc.select("client")
                                    provider = STProvider(
                                        client.attr("isp"),
                                        client.attr("isp"),
                                        client.attr("lat"),
                                        client.attr("lon")
                                    )
                                } else {
                                    provider = null
                                }
                            }
                        provider
                    }
                    provider = differed.await()
                    //End collecting provider

                    //Getting server list
                    repo.getServersPublic()
                        .catch { ex ->
                            listener.onError(ex.message.toString())
                        }
                        .collect { response ->
                            if (response.isSuccessful) {
                                val body = response.body()?.string()
                                val doc = Jsoup.parse(body, Parser.xmlParser())
                                val client = doc.select("client")
                                val servers = doc.getElementsByTag("server")
                                if (servers.isNotEmpty()) {
                                    val list = getServers(servers, provider)
                                    val resp = ServersResponse(
                                        provider,
                                        list
                                    )
                                    listener.onSuccess(resp)
                                } else {
                                    listener.onError("No servers found")
                                }
                            } else {
                                listener.onError(response.message().toString())
                            }
                        }
                    //End Getting Server List
                }
                else -> {
//First collecting Provider
                    var provider: STProvider? = null
                    val differed = CoroutineScope(Dispatchers.IO).async {
                        repo.getProvider()
                            .catch {
                                provider = null
                            }
                            .collect { response ->
                                if (response.isSuccessful) {
                                    val body = response.body()?.string()
                                    val doc = Jsoup.parse(body, Parser.xmlParser())
                                    val client = doc.select("client")
                                    provider = STProvider(
                                        client.attr("isp"),
                                        client.attr("isp"),
                                        client.attr("lat"),
                                        client.attr("lon")
                                    )
                                } else {
                                    provider = null
                                }
                            }
                        provider
                    }
                    provider = differed.await()
                    //End collecting provider

                    //Getting server list
                    repo.getServersPublic()
                        .catch { ex ->
                            listener.onError(ex.message.toString())
                        }
                        .collect { response ->
                            if (response.isSuccessful) {
                                val body = response.body()?.string()
                                val doc = Jsoup.parse(body, Parser.xmlParser())
                                val client = doc.select("client")
                                val servers = doc.getElementsByTag("server")
                                if (servers.isNotEmpty()) {
                                    val list = getServers(servers, provider)
                                    val resp = ServersResponse(
                                        provider,
                                        list
                                    )
                                    listener.onSuccess(resp)
                                } else {
                                    listener.onError("No servers found")
                                }
                            } else {
                                listener.onError(response.message().toString())
                            }
                        }
                    //End Getting Server List
                }
            }
            ApiStatus.Loading
        }
    }

    private fun getServers(servers: Elements, stProvider: STProvider?): List<STServer> {
        val list = mutableListOf<STServer>()
        for (item in servers) {
            val server = item.select("server")
            var url = server.attr("url")
            if(!url.contains("8080")){
                url = url.replace(":80", ":8080")
            }
            val stserver = STServer(
                url,
                server.attr("lat"),
                server.attr("lon"),
                server.attr("name"),
                server.attr("sponsor")
            )
            stProvider?.let {
                val from = LatLng(
                    stProvider.lat?.toDouble()!!,
                    stProvider.lon?.toDouble()!!
                )
                val to = LatLng(
                    stserver.lat?.toDouble()!!,
                    stserver.lon?.toDouble()!!
                )
                val distance = SphericalUtil.computeDistanceBetween(from, to) / 1000
                stserver.distance = distance.toInt()
            }
            list.add(stserver)
        }
        return list
    }

    interface ServerStatusListener {
        fun onLoading()
        fun onSuccess(response: ServersResponse)
        fun onError(error: String)
    }
}