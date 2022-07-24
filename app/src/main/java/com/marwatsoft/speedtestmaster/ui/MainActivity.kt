package com.marwatsoft.speedtestmaster.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.NavController
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
        supportActionBar?.hide()
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.mainFragment,R.id.historyFragment,R.id.settingsFragment)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomnavigation.setupWithNavController(navController)
//        binding.bottomnavigation.setOnItemSelectedListener(object :NavigationBarView.OnItemSelectedListener{
//            override fun onNavigationItemSelected(item: MenuItem): Boolean {
//                return when(item.itemId){
//                    R.id.mainFragment ->{
//                        navController.popBackStack()
//                        navController.navigate(R.id.mainFragment)
//                        true
//                    }
//                    R.id.historyFragment ->{
//                        navController.popBackStack()
//                        navController.navigate(R.id.historyFragment)
//                         true
//                    }
//                    R.id.settingsFragment ->{
//                        navController.popBackStack()
//                        navController.navigate(R.id.settingsFragment)
//                         true
//                    }
//                    else -> {  false}
//                }
//            }
//
//        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {

        return mNavController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}