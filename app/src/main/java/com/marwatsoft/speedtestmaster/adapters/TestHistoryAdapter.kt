package com.marwatsoft.speedtestmaster.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.marwatsoft.speedtestmaster.R
import com.marwatsoft.speedtestmaster.data.Test.Test
import com.marwatsoft.speedtestmaster.databinding.ItemTesthistoryBinding

class TestHistoryAdapter: PagingDataAdapter<Test,TestHistoryAdapter.TestHistoryViewHolder>(mCallback) {
    lateinit var mContext:Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestHistoryViewHolder {
        mContext = parent.context
        val view = ItemTesthistoryBinding.inflate(
            LayoutInflater.from(mContext),parent,false
        )
        return TestHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: TestHistoryViewHolder, position: Int) {
        val item = getItem(position)
       item ?.let { itm ->
           holder.apply {
               txtdownload.text = String.format(
                   mContext.getString(R.string.testhistoryitem_download),
                   itm.downloadspeed)
               txtupload.text = String.format(
                   mContext.getString(R.string.testhistoryitem_upload),itm.uploadspeed
               )
               txtserver.text = itm.testserver
               txtprovider.text = itm.provider
               txtdate.text = itm.created.toString()
           }
       }
    }

    class TestHistoryViewHolder(binding:ItemTesthistoryBinding): RecyclerView.ViewHolder(binding.root){
        val txtdownload = binding.txtDownload
        val txtupload = binding.txtUpload
        val txtserver = binding.txtServer
        val txtprovider = binding.txtProvider
        val txtdate = binding.txtDate
    }
    companion object {
        val mCallback = object: DiffUtil.ItemCallback<Test>(){
            override fun areItemsTheSame(oldItem: Test, newItem: Test): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Test, newItem: Test): Boolean {
                return oldItem.id == newItem.id
            }

        }
    }
}