package com.marwatsoft.speedtestmaster.ui.settings

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.marwatsoft.speedtestmaster.R
import com.marwatsoft.speedtestmaster.databinding.FragmentHistoryBinding
import com.marwatsoft.speedtestmaster.databinding.FragmentSettingsBinding
import com.marwatsoft.speedtestmaster.helpers.DialogButtonClickListener
import com.marwatsoft.speedtestmaster.helpers.STDialog
import timber.log.Timber

class SettingsFragment : Fragment() {
    lateinit var mContext:Context
    lateinit var binding:FragmentSettingsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mContext = requireContext()
        binding = FragmentSettingsBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initGui()
    }

    fun initGui(){
        binding.toggleConnectiontype.addOnButtonCheckedListener { group, checkedId, isChecked ->
            when(group.checkedButtonId){
                R.id.toggle_multiple ->{
                    if(isChecked){
                        binding.toggleMultiple.setTextColor(
                           ContextCompat.getColor(mContext,R.color.white)
                        )
                        binding.toggleSingle.setTextColor(
                            ContextCompat.getColor(mContext,R.color.greyPrimary)
                        )
                    }
                }
                R.id.toggle_single ->{
                    if(isChecked){
                        binding.toggleMultiple.setTextColor(
                            ContextCompat.getColor(mContext,R.color.greyPrimary)
                        )
                        binding.toggleSingle.setTextColor(
                            ContextCompat.getColor(mContext,R.color.white)
                        )
                        Timber.e("Single")
                    }
                }
            }
        }
        binding.imgHelp.setOnClickListener {
            val builder = STDialog.Builder(mContext)
                .setMessage(getString(R.string.help_connections))
                .setPositive("OK")
                .addListener(object: DialogButtonClickListener{
                    override fun onButtonClicked(dialog: AlertDialog?) {
                        dialog?.hide()
                    }

                })
                .build()
            builder.showDialog()
        }
    }
}