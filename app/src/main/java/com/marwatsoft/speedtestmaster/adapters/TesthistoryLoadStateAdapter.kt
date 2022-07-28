package com.marwatsoft.speedtestmaster.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.marwatsoft.speedtestmaster.databinding.LoadStateViewBinding

class TesthistoryLoadStateAdapter() :
    LoadStateAdapter<TesthistoryLoadStateAdapter.TesthistoryLoadStateViewHolder>() {



    override fun onBindViewHolder(holder: TesthistoryLoadStateViewHolder, loadState: LoadState) {
        holder.binding.apply {
            progress.isVisible = loadState is LoadState.Loading
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): TesthistoryLoadStateViewHolder {
        val view = LoadStateViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,false
        )
        return TesthistoryLoadStateViewHolder(view)
    }
    class  TesthistoryLoadStateViewHolder(val binding:LoadStateViewBinding):
        RecyclerView.ViewHolder(binding.root)

}