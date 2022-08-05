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
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.analytics.FirebaseAnalytics
import com.marwatsoft.speedtestmaster.BuildConfig
import com.marwatsoft.speedtestmaster.R
import com.marwatsoft.speedtestmaster.adapters.TestHistoryAdapter
import com.marwatsoft.speedtestmaster.adapters.TesthistoryLoadStateAdapter
import com.marwatsoft.speedtestmaster.databinding.FragmentHistoryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import pk.farimarwat.speedtest.models.STProvider
import pk.farimarwat.speedtest.models.STServer
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class HistoryFragment : Fragment() {
    lateinit var mContext:Context
    lateinit var binding:FragmentHistoryBinding
    lateinit var mNavController:NavController
    val mViewModel:HistoryFragmentViewModel by viewModels()
    val mAdapter by lazy {
        TestHistoryAdapter(object :TestHistoryAdapter.TestHistoryListener{
            override fun onItemClicked(provider: STProvider, server: STServer) {
                showInterstitial()
               val action = HistoryFragmentDirections.actionHistoryFragmentToMapFragment(
                   provider,server
               )
                mNavController.navigate(action)
            }

        })
    }
    @Inject
    lateinit var mFirebaseAnalytics: FirebaseAnalytics
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(inflater,container,false)
        mNavController = findNavController()
        mContext = requireContext()


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFirebaseAnalytics.logEvent("FRAGMENT_History",null)
        initGui()
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                delay(200)
                mViewModel.listAll().collect{
                    mAdapter.submitData(it)
                }
            }
        }
        lifecycleScope.launch{
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                loadInterstitial(mContext)
            }
        }
    }
    fun initGui(){
        binding.recyclerviewTesthistory.apply {
            layoutManager = LinearLayoutManager(mContext)
            adapter = mAdapter.withLoadStateFooter(TesthistoryLoadStateAdapter())
        }
    }

    var mInterstitial: InterstitialAd? = null
    fun loadInterstitial(context: Context) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            BuildConfig.ADMOB_INTERSTITIAL_ADD,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Timber.e(adError?.toString())
                    mInterstitial = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Timber.e("Interstitial ad loaded")
                    mInterstitial = interstitialAd
                }
            })
    }
    fun showInterstitial(){
        mInterstitial?.show(requireActivity())
    }
}