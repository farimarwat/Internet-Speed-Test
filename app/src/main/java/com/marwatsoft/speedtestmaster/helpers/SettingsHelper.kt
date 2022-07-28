package com.marwatsoft.speedtestmaster.helpers

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsHelper @Inject constructor(val context: Application)  {

    companion object {
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
        val CONNECTIONS = intPreferencesKey("connections")
        val TIMEOUT= intPreferencesKey("timeout")
        val SERVER_TYPE = stringPreferencesKey("servertype")

        val SERVERS_PREMIUM = "premium"
        val Servers_PUBLIC = "public"
        val CONNECTION_TYPE_SINGLE = 1
        val CONNECTION_TYPE_MULTIPLE = 2
    }
    val connection:Flow<Int> = context.dataStore.data.map { pref ->
        pref[CONNECTIONS] ?: CONNECTION_TYPE_MULTIPLE
    }
    suspend fun storeConnection(type:Int){
        context.dataStore.edit { pref ->
            pref[CONNECTIONS] = type
        }
    }

    val timeout:Flow<Int> = context.dataStore.data.map { pref ->
        pref[TIMEOUT] ?: 20
    }

    suspend fun storeTimeout(timeout:Int){
        context.dataStore.edit { pref->
            pref[TIMEOUT] = timeout
        }
    }

    val servertype:Flow<String> = context.dataStore.data.map { pref ->
        pref[SERVER_TYPE] ?: SERVERS_PREMIUM
    }
    suspend fun storeServerType(type:String){
        context.dataStore.edit { pref ->
            pref[SERVER_TYPE] = type
        }
    }
}