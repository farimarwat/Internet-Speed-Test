package com.marwatsoft.speedtestmaster.SpeedTestLib

import android.annotation.SuppressLint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.math.BigDecimal
import java.math.RoundingMode
import java.net.HttpURLConnection
import java.net.URL
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

class DownloadTest private constructor(builder:Builder){
    var mUrl:String? = null
    var mHttpType:Int? = null
    var mListener:DownloadListener? = null

    var mStartTime:Long = 0
    var mEndTime:Long = 0
    var mDownloadElapsedTime:Double = 0.0
    var mDownloadedBytes:Int = 0
    var mFinalDownloadRate:Double = 0.0
    var mInstanceDownloadRate:Double = 0.0
    var mTimeOut = 0
    var mIsFinished:Boolean = false
    var mIsCanceled:Boolean = false

    companion object {
        val TYPE_HTTP = 1
        val TYPE_HTTPS = 2
    }
    class Builder{
        private var mUrl:String? = null
        private var mHttpType:Int? = null
        private var mListener:DownloadListener? = null
        private var mTimeOut = 8

        //Setters
        fun setUrl(url:String) = apply { this.mUrl = url }
        fun setHttpType(type:Int) = apply { this.mHttpType = type }
        fun setTimeOut(time:Int) = apply { this.mTimeOut = time }
        fun addListener(listener:DownloadListener) = apply { this.mListener = listener }
        //Getters
        fun getUrl():String? = this.mUrl
        fun getHttpType():Int? = this.mHttpType
        fun getTimeOut():Int = this.mTimeOut
        fun getListener():DownloadListener? = this.mListener

        fun build() = DownloadTest(this)
    }

    init {
        this.mUrl = builder.getUrl()
        this.mHttpType = builder.getHttpType()
        this.mListener = builder.getListener()
        this.mTimeOut = builder.getTimeOut()
    }

    suspend fun start(){
        CoroutineScope(Dispatchers.IO).launch {
            mListener?.onStarted()
            var url:URL
            mUrl?.let { u ->
                var httpsConn: HttpURLConnection? = null
                val trustAllCerts = arrayOf<TrustManager>(
                    @SuppressLint("CustomX509TrustManager")
                    object : X509TrustManager {
                        override fun getAcceptedIssuers(): Array<X509Certificate> {
                            return arrayOf()
                        }

                        @SuppressLint("TrustAllX509TrustManager")
                        override fun checkClientTrusted(
                            certs: Array<X509Certificate>,
                            authType: String
                        ) {
                        }

                        @SuppressLint("TrustAllX509TrustManager")
                        override fun checkServerTrusted(
                            certs: Array<X509Certificate>,
                            authType: String
                        ) {
                        }
                    }
                )

                mDownloadedBytes = 0
                var responseCode = 0

                val fileUrls: MutableList<String> = ArrayList()
                fileUrls.add(u + "random4000x4000.jpg")
                fileUrls.add(u + "random3000x3000.jpg")
                mStartTime = System.currentTimeMillis()
                outer@ for (link in fileUrls) {
                    try {
                        url = URL(link)
                        httpsConn = url.openConnection() as HttpURLConnection?
                        val sc = SSLContext.getInstance("SSL")
                        sc.init(null, trustAllCerts, SecureRandom())
                        httpsConn?.connect()
                        responseCode = httpsConn?.responseCode!!
                    } catch (ex: java.lang.Exception) {
                        Timber.e("HttpDownloadTest: " + ex.message)
                        mListener?.onError(ex.message!!)
                        break@outer
                    }
                    try {
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            val buffer = ByteArray(10240)
                            val inputStream = httpsConn.inputStream
                            var len = 0
                            while (inputStream.read(buffer).also { len = it } != -1) {
                                mDownloadedBytes += len
                                mEndTime = System.currentTimeMillis()
                                mDownloadElapsedTime = (mEndTime - mStartTime) / 1000.0
                                setInstantDownloadRate(mDownloadedBytes, mDownloadElapsedTime)
                                mListener?.onProgress(getInstantDownloadRate())
                                if (mDownloadElapsedTime >= mTimeOut) {
                                    break@outer
                                }
                            }
                            inputStream.close()
                            httpsConn.disconnect()
                        } else {
                            Timber.e("Link Not Found")
                            mListener?.let { listener ->
                                listener.onError("Link not found")
                            }
                        }
                    } catch (ex: java.lang.Exception) {
                        ex.printStackTrace()
                    }
                }
                mEndTime = System.currentTimeMillis()
                mDownloadElapsedTime = (mEndTime - mStartTime) / 1000.0
                mFinalDownloadRate = ((mDownloadedBytes * 8 )/ (1000 * 1000.0)) / mDownloadElapsedTime
                mIsFinished = true
                mListener?.onFinished(round(mFinalDownloadRate,2))
            }
        }
    }

    private fun round(value: Double, places: Int): Double {
        require(places >= 0)
        var bd: BigDecimal
        bd = try {
            BigDecimal(value)
        } catch (ex: Exception) {
            return 0.0
        }
        bd = bd.setScale(places, RoundingMode.HALF_UP)
        return bd.toDouble()
    }
    private fun getInstantDownloadRate(): Double {
        return mInstanceDownloadRate
    }

    private fun setInstantDownloadRate(downloadedByte: Int, elapsedTime: Double) {
        if (downloadedByte >= 0) {
            this.mInstanceDownloadRate = round((downloadedByte * 8 / (1000 * 1000) / elapsedTime), 2)
        } else {
            this.mInstanceDownloadRate = 0.0
        }
    }
}

interface DownloadListener{
    fun onStarted()
    fun onProgress(progress:Double)
    fun onFinished(progress: Double)
    fun onError(error:String)
}