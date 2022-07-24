package com.marwatsoft.speedtestmaster.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.marwatsoft.speedtestmaster.R
import com.marwatsoft.speedtestmaster.data.Test.Test
import com.marwatsoft.speedtestmaster.databinding.ItemTesthistoryBinding

class TestHistoryAdapter: ListAdapter<Test,TestHistoryAdapter.TestHistoryViewHolder>(mCallback) {
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
        holder.apply {
            txtdownload.text = String.format(
                mContext.getString(R.string.testhistoryitem_download),
                item.downloadspeed)
            txtupload.text = String.format(
                mContext.getString(R.string.testhistoryitem_upload),item.uploadspeed
            )
            txtserver.text = item.testserver
            txtprovider.text = item.provider
            txtdate.text = item.created.toString()
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