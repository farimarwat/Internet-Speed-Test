package com.marwatsoft.speedtestmaster.data.Test

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TestRepo @Inject constructor(val dao:TestDao) {

    suspend fun insert(test:Test) = dao.insert(test)
    suspend fun delete(test: Test) = dao.delete(test)
    fun listAll():Flow<List<Test>> = dao.listAll()
}