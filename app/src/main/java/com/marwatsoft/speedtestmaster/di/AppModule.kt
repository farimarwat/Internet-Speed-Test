package com.marwatsoft.speedtestmaster.di

import android.annotation.SuppressLint
import android.app.Application
import android.net.ConnectivityManager
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.google.firebase.analytics.FirebaseAnalytics
import com.marwatsoft.speedtestmaster.data.Test.TestDao
import com.marwatsoft.speedtestmaster.helpers.DATABASENAME_TEST
import com.marwatsoft.speedtestmaster.data.TestDataHelper
import com.marwatsoft.speedtestmaster.network.SpeedTestServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providesConnectivityManager(context: Application): ConnectivityManager {
        return ContextCompat.getSystemService(context,ConnectivityManager::class.java)
                as ConnectivityManager
    }

    @Singleton
    @Provides
    fun provideTestDataHelper(context: Application): TestDataHelper {
        return Room.databaseBuilder(
            context,
            TestDataHelper::class.java,
            DATABASENAME_TEST
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    @Singleton
    @Provides
    fun providesTestDao(dataHelper: TestDataHelper):TestDao{
        return dataHelper.testdao()
    }

    @SuppressLint("MissingPermission")
    @Singleton
    @Provides
    fun providesFirebaseAnalytics(context: Application):FirebaseAnalytics{
        return FirebaseAnalytics.getInstance(context)
    }
}