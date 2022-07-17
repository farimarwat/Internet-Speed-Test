package com.funsol.wifianalyzer.utils

import java.text.SimpleDateFormat
import java.util.*

object MyDateUtil {
    val QUALITY_DURATION = 90
    fun getDays(lastconnected:String):Long{
        var days:Long = 0
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val last_connected = sdf.parse(lastconnected)
        val c = Calendar.getInstance()
        val currentDate = c.time

        last_connected?.let {
            val diff: Long = currentDate.time - it.time
            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            days = hours / 24
        }

        return days
    }
}