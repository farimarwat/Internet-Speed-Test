package com.marwatsoft.speedtestmaster.ui.map

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.marwatsoft.speedtestmaster.R
import com.marwatsoft.speedtestmaster.databinding.FragmentMapBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pk.farimarwat.speedtest.models.STProvider
import pk.farimarwat.speedtest.models.STServer

class MapFragment : Fragment(), OnMapReadyCallback , GoogleMap.OnCameraIdleListener{
    lateinit var mContext:Context
    lateinit var binding:FragmentMapBinding
    val mNavArgs:MapFragmentArgs by navArgs()
    var mServer:STServer? = null
    var mProvider:STProvider? = null
    private lateinit var mMap: GoogleMap
    var isAnimatedToServer = false
    var mIconProvider: BitmapDescriptor? = null
    var mIconServer: BitmapDescriptor? = null
    val mLocations by lazy { mutableListOf<LatLng>() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mContext = requireContext()
        binding = FragmentMapBinding.inflate(inflater,container,false)
        mServer = mNavArgs.server
        mProvider = mNavArgs.provider
        loadIcons()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initGui()
    }
    fun initGui(){
        val mapFragment = this.childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    override fun onMapReady(googlemap: GoogleMap) {
        mMap = googlemap
        val mapStyleOptions = MapStyleOptions.loadRawResourceStyle(mContext,R.raw.map)
        mMap.setMapStyle(mapStyleOptions)

        mMap.setOnCameraIdleListener(this)
        mProvider?.let { provider ->
            val provider_location  = LatLng(provider.lat?.toDouble()!!,provider.lon?.toDouble()!!)
            mLocations.add(provider_location)
            mMap.addPolyline(PolylineOptions()
                .add(provider_location))

            mMap.addMarker(
                MarkerOptions()
                    .position(provider_location)
                    .title(provider.providername)
                    .icon(mIconProvider)
            )
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(provider_location, 12f))
        }

    }

    override fun onCameraIdle() {
        if(!isAnimatedToServer){
            mServer?.let { server ->
                val server_location  = LatLng(server.lat?.toDouble()!!,server.lon?.toDouble()!!)
                mLocations.add(server_location)
                mMap.addPolyline(PolylineOptions().addAll(mLocations))
                mMap.addMarker(
                    MarkerOptions()
                        .position(server_location)
                        .title(server.name)
                        .icon(mIconServer)
                )
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(server_location, 12f))
            }

            isAnimatedToServer = true
        }
    }

    fun loadIcons(){
        CoroutineScope(Dispatchers.IO).launch {
            mIconProvider = BitmapDescriptorFactory.fromResource(R.drawable.provider)
            mIconServer = BitmapDescriptorFactory.fromResource(R.drawable.server)
        }
    }
}