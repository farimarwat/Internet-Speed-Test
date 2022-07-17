package com.marwatsoft.speedtestmaster.adapters

import android.app.Application
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.marwatsoft.speedtestmaster.R
import com.marwatsoft.speedtestmaster.databinding.ItemServerBinding
import pk.farimarwat.speedtest.models.STServer

class STServerAdapter(
    val mListener:StServerClickListener
): ListAdapter<STServer,STServerAdapter.STServerHolder>(callback){
    lateinit var mContext:Context
    interface StServerClickListener{
        fun onStServerClicked(server:STServer)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): STServerHolder {
        mContext = parent.context
        val view = ItemServerBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        )
        return STServerHolder(view)
    }

    override fun onBindViewHolder(holder: STServerHolder, position: Int) {
        val item = getItem(position)
        holder.apply {
            txtSponser.text = item.sponsor
            txtName.text = item.name
            txtDistance.text = String.format(
                mContext.getString(R.string.distance),
                item.distance
            )
            container.setOnClickListener{
                mListener.onStServerClicked(item)
            }
        }
    }
    class STServerHolder(binding:ItemServerBinding):RecyclerView.ViewHolder(binding.root){
        val txtSponser = binding.txtSponcer
        val txtName = binding.txtName
        val txtDistance = binding.txtDistance
        val container = binding.container
    }
    companion object {
        val callback = object:DiffUtil.ItemCallback<STServer>(){
            override fun areItemsTheSame(oldItem: STServer, newItem: STServer): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: STServer, newItem: STServer): Boolean {
                return oldItem.name == newItem.name
            }

        }
    }
}