package com.marwatsoft.speedtestmaster.ui.main

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.Context
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.dinuscxj.progressbar.CircleProgressBar
import com.farimarwat.supergaugeview.SuperGaugeView
import com.google.android.gms.ads.LoadAdError
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.marwatsoft.speedtestmaster.R
import com.marwatsoft.speedtestmaster.adapters.STServerAdapter
import com.marwatsoft.speedtestmaster.databinding.FragmentMainBinding
import com.marwatsoft.speedtestmaster.helpers.DialogButtonClickListener
import com.marwatsoft.speedtestmaster.helpers.STDialog

import com.marwatsoft.speedtestmaster.network.ApiStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pk.farimarwat.modernadmob.AdmobView
import pk.farimarwat.speedtest.models.STServer
import pk.farimarwat.speedtest.models.TESTTYPE_DOWNLOAD
import pk.farimarwat.speedtest.models.TESTTYPE_UPLOAD
import pk.farimarwat.speedtest.models.TestingStatus
import timber.log.Timber


@AndroidEntryPoint
class MainFragment : Fragment() {
    lateinit var binding: FragmentMainBinding
    lateinit var mContext: Context
    val mViewModel: MainFragmentViewModel by viewModels()
    val mNavController by lazy { findNavController() }
    private val mAdapterServer by lazy {
        STServerAdapter(object : STServerAdapter.StServerClickListener {
            override fun onStServerClicked(server: STServer) {
                mViewModel.mSTServerSelected.value = server
                mBehaviorServerBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
            }

        })
    }
    lateinit var mBehaviorServerBottomSheet: BottomSheetBehavior<ConstraintLayout>
    lateinit var mViewServerBottomSheet: ConstraintLayout
    lateinit var mUrl: String

    //SpeedView

    //End speedview
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mContext = requireContext()
        binding = FragmentMainBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initGui()
        //Listening network listener
        lifecycleScope.launchWhenCreated {
            mViewModel.loadServers()
        }

        //Collecting Server List
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.mListSTServer.collect {
                    when (it) {
                        is ApiStatus.Loading -> {
                            mAdapterServer.submitList(null)
                            mBehaviorServerBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
                            binding.containerError.visibility = View.GONE
                            binding.containerLoading.visibility = View.VISIBLE
                            binding.groupGo.visibility = View.GONE
                        }
                        is ApiStatus.Error -> {
                            mAdapterServer.submitList(null)
                            mBehaviorServerBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
                            binding.txtError.text = it.error
                            binding.containerError.visibility = View.VISIBLE
                            binding.containerLoading.visibility = View.GONE
                            binding.groupGo.visibility = View.GONE

                        }
                        is ApiStatus.Success -> {
                            binding.containerError.visibility = View.GONE
                            binding.containerLoading.visibility = View.GONE
                            binding.groupGo.visibility = View.VISIBLE
                            val provider = mViewModel.mSTProvider.value
                            provider?.let {
                                binding.txtProvidername.text = String.format(
                                    getString(R.string.provider), it.providername
                                )
                            }

                            //setting server list
                            val list = it.list as MutableList<STServer>
                            if (list.isNotEmpty()) {
                                val server = list.first()
                                mViewModel.mSTServerSelected.value = server
                                val recyclerview =
                                    mViewServerBottomSheet.findViewById<RecyclerView>(
                                        R.id.recyclerveiw_servers
                                    )
                                recyclerview.apply {
                                    layoutManager = LinearLayoutManager(mContext)
                                    adapter = mAdapterServer
                                }
                                mAdapterServer.submitList(list)
                            }
                        }
                    }
                }
            }
        }

        //Collecting Selected Server
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.mSTServerSelected.observe(viewLifecycleOwner) {
                    if (it != null) {
                        binding.txtTestserver.visibility = View.VISIBLE
                        binding.txtTestserver.text = "${it.sponsor}(${it.name})"
                        mUrl = it.url
                    } else {
                        binding.txtTestserver.visibility = View.GONE
                        binding.txtTestserver.text = ""
                    }
                }
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()

    }

    fun initGui() {

        binding.btnRetry.setOnClickListener {
            mViewModel.loadServers()
        }
        mViewServerBottomSheet = binding.root.findViewById(R.id.container)
        mBehaviorServerBottomSheet = BottomSheetBehavior.from(mViewServerBottomSheet)

        binding.btnGo.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToTestmainFragment(mUrl)
            mNavController.navigate(action)
        }
        binding.txtChangetestserver.setOnClickListener {
            mBehaviorServerBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
        }

    }



}