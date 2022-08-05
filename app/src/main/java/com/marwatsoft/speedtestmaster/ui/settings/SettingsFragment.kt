package com.marwatsoft.speedtestmaster.ui.settings

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.ads.LoadAdError
import com.google.firebase.analytics.FirebaseAnalytics
import com.marwatsoft.speedtestmaster.BuildConfig
import com.marwatsoft.speedtestmaster.R
import com.marwatsoft.speedtestmaster.databinding.FragmentHistoryBinding
import com.marwatsoft.speedtestmaster.databinding.FragmentSettingsBinding
import com.marwatsoft.speedtestmaster.helpers.DialogButtonClickListener
import com.marwatsoft.speedtestmaster.helpers.STDialog
import com.marwatsoft.speedtestmaster.helpers.SettingsHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import pk.farimarwat.modernadmob.AdmobView
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    lateinit var mContext:Context
    lateinit var binding:FragmentSettingsBinding
    val mViewModel:SettingsFragmentViewModel by viewModels()
    @Inject
    lateinit var mFirebaseAnalytics: FirebaseAnalytics
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
        mFirebaseAnalytics.logEvent("FRAGMENT_SETTINGS",null)
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.mConnectionType.collect{
                    when(it){
                       SettingsHelper.CONNECTION_TYPE_SINGLE -> {
                            binding.toggleSingle.isChecked = true
                            setBtnSingle()
                        }
                        SettingsHelper.CONNECTION_TYPE_MULTIPLE -> {
                            binding.toggleMultiple.isChecked = true
                            setBtnMultiple()
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.mTimeout.collect{
                   binding.edtDuration.setText(it.toString())
                }
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.mServerType.collect{
                    when(it){
                        SettingsHelper.Servers_PUBLIC -> {
                            binding.togglePublic.isChecked = true
                            setBtnServerPublic()
                        }
                        SettingsHelper.SERVERS_PREMIUM -> {
                            binding.togglePremium.isChecked = true
                            setBtnServerPremium()
                        }
                    }
                }
            }
        }
        lifecycleScope.launch{
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                loadBannerAd()
            }
        }
        initGui()
    }

    fun initGui(){
        binding.toggleConnectiontype.addOnButtonCheckedListener { group, checkedId, isChecked ->
            when(group.checkedButtonId){
                R.id.toggle_multiple ->{
                    if(isChecked){
                       setBtnMultiple()
                    }
                }
                R.id.toggle_single ->{
                    if(isChecked){
                        setBtnSingle()
                    }
                }
            }
        }
        binding.imgHelpConnection.setOnClickListener {
            val builder = STDialog.Builder(mContext)
                .setMessage(getString(R.string.help_connections))
                .setPositive("OK")
                .addListener(object: DialogButtonClickListener{
                    override fun onButtonClicked(dialog: AlertDialog?) {
                        dialog?.dismiss()
                    }

                })
                .build()
            builder.showDialog()
        }
        binding.imgHelpDuration.setOnClickListener {
            val builder = STDialog.Builder(mContext)
                .setMessage(getString(R.string.help_duration))
                .setPositive("OK")
                .addListener(object: DialogButtonClickListener{
                    override fun onButtonClicked(dialog: AlertDialog?) {
                        dialog?.dismiss()
                    }

                })
                .build()
            builder.showDialog()
        }
        binding.edtDuration.doAfterTextChanged {
            it?.let {
                val str_duration = it.toString()
                if(str_duration.isNotEmpty()){
                    val duration = str_duration.toInt()
                    if(duration in 12..20){
                        mViewModel.storeTimeOut(duration)
                    }
                }
            }
        }
        binding.toggleTestservers.addOnButtonCheckedListener { group, checkedId, isChecked ->
            when(group.checkedButtonId){
                R.id.toggle_public ->{
                    if(isChecked){
                        setBtnServerPublic()
                    }
                }
                R.id.toggle_premium ->{
                    if(isChecked){
                        setBtnServerPremium()
                    }
                }
            }
        }
        binding.imgHelpTestserver.setOnClickListener {
            val builder = STDialog.Builder(mContext)
                .setMessage(getString(R.string.help_servers))
                .setPositive("OK")
                .addListener(object: DialogButtonClickListener{
                    override fun onButtonClicked(dialog: AlertDialog?) {
                        dialog?.dismiss()
                    }

                })
                .build()
            builder.showDialog()
        }
        binding.imgHelpAbout.setOnClickListener {
            val builder = STDialog.Builder(mContext)
                .setMessage(getString(R.string.help_about,getString(R.string.app_name),BuildConfig.VERSION_NAME))
                .setPositive("OK")
                .addListener(object: DialogButtonClickListener{
                    override fun onButtonClicked(dialog: AlertDialog?) {
                        dialog?.dismiss()
                    }

                })
                .build()
            builder.showDialog()
        }
        binding.imgHelpPrivacypolicy.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(getString(R.string.url_privacy_policy))
            startActivity(intent)
        }
    }

    fun setBtnMultiple(){
        mViewModel.storeConnectionType(SettingsHelper.CONNECTION_TYPE_MULTIPLE)
        binding.toggleMultiple.setTextColor(
            ContextCompat.getColor(mContext,R.color.white)
        )
        binding.toggleSingle.setTextColor(
            ContextCompat.getColor(mContext,R.color.greyPrimary)
        )
    }
    fun setBtnSingle(){
        mViewModel.storeConnectionType(SettingsHelper.CONNECTION_TYPE_SINGLE)
        binding.toggleMultiple.setTextColor(
            ContextCompat.getColor(mContext,R.color.greyPrimary)
        )
        binding.toggleSingle.setTextColor(
            ContextCompat.getColor(mContext,R.color.white)
        )
    }

    fun setBtnServerPublic(){
        mViewModel.storeServerType(SettingsHelper.Servers_PUBLIC)
        binding.togglePremium.setTextColor(
            ContextCompat.getColor(mContext,R.color.greyPrimary)
        )
        binding.togglePublic.setTextColor(
            ContextCompat.getColor(mContext,R.color.white)
        )
    }
    fun setBtnServerPremium(){
        mViewModel.storeServerType(SettingsHelper.SERVERS_PREMIUM)
        binding.togglePremium.setTextColor(
            ContextCompat.getColor(mContext,R.color.white)
        )
        binding.togglePublic.setTextColor(
            ContextCompat.getColor(mContext,R.color.greyPrimary)
        )
    }

    fun loadBannerAd() {
        binding.myads
            .loadAd(mContext, BuildConfig.ADMOB_NATIVE_ADD,
                object : AdmobView.ModernAdmobListener {
                    override fun onAdClicked() {

                    }

                    override fun onAdClosed() {

                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        Timber.e("Ad error: ${error.message}")
                    }

                    override fun onAdImpression() {

                    }

                    override fun onAdLoaded() {
                        Timber.e("Add Loaded")
                    }

                    override fun onAdOpened() {

                    }

                })
    }
}