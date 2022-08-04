package com.marwatsoft.speedtestmaster.ui.testmain

import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.farimarwat.supergaugeview.SuperGaugeView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.marwatsoft.speedtestmaster.BuildConfig
import com.marwatsoft.speedtestmaster.R
import com.marwatsoft.speedtestmaster.data.Test.Test
import com.marwatsoft.speedtestmaster.data.Test.TestRepo
import com.marwatsoft.speedtestmaster.databinding.FragmentTestmainBinding
import com.marwatsoft.speedtestmaster.helpers.SettingsHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pk.farimarwat.modernadmob.AdmobView
import pk.farimarwat.speedtest.models.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class TestmainFragment : Fragment() {
    lateinit var binding: FragmentTestmainBinding
    lateinit var mContext: Context
    val mViewModel: TestmainFragmentViewModel by viewModels()
    val mNavArgs: TestmainFragmentArgs by navArgs()
    lateinit var mUrl: String
    lateinit var mProvider: STProvider
    lateinit var mServer: STServer

    @Inject
    lateinit var mTestRepo: TestRepo

    @Inject
    lateinit var mSettings: SettingsHelper
    var mDownloadSpeed = 0.0
    var mUploadSpeed = 0.0

    // line chart
    lateinit var mListDownload: ArrayList<Entry>
    lateinit var mListUpload: ArrayList<Entry>
    lateinit var mLineDataset: ArrayList<ILineDataSet>
    lateinit var mLineData: LineData


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTestmainBinding.inflate(inflater, container, false)
        mContext = requireContext()
        mUrl = mNavArgs.url
        mProvider = mNavArgs.provider
        mServer = mNavArgs.server
        mViewModel.getTimeOut()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //Collecting Testing status
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.mTestingStatus.collect {
                    Timber.e("TestingStatus: ${it}")
                    when (it) {
                        is TestingStatus.Idle -> {

                        }
                        is TestingStatus.Testing -> {
                            if (it.testing) {
                                binding.groupSpeedtest.visibility = View.VISIBLE
                                binding.containerError.visibility = View.GONE
                                if (it.testtype == TESTTYPE_DOWNLOAD) {
                                    animateText(binding.txtDownload)
                                    binding.groupSpeedtest.visibility = View.VISIBLE
                                    binding.speedView.setGaugeBottomIcon(
                                        ContextCompat.getDrawable(
                                            mContext,
                                            R.drawable.ic_baseline_arrow_circle_down_24
                                        )
                                    )
                                    binding.speedView.setGaugeBottomIconColor(R.color.download)
                                    binding.speedView.setGaugeText(getString(R.string.download))
                                    binding.txtNumberDownload.text = "--"
                                    binding.speedView.setProgressBackground(SuperGaugeView.GAUGE_KEYHOLE_15)
                                } else if (it.testtype == TESTTYPE_UPLOAD) {
                                    animateText(binding.txtUpload)
                                    binding.groupSpeedtest.visibility = View.VISIBLE
                                    binding.speedView.setGaugeBottomIcon(
                                        ContextCompat.getDrawable(
                                            mContext,
                                            R.drawable.ic_baseline_arrow_circle_up_24
                                        )
                                    )
                                    binding.speedView.setGaugeBottomIconColor(R.color.upload)
                                    binding.speedView.setGaugeText(getString(R.string.upload))
                                    binding.txtNumberUpload.text = "--"
                                    binding.speedView.setProgressBackground(SuperGaugeView.GAUGE_KEYHOLE_14)
                                }
                            }
                        }
                        is TestingStatus.Error -> {
                            binding.groupSpeedtest.visibility = View.GONE
                            binding.containerError.visibility = View.VISIBLE
                            binding.txtError.text = "Error: ${it.error}"
                            mViewModel.stopTesting()
                            binding.txtDownload.clearAnimation()
                            binding.txtUpload.clearAnimation()
                        }
                        is TestingStatus.Canceled -> {
                            binding.groupSpeedtest.visibility = View.GONE
                        }
                        is TestingStatus.Finished -> {
                            if (it.testtype == TESTTYPE_DOWNLOAD) {
                                mDownloadSpeed = mViewModel.mSpeed.value
                                binding.txtDownload.clearAnimation()
                                val mlastSpeed = mViewModel.mSpeed.value.toString()
                                binding.txtNumberDownload.text = mlastSpeed
                                delay(1000)
                                mViewModel.mSpeed.value = 0.0
                                binding.speedView.setProgress(0f)
                                mViewModel.startUploadTest(mUrl!!)
                            } else if (it.testtype == TESTTYPE_UPLOAD) {
                                mUploadSpeed = mViewModel.mSpeed.value
                                val c = Calendar.getInstance()
                                val date = c.time
                                mTestRepo.insert(
                                    Test(
                                        0,
                                        mDownloadSpeed,
                                        mUploadSpeed,
                                        mServer.sponsor.toString(),
                                        mServer.lat, mServer.lon,
                                        mProvider.providername, mProvider.lat, mProvider.lon,
                                        date
                                    )
                                )
                                mViewModel.mTestingStatus.value = TestingStatus.Idle
                                binding.txtUpload.clearAnimation()
                                val mlastSpeed = mViewModel.mSpeed.value.toString()
                                binding.txtNumberUpload.text = mlastSpeed
                                delay(1000)
                                binding.speedView.setProgress(0f)
                                showAdd()
                                showInterstitial()
                            }
                        }

                    }
                }
            }
        }

        //Collecting speed
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.mSpeed.collect { speed ->
                    val teststatus = mViewModel.mTestingStatus.value
                    when (teststatus) {
                        is TestingStatus.Testing -> {
                            if (teststatus.testing) {
                                delay(200)
                                val spd = speed.toFloat()
                                binding.speedView.setProgress(spd)
                            }
                        }
                    }
                }
            }
        }

        //Collecting line chart entry download
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.mEntryDownload.observe(viewLifecycleOwner) {
                    it?.let { entry ->
                        val set1 =
                            binding.linechartStrength.data.getDataSetByIndex(1) as LineDataSet
                        set1.addEntry(entry)
                        binding.linechartStrength.data.notifyDataChanged()
                        binding.linechartStrength.notifyDataSetChanged()
                        binding.linechartStrength.invalidate()
                    }
                }
            }
        }
        //Collecting line chart entry upload
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.mEntryUpload.observe(viewLifecycleOwner) {
                    it?.let { entry ->
                        val set1 =
                            binding.linechartStrength.data.getDataSetByIndex(2) as LineDataSet
                        set1.addEntry(entry)
                        binding.linechartStrength.data.notifyDataChanged()
                        binding.linechartStrength.notifyDataSetChanged()
                        binding.linechartStrength.invalidate()
                    }
                }
            }
        }
        //Loading ad
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                loadBannerAd()
            }
        }
        lifecycleScope.launch{
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                loadInterstitial(mContext)
            }
        }

        //Collecting Ping
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.mPing.collect {
                    binding.txtNumberPing.text = "${it}"
                }
            }
        }
        //Collecting Jitter
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.mJitter.collect {
                    binding.txtNumberJitter.text = "${it}"
                }
            }
        }
        initGui()
    }

    fun initGui() {
        binding.speedView.addGaugeListener(object : SuperGaugeView.GaugeListener {
            override fun onGaugePrepared(prepared: Boolean) {
                startTest()
            }

            override fun onProgress(progress: Float) {

            }

            override fun onStartPreparing() {
                binding.groupSpeedtest.visibility = View.VISIBLE
            }

        })
        binding.btnRestart.setOnClickListener {
            YoYo.with(Techniques.FadeInDown)
                .onStart {
                    YoYo.with(Techniques.FadeOutUp)
                        .duration(200)
                        .onEnd {
                            binding.containerAdd.visibility = View.GONE
                            binding.speedView.visibility = View.VISIBLE
                        }
                        .playOn(binding.containerAdd)
                }
                .duration(700)
                .onEnd {
                    startTest()
                }
                .playOn(binding.speedView)
        }
        binding.btnRetry.setOnClickListener {
            startTest()
        }
        binding.speedView.prepareGauge(mContext)
        setupLineChart(mContext)
    }

    fun setupLineChart(context: Context) {
        Timber.e("SetupLineChartTimeOut: ${mViewModel.mTimeOut}")
        mListDownload = ArrayList()
        mListUpload = ArrayList()
        //Dummy line
        val list = ArrayList<Entry>()
        list.add(Entry(0f, 0f))
        list.add(Entry((mViewModel.mTimeOut * 1000).toFloat(), 0f))
        val dummydataset = LineDataSet(list, "Speed")
        dummydataset.setDrawCircles(false)
        dummydataset.color = ContextCompat.getColor(context, R.color.colorBackground)
        dummydataset.mode = LineDataSet.Mode.CUBIC_BEZIER
        //End dummy line

        val downloadDataset = LineDataSet(mListDownload, "Speed")
        downloadDataset.setDrawCircles(false)
        downloadDataset.color = ContextCompat.getColor(context, R.color.download)
        downloadDataset.mode = LineDataSet.Mode.CUBIC_BEZIER
        downloadDataset.lineWidth = 2F


        val uploadDataSet = LineDataSet(mListUpload, "Speed")
        uploadDataSet.setDrawCircles(false)
        uploadDataSet.color = ContextCompat.getColor(context, R.color.upload)
        uploadDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        downloadDataset.lineWidth = 2F

        mLineDataset = ArrayList()
        mLineDataset.add(dummydataset)
        mLineDataset.add(downloadDataset)
        mLineDataset.add(uploadDataSet)
        mLineData = LineData(mLineDataset)

        binding.linechartStrength.data = mLineData
        binding.linechartStrength.apply {
            xAxis.setDrawGridLines(false)
            xAxis.setDrawAxisLine(false)
            axisLeft.setDrawGridLines(false)
            axisLeft.setDrawAxisLine(false)
            axisRight.setDrawGridLines(false)
            axisRight.setDrawAxisLine(false)
            animateX(3000, Easing.Linear)
            description.isEnabled = false
            legend.isEnabled = false
            setDrawBorders(false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mViewModel.stopTesting()
    }

    fun startTest() {
        mViewModel.startPing("www.google.com")
        val downloaddataset = binding.linechartStrength
            .data.getDataSetByIndex(1) as LineDataSet
        downloaddataset.values = ArrayList<Entry>()
        val uploaddataset = binding.linechartStrength
            .data.getDataSetByIndex(2) as LineDataSet
        uploaddataset.values = ArrayList<Entry>()
        binding.txtNumberDownload.text = "--"
        binding.txtNumberUpload.text = "--"
        mUrl = mUrl.replace(
            mUrl.split("/").toTypedArray()[mUrl.split("/")
                .toTypedArray().size - 1],
            ""
        )
        mUrl = mUrl.replace("http://", "https://")
        mViewModel.startDownloadTest(mUrl)
    }

    fun animateText(view: View) {
        val anim: Animation = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 1000 //You can manage the blinking time with this parameter
        anim.startOffset = 20
        anim.repeatMode = Animation.REVERSE
        anim.repeatCount = Animation.INFINITE
        view.startAnimation(anim)
    }

    fun alphaHide(view: View) {
        val animator = ValueAnimator.ofFloat(1.0f, 0.0f)
        animator.duration = 2000
        animator.addUpdateListener { animation ->
            val alpha = animation.animatedValue as Float
            view.alpha = alpha
        }
        animator.start()
    }

    suspend fun saveTest(test: Test) {
        mTestRepo.insert(test)
    }

    fun showAdd() {
        YoYo.with(Techniques.FadeOutUp)
            .delay(500)
            .onEnd {
                binding.speedView.visibility = View.GONE
                binding.containerAdd.visibility = View.VISIBLE
                YoYo.with(Techniques.FadeInDown)
                    .duration(700)
                    .playOn(binding.containerAdd)
            }
            .duration(700)
            .playOn(binding.speedView)

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