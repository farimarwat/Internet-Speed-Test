package com.marwatsoft.speedtestmaster.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.MobileAds
import com.google.android.material.navigation.NavigationBarView
import com.marwatsoft.speedtestmaster.R
import com.marwatsoft.speedtestmaster.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var mNavController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        mNavController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.mainFragment,R.id.historyFragment,R.id.settingsFragment)
        )
        setupActionBarWithNavController(mNavController, appBarConfiguration)
        binding.bottomnavigation.setupWithNavController(mNavController)
        mNavController.addOnDestinationChangedListener(object:NavController.OnDestinationChangedListener{
            override fun onDestinationChanged(
                controller: NavController,
                destination: NavDestination,
                arguments: Bundle?
            ) {
                when(destination.id){
                    R.id.mainFragment ->{
                        supportActionBar?.hide()
                        binding.bottomnavigation.visibility = View.VISIBLE
                    }
                    R.id.mapFragment ->{
                        binding.bottomnavigation.visibility = View.GONE
                        supportActionBar?.show()
                    }
                    R.id.testmainFragment ->{
                        binding.bottomnavigation.visibility = View.GONE
                        supportActionBar?.show()
                    }
                    else ->{
                        binding.bottomnavigation.visibility = View.VISIBLE
                        supportActionBar?.show()
                    }
                }
            }
        })
    }


    override fun onSupportNavigateUp(): Boolean {

        return mNavController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}