package com.marwatsoft.speedtestmaster.ui.history

import androidx.lifecycle.ViewModel
import com.marwatsoft.speedtestmaster.data.Test.Test
import com.marwatsoft.speedtestmaster.data.Test.TestRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class HistoryFragmentViewModel @Inject constructor(
    val repo:TestRepo
): ViewModel() {

    fun listAll():Flow<List<Test>> = repo.listAll()
}