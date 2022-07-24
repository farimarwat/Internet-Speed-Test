package com.marwatsoft.speedtestmaster.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.marwatsoft.speedtestmaster.data.Test.Test
import com.marwatsoft.speedtestmaster.data.Test.TestRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HistoryFragmentViewModel @Inject constructor(
    val repo:TestRepo
): ViewModel() {

    suspend fun listAll(): Flow<PagingData<Test>> {
        val differed = viewModelScope.async(Dispatchers.IO) {
             Pager(
                config = PagingConfig(pageSize = 10),
                pagingSourceFactory = { repo.listAll() }
            ).flow.cachedIn(viewModelScope)
        }
        return differed.await()
    }
}