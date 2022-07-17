package com.marwatsoft.speedtestmaster.ui.testmain

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import com.marwatsoft.speedtestmaster.repository.SpeedTestRepo
import com.marwatsoft.speedtestmaster.utils.speedtest.HttpDownloadTest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
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
    private val mSpeedTestRepo: SpeedTestRepo
) : ViewModel() {
    val mTestingStatus by lazy { MutableStateFlow<TestingStatus>(TestingStatus.Testing(false, "")) }
    val mSpeed by lazy { MutableStateFlow(0.0) }
    val mTimeOut by lazy { 12 }
    var mBuilderUpload: TestUploader? = null
    var mBuilderDownload: TestDownloader? = null
    val mEntryDownload by lazy { MutableLiveData<Entry>(null) }
    val mEntryUpload by lazy { MutableLiveData<Entry>(null) }

    val expDownload = CoroutineExceptionHandler { coroutineContext, throwable ->
        val msg = throwable.message
        msg?.let {
            mTestingStatus.value = TestingStatus.Error(it)
        }
    }

    fun startDownloadTest(url: String) = viewModelScope.launch(Dispatchers.IO + expDownload) {
        val list = ArrayList<Entry>()
        var xCounter = 0.0f
        mEntryDownload.postValue(null)
        mBuilderDownload = TestDownloader.Builder(url)
            .addListener(object : TestDownloader.TestDownloadListener {
                override fun onStart() {
                    mTestingStatus.value = TestingStatus.Testing(true, TESTTYPE_DOWNLOAD)
                }

                override fun onProgress(progress: Double, elapsedTimeMillis: Int) {
                    mSpeed.value = progress
                    val x = elapsedTimeMillis.toFloat()
                    if (x != xCounter) {
                        xCounter = x
                        mEntryDownload.postValue(null)
                        mEntryDownload.postValue(Entry(xCounter, progress.toFloat()))
                    }
                }

                override fun onStopped() {

                }

                override fun onFinished(
                    finalprogress: Double,
                    datausedinkb: Int,
                    elapsedTimeMillis: Int
                ) {
                    mTestingStatus.value = TestingStatus.Finished(TESTTYPE_DOWNLOAD)
                }

                override fun onError(msg: String) {
                    mTestingStatus.value = TestingStatus.Error(msg)
                }

            })
            .setTimeOUt(mTimeOut)
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

                override fun onProgress(progress: Double, elapsedTimeMillis: Int) {
                    mSpeed.value = progress
                    val x = elapsedTimeMillis.toFloat()
                    if (x != xCounter) {
                        xCounter = x
                        mEntryUpload.postValue(null)
                        mEntryUpload.postValue(Entry(xCounter, progress.toFloat()))
                    }
                }

                override fun onStopped() {
                }

                override fun onFinished(
                    finalprogress: Double,
                    datausedinkb: Int,
                    elapsedTimeMillis: Int
                ) {
                    mTestingStatus.value = TestingStatus.Finished(TESTTYPE_UPLOAD)
                }

                override fun onError(msg: String) {
                    mTestingStatus.value = TestingStatus.Error(msg)
                }

            })
            .setTimeOUt(mTimeOut)
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

    override fun onCleared() {
        super.onCleared()
        stopTesting()
    }
}