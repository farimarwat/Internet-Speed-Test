package com.marwatsoft.speedtestmaster.ui.settings

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marwatsoft.speedtestmaster.helpers.SettingsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsFragmentViewModel @Inject constructor(
    val mContext:Application,
    val mSettings: SettingsHelper
):ViewModel() {
    val mConnectionType by lazy { MutableStateFlow(0) }
    val mTimeout by lazy { MutableStateFlow(0) }
    val mServerType by lazy { MutableStateFlow("") }
    init {
        getConnectionType()
        getTimeOut()
        getServerType()
    }

    fun getConnectionType() = viewModelScope.launch(Dispatchers.IO) {
        mSettings.connection.collect{
            mConnectionType.value = it
        }
    }
    fun getTimeOut() = viewModelScope.launch(Dispatchers.IO) {
        mSettings.timeout.collect{
            mTimeout.value = it
        }
    }
    fun getServerType() = viewModelScope.launch(Dispatchers.IO) {
        mSettings.servertype.collect{
            mServerType.value = it
        }
    }
    fun storeConnectionType(type:Int) = viewModelScope.launch(Dispatchers.IO) {
        mSettings.storeConnection(type)
    }
    fun storeTimeOut(timeout:Int) = viewModelScope.launch(Dispatchers.IO) {
        mSettings.storeTimeout(timeout)
    }
    fun storeServerType(type:String) = viewModelScope.launch(Dispatchers.IO) {
        mSettings.storeServerType(type)
    }

}