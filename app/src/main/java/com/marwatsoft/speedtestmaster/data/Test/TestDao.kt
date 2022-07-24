package com.marwatsoft.speedtestmaster.data.Test

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface TestDao {

    @Insert
    suspend fun insert(test: Test)

    @Query("SELECT * FROM testhistory ORDER BY _id DESC")
    fun listAll():PagingSource<Int,Test>

    @Delete
    suspend fun delete(test: Test)
}