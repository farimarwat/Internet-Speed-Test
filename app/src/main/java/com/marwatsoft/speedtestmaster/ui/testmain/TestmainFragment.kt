package com.marwatsoft.speedtestmaster.ui.testmain

import android.animation.ValueAnimator
import android.app.AlertDialog
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
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.farimarwat.supergaugeview.SuperGaugeView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.gms.ads.LoadAdError
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.marwatsoft.speedtestmaster.R
import com.marwatsoft.speedtestmaster.databinding.FragmentTestmainBinding
import com.marwatsoft.speedtestmaster.helpers.DialogButtonClickListener
import com.marwatsoft.speedtestmaster.helpers.STDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pk.farimarwat.modernadmob.AdmobView
import pk.farimarwat.speedtest.models.TESTTYPE_DOWNLOAD
import pk.farimarwat.speedtest.models.TESTTYPE_UPLOAD
import pk.farimarwat.speedtest.models.TestingStatus
import timber.log.Timber

@AndroidEntryPoint
class TestmainFragment : Fragment() {
    lateinit var binding: FragmentTestmainBinding
    lateinit var mContext: Context
    val mViewModel: TestmainFragmentViewModel by viewModels()
    val mNavArgs: TestmainFragmentArgs by navArgs()
    lateinit var mUrl: String

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initGui()
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
                        }
                        is TestingStatus.Canceled -> {
                            binding.groupSpeedtest.visibility = View.GONE
                        }
                        is TestingStatus.Finished -> {
                            if (it.testtype == TESTTYPE_DOWNLOAD) {
                                binding.txtDownload.clearAnimation()
                                val mlastSpeed = mViewModel.mSpeed.value.toString()
                                binding.txtNumberDownload.text = mlastSpeed
                                delay(1000)
                                mViewModel.mSpeed.value = 0.0
                                binding.speedView.setProgress(0f)
                                mViewModel.startUploadTest(mUrl!!)
                            } else if (it.testtype == TESTTYPE_UPLOAD) {
                                binding.txtUpload.clearAnimation()
                                val mlastSpeed = mViewModel.mSpeed.value.toString()
                                binding.txtNumberUpload.text = mlastSpeed
                                delay(1000)
                                mViewModel.mSpeed.value = 0.0
                                binding.speedView.setProgress(0f)
                                showAdd()
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
    }

    fun initGui() {

        binding.speedView.prepareGauge(mContext)
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
            YoYo.with(Techniques.FadeInUp)
                .onStart {
                    binding.speedView.visibility = View.VISIBLE
                    YoYo.with(Techniques.FadeOutUp)
                        .duration(700)
                        .onEnd {
                            binding.containerAdd.visibility = View.GONE
                        }
                        .playOn(binding.containerAdd)
                }
                .duration(700)
                .onEnd {
                    startTest()
                }
                .playOn(binding.speedView)
        }

        setupLineChart(mContext)
    }

    fun setupLineChart(context: Context) {
        mListDownload = ArrayList()
        mListUpload = ArrayList()

        //Dummy line
        val list = ArrayList<Entry>()
//        for(i in 0..mViewModel.mTimeOut){
//            list.add(Entry(i.toFloat(),i.toFloat()))
//        }
        list.add(Entry(0f,0f))
        list.add(Entry(12000f,0f))
        val dummydataset = LineDataSet(list,"Speed")
        dummydataset.setDrawCircles(false)
        dummydataset.color = ContextCompat.getColor(context, R.color.colorBackground)
        dummydataset.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        //End dummy line

        val downloadDataset = LineDataSet(mListDownload, "Speed")
        downloadDataset.setDrawCircles(false)
        downloadDataset.color = ContextCompat.getColor(context, R.color.download)
        downloadDataset.mode = LineDataSet.Mode.HORIZONTAL_BEZIER


        val uploadDataSet = LineDataSet(mListUpload, "Speed")
        uploadDataSet.setDrawCircles(false)
        uploadDataSet.color = ContextCompat.getColor(context, R.color.upload)
        uploadDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

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
            animateX(3000,Easing.Linear)
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

    suspend fun loadBannerAd() {
        binding.myads
            .loadAd(mContext, "ca-app-pub-3940256099942544/1044960115",
                object : AdmobView.ModernAdmobListener {
                    override fun onAdClicked() {

                    }

                    override fun onAdClosed() {

                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        Timber.e(error.message)
                    }

                    override fun onAdImpression() {
                        TODO("Not yet implemented")
                    }

                    override fun onAdLoaded() {
                        Timber.e("Add Loaded")
                    }

                    override fun onAdOpened() {
                        TODO("Not yet implemented")
                    }

                })
    }
}