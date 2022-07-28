package com.marwatsoft.speedtestmaster.data.Test

import androidx.paging.PagingSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TestRepo @Inject constructor(private val dao:TestDao) {

    suspend fun insert(test:Test) = dao.insert(test)
    suspend fun delete(test: Test) = dao.delete(test)
    fun listAll():PagingSource<Int,Test> = dao.listAll()
    suspend fun listPagedHistory(limit:Int,offset:Int):List<Test>
    = dao.listPagedHistory(limit,offset)
}