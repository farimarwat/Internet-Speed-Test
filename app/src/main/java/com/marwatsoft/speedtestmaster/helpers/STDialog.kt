package com.marwatsoft.speedtestmaster.helpers

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.marwatsoft.speedtestmaster.R
import com.marwatsoft.speedtestmaster.SpeedTestLib.DownloadListener

class STDialog private constructor(builder:Builder){
    var mContext: Context
    private var mDialog:AlertDialog? = null
    private var mMessage:String? = null
    private var mListener:DialogButtonClickListener? = null
    private var mPositive:String? = null
    private var mNegative:String? = null

    class Builder(val context: Context){
        private var mDialog:AlertDialog? = null
        private var mMessage:String? = null
        private var mListener:DialogButtonClickListener? = null
        private var mPositive:String? = null
        private var mNegative:String? = null

        //setters
        fun setMessage(message:String) = apply { this.mMessage = message }
        fun addListener(listener: DialogButtonClickListener) = apply { this.mListener = listener }
        fun setPositive(text:String) = apply { this.mPositive = text }
        fun setNegative(text:String) = apply { this.mNegative = text }
        //getters
        fun getMessage():String? = this.mMessage
        fun getListener():DialogButtonClickListener? = this.mListener
        fun getPositive():String? = this.mPositive
        fun getNegative():String? = this.mNegative
        fun build() = STDialog(this)
    }
    init {
        this.mMessage = builder.getMessage()
        this.mListener = builder.getListener()
        mPositive = builder.getPositive()
        mNegative = builder.getNegative()
        mContext = builder.context
    }

    fun showDialog(){
        val view = LayoutInflater.from(mContext).inflate(R.layout.dialog,null)
        val positive = view.findViewById<MaterialButton>(R.id.btn_positive)
        mPositive?.let {
            positive.visibility = View.VISIBLE
            positive.text = it
            mListener?.let { listener ->
                positive.setOnClickListener{
                    listener.onButtonClicked(mDialog)
                }
            }
        }
        val negative = view.findViewById<MaterialButton>(R.id.btn_negative)
        mNegative?.let {
            negative.visibility = View.VISIBLE
            mListener?.let { listener ->
                negative.setOnClickListener{
                    listener.onButtonClicked(mDialog)
                }
            }
        }
        val txtMessage = view.findViewById<TextView>(R.id.txt_message)
        mMessage?.let { msg ->
            txtMessage.text = msg
        }
        val builder = AlertDialog.Builder(mContext)
        builder.setView(view)
        mDialog = builder.create()
        mDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        mDialog?.show()
    }
}
interface DialogButtonClickListener{
    fun onButtonClicked(dialog:AlertDialog?)
}