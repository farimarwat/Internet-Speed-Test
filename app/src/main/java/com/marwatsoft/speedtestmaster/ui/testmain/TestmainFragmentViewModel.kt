package com.marwatsoft.speedtestmaster.ui.testmain

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import com.marwatsoft.speedtestmaster.helpers.SettingsHelper
import com.marwatsoft.speedtestmaster.repository.SpeedTestRepo
import com.marwatsoft.speedtestmaster.utils.speedtest.HttpDownloadTest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import pk.farimarwat.speedtest.Ping
import pk.farimarwat.speedtest.TestDownloader
import pk.farimarwat.speedtest.TestUploader
import pk.farimarwat.speedtest.models.TESTTYPE_DOWNLOAD
import pk.farimarwat.speedtest.models.TESTTYPE_UPLOAD
import pk.farimarwat.speedtest.models.TestingStatus
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TestmainFragmentViewModel @Inject constructor(
    val mContext: Application,
    val mSettings:SettingsHelper
) : ViewModel() {
    val mTestingStatus by lazy { MutableStateFlow<TestingStatus>(TestingStatus.Testing(false, "")) }
    val mSpeed by lazy { MutableStateFlow(0.0) }
    var mTimeOut = 12
    var mConnectionType = SettingsHelper.CONNECTION_TYPE_MULTIPLE

    var mBuilderUpload: TestUploader? = null
    var mBuilderDownload: TestDownloader? = null
    val mEntryDownload by lazy { MutableLiveData<Entry>(null) }
    val mEntryUpload by lazy { MutableLiveData<Entry>(null) }
    val mPing by lazy { MutableStateFlow(0) }
    val mJitter by lazy { MutableStateFlow(0) }

    init {
        getConnectionType()
    }
    val expDownload = CoroutineExceptionHandler { coroutineContext, throwable ->
        val msg = throwable.message
        msg?.let {
            mTestingStatus.value = TestingStatus.Error(it)
        }
    }
    fun getConnectionType() = viewModelScope.launch(Dispatchers.IO) {
        mSettings.connection.collect{
            mConnectionType = it
        }
    }
    fun getTimeOut() = viewModelScope.launch(Dispatchers.IO) {
        mSettings.timeout.collect{
            Timber.e("GetTimeOut: ${it}")
            mTimeOut = it
        }
    }

    fun startDownloadTest(url: String) = viewModelScope.launch(Dispatchers.IO + expDownload) {
        Timber.e("Type: ${mConnectionType}")
        mEntryDownload.postValue(null)
        mBuilderDownload = TestDownloader.Builder(url)
            .addListener(object : TestDownloader.TestDownloadListener {
                override fun onStart() {
                    mTestingStatus.value = TestingStatus.Testing(true, TESTTYPE_DOWNLOAD)
                }

                override fun onProgress(progress: Double, elapsedTimeMillis: Double) {
                    mSpeed.value = progress
                    mEntryDownload.postValue(null)
                    mEntryDownload.postValue(Entry((elapsedTimeMillis*1000).toFloat(), progress.toFloat()))
                }

                override fun onFinished(
                    finalprogress: Double,
                    datausedinkb: Int,
                    elapsedTimeMillis: Double
                ) {
                    mTestingStatus.value = TestingStatus.Finished(TESTTYPE_DOWNLOAD)
                    Timber.e("ElapsedTime: ${elapsedTimeMillis}")
                }

                override fun onError(msg: String) {
                    mTestingStatus.value = TestingStatus.Error(msg)
                }

            })
            .setTimeOUt(mTimeOut)
            .setThreadsCount(mConnectionType)
            .build()
        mBuilderDownload?.start()
    }
    //End Download Test


    //upload test
    val expUpload = CoroutineExceptionHandler { coroutineContext, throwable ->
        val msg = throwable.message
        msg?.let {
            mTestingStatus.value = TestingStatus.Error(it)
        }
    }

    fun startUploadTest(url: String) = viewModelScope.launch(Dispatchers.IO + expUpload) {
        delay(2000)
        val list = ArrayList<Entry>()
        var xCounter = 0.0f
        val fullurl = "${url}upload.php"
        mBuilderUpload = TestUploader.Builder(fullurl)
            .addListener(object : TestUploader.TestUploadListener {
                override fun onStart() {
                    mTestingStatus.value = TestingStatus.Testing(true, TESTTYPE_UPLOAD)
                }

                override fun onProgress(progress: Double, elapsedTimeMillis: Double) {
                    mSpeed.value = progress
                    mEntryUpload.postValue(null)
                    mEntryUpload.postValue(Entry((elapsedTimeMillis*1000).toFloat(), progress.toFloat()))
                }


                override fun onFinished(
                    finalprogress: Double,
                    datausedinkb: Int,
                    elapsedTimeMillis: Double
                ) {
                    mTestingStatus.value = TestingStatus.Finished(TESTTYPE_UPLOAD)
                }

                override fun onError(msg: String) {
                    mTestingStatus.value = TestingStatus.Error(msg)

                }

            })
            .setTimeOUt(mTimeOut)
            .setThreadsCount(mConnectionType)
            .build()
        mBuilderUpload?.start()
    }

    //end upload test
    fun stopTesting() {
        mTestingStatus.value = TestingStatus.Canceled
        mBuilderDownload?.stop()
        mBuilderUpload?.removeListener()

        mBuilderDownload?.stop()
        mBuilderDownload?.removeListener()
    }

   fun startPing(url:String) = viewModelScope.launch{
        val builder = Ping.Builder(url)
            .setListener(object :Ping.PingListener{
                override fun onStarted() {

                }

                override fun onError(error: String) {
                    Timber.e("PingError: ${error}")
                }

                override fun onInstantRtt(instantRtt: Double) {
                }

                override fun onAvgRtt(avgRtt: Double) {
                    mPing.value = avgRtt.toInt()
                }

                override fun onFinished(jitter: Int) {
                    mJitter.value = jitter.toInt()
                }

            })
            .build()
        builder.start()
    }

    override fun onCleared() {
        super.onCleared()
        stopTesting()
    }
}