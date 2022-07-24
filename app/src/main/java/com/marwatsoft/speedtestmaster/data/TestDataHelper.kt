package com.marwatsoft.speedtestmaster.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.marwatsoft.speedtestmaster.data.Test.Test
import com.marwatsoft.speedtestmaster.data.Test.TestDao

@Database(entities = [
    Test::class
], version = 1, exportSchema = true)
@TypeConverters(Converters::class)
abstract class TestDataHelper: RoomDatabase() {
    abstract fun testdao():TestDao
}