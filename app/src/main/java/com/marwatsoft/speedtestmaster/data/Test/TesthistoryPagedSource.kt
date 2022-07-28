package com.marwatsoft.speedtestmaster.data.Test

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.delay
import javax.inject.Inject

class TesthistoryPagedSource @Inject constructor(val repo:TestRepo):
    PagingSource<Int, Test>() {
    override fun getRefreshKey(state: PagingState<Int, Test>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Test> {
        val page = params.key ?: 0
        return try {
            val entities = repo.listPagedHistory(params.loadSize, page * params.loadSize)

            // simulate page loading
            if (page != 0) delay(1000)

            LoadResult.Page(
                data = entities,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (entities.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}