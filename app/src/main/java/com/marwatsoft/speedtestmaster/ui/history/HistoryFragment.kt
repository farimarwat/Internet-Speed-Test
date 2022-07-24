package com.marwatsoft.speedtestmaster.ui.history

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.marwatsoft.speedtestmaster.R
import com.marwatsoft.speedtestmaster.adapters.TestHistoryAdapter
import com.marwatsoft.speedtestmaster.databinding.FragmentHistoryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class HistoryFragment : Fragment() {
    lateinit var mContext:Context
    lateinit var binding:FragmentHistoryBinding
    val mViewModel:HistoryFragmentViewModel by viewModels()
    val mAdapter by lazy {
        TestHistoryAdapter()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(inflater,container,false)
        mContext = requireContext()


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initGui()
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                delay(200)
                mViewModel.listAll().collect{
                    mAdapter.submitData(it)
                }
            }
        }
    }
    fun initGui(){
        binding.recyclerviewTesthistory.apply {
            layoutManager = LinearLayoutManager(mContext)
            adapter = mAdapter
        }
    }
}