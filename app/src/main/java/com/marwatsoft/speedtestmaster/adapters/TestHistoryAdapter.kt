package com.marwatsoft.speedtestmaster.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.marwatsoft.speedtestmaster.R
import com.marwatsoft.speedtestmaster.data.Test.Test
import com.marwatsoft.speedtestmaster.databinding.ItemTesthistoryBinding
import pk.farimarwat.speedtest.models.STProvider
import pk.farimarwat.speedtest.models.STServer
import timber.log.Timber

class TestHistoryAdapter(
    private var mListener: TestHistoryListener
) : PagingDataAdapter<Test, TestHistoryAdapter.TestHistoryViewHolder>(mCallback) {
    interface TestHistoryListener {
        fun onItemClicked(provider: STProvider, server: STServer)
    }

    lateinit var mContext: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestHistoryViewHolder {
        mContext = parent.context
        val view = ItemTesthistoryBinding.inflate(
            LayoutInflater.from(mContext), parent, false
        )
        return TestHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: TestHistoryViewHolder, position: Int) {
        val item = getItem(position)
        item?.let { itm ->
            holder.apply {
                txtdownload.text = String.format(
                    mContext.getString(R.string.testhistoryitem_download),
                    itm.downloadspeed
                )
                txtupload.text = String.format(
                    mContext.getString(R.string.testhistoryitem_upload), itm.uploadspeed
                )
                txtserver.text = itm.testserver
                txtprovider.text = itm.provider
                txtdate.text = itm.created.toString()
                container.setOnClickListener{
                    val server = STServer(null,item.testserver_lat,item.testserver_lon,item.testserver,null)
                    val provider = STProvider(item.provider,item.provider,item.provider_lat,item.provider_lon)
                    mListener.onItemClicked(provider,server)
                }
            }
            doAnimation(mContext, holder.container)
        }
    }

    fun doAnimation(context: Context, view: View) {
        val animation = AnimationUtils.loadAnimation(context, R.anim.fall_down)
        animation.duration = 700
        view.startAnimation(animation)
    }

    class TestHistoryViewHolder(binding: ItemTesthistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val txtdownload = binding.txtDownload
        val txtupload = binding.txtUpload
        val txtserver = binding.txtServer
        val txtprovider = binding.txtProvider
        val txtdate = binding.txtDate
        val container = binding.container
    }

    companion object {
        val mCallback = object : DiffUtil.ItemCallback<Test>() {
            override fun areItemsTheSame(oldItem: Test, newItem: Test): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Test, newItem: Test): Boolean {
                return oldItem.id == newItem.id
            }

        }
    }
}