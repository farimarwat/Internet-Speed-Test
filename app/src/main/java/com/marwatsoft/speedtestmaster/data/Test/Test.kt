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
    @ColumnInfo(name = "provider")
    val provider:String?,
    @ColumnInfo(name = "created")
    val created:Date?
)
