package com.marwatsoft.speedtestmaster.data.Test

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "testhistory")
data class Test(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id:Int,
    @ColumnInfo(name = "downloadspeed")
    val downloadspeed:Double,
    @ColumnInfo(name="uploadspeed")
    val uploadspeed:Double,

    @ColumnInfo(name = "testserver")
    val testserver:String,
    @ColumnInfo(name="testserver_lat")
    val testserver_lat:String?,
    @ColumnInfo(name="testserver_lon")
    val testserver_lon:String?,

    @ColumnInfo(name = "provider")
    val provider:String?,
    @ColumnInfo(name = "provider_lat")
    val provider_lat:String?,
    @ColumnInfo(name="provider_lon")
    val provider_lon:String?,

    @ColumnInfo(name = "created")
    val created:Date?
)
