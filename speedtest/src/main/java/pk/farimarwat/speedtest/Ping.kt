package pk.farimarwat.speedtest

import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

class Ping private constructor(builder:Builder){
    companion object {
        val COUNT_DEFAULT = 4
    }
    private val result = HashMap<String,Any>()
    private var mServer:String = ""
    private var mCount:Int = COUNT_DEFAULT
    private var mFinished = false
    private var mStarted = false
    private var instantRtt = 0.0
    private var avgRtt = 0.0
    private var mListener:PingListener?= null
    private val mListPing = mutableListOf<Int>()
    class Builder(val url:String){
        private var mCount = COUNT_DEFAULT
        private var mListener:PingListener?= null
        fun setCount(count:Int) = apply { this.mCount = count }
        fun getCount():Int = this.mCount
        fun setListener(listener: PingListener) = apply { mListener = listener }
        fun getListener():PingListener? = this.mListener
        fun build():Ping = Ping(this)
    }
    init {
        mServer = builder.url
        mCount = builder.getCount()
        mListener = builder.getListener()
    }

    fun start(){
        val exp = CoroutineExceptionHandler { coroutineContext, throwable ->
            mListener?.onError(throwable?.message.toString())
        }
        CoroutineScope(Dispatchers.IO + exp).launch {
            val ps = ProcessBuilder("ping", "-c $mCount", mServer)
            mListener?.onStarted()
            ps.redirectErrorStream(true)
            val pr = ps.start()
            val `in` = BufferedReader(InputStreamReader(pr.inputStream))
            var line: String
            while (`in`.readLine().also { line = it } != null) {
                if (line.contains("icmp_seq")) {
                    instantRtt = line.split(" ".toRegex()).toTypedArray()[line.split(" ".toRegex())
                        .toTypedArray().size - 2].replace("time=", "").toDouble()
                    mListener?.onInstantRtt(instantRtt)
                    mListPing.add(instantRtt.toInt())
                }
                if (line.startsWith("rtt ")) {
                    avgRtt = line.split("/".toRegex()).toTypedArray()[4].toDouble()
                    mListener?.onAvgRtt(avgRtt)
                    break
                }
                if (line.contains("Unreachable") || line.contains("Unknown") || line.contains("%100 packet loss")) {
                    mListener?.onError("Unreachable/Unknown server")
                    return@launch
                }
            }
            pr.waitFor()
            `in`.close()
            val jitter = calculateJitter(mListPing)
            mListener?.onFinished(jitter)
        }
    }
    fun calculateJitter(list:List<Int>):Int{
        val list_difference = mutableListOf<Int>()
        var firstValue: Int
        var secondValue: Int
        for (i in list.indices) {
            if (i < list.size - 1) {
                firstValue = list[i]
                secondValue = list[i + 1]
                val diff = firstValue - secondValue
                list_difference.add(Math.abs(diff))
            }
        }
        var total = 0
        for (i in list_difference) {
            total += i
        }
        val jitter = total.div(list_difference.size)
        return jitter
    }
    interface PingListener{
        fun onStarted()
        fun onError(error:String)
        fun onInstantRtt(instantRtt:Double)
        fun onAvgRtt(avgRtt:Double)
        fun onFinished(jitter:Int)
    }
}