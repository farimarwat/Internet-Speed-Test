package com.marwatsoft.speedtestmaster.ui.main

import android.app.Application
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.marwatsoft.speedtestmaster.R
import com.marwatsoft.speedtestmaster.SpeedTestLib.DownloadListener
import com.marwatsoft.speedtestmaster.SpeedTestLib.DownloadTest
import com.marwatsoft.speedtestmaster.model.*
import com.marwatsoft.speedtestmaster.network.ApiStatus
import com.marwatsoft.speedtestmaster.repository.SpeedTestRepo
import com.marwatsoft.speedtestmaster.utils.speedtest.HttpDownloadTest
import com.marwatsoft.speedtestmaster.utils.speedtest.HttpUploadTest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import pk.farimarwat.speedtest.Servers
import pk.farimarwat.speedtest.TestDownloader
import pk.farimarwat.speedtest.TestUploader
import pk.farimarwat.speedtest.models.*
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainFragmentViewModel @Inject constructor(
    private val mConnectivityManager: ConnectivityManager,
    private val mContext:Application
): ViewModel() {
    val mSTProvider by lazy { MutableLiveData<STProvider>(null) }
    val mListSTServer by lazy { MutableStateFlow<ApiStatus>(ApiStatus.Loading) }

   var mNetworkCallback:ConnectivityManager.NetworkCallback? = null
    val mSTServerSelected by lazy { MutableLiveData<STServer>(null) }

    val exp = CoroutineExceptionHandler { coroutineContext, throwable ->
        val msg = throwable.message
        msg?.let {
            mListSTServer.value = ApiStatus.Error(it)
        }
    }
    fun loadServers() = viewModelScope.launch(Dispatchers.IO + exp) {
        val serversbuilder = Servers.Builder()
            .setServerType(Servers.SERVERS_PREMIUM)
            .build()
        serversbuilder.listServers(object : Servers.ServerStatusListener{
            override fun onLoading() {
                mListSTServer.value = ApiStatus.Loading
            }

            override fun onSuccess(response: ServersResponse) {
                Timber.e("${response.provider}")
                response.servers?.let {
                    mListSTServer.value = ApiStatus.Success(it)
                }
            }

            override fun onError(error: String) {
                mListSTServer.value = ApiStatus.Error(
                    mContext.getString(R.string.error_internet)
                )
            }

        })
    }

    override fun onCleared() {
        super.onCleared()
        Timber.e("OnCleared")
        mNetworkCallback?.let {
            mConnectivityManager.unregisterNetworkCallback(it)
        }
    }
}