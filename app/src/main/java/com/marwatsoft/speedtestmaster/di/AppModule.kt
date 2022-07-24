package com.marwatsoft.speedtestmaster.di

import android.app.Application
import android.net.ConnectivityManager
import androidx.core.content.ContextCompat
import androidx.room.Room
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
    val BASE_URL_SPEEDTEST = "https://www.speedtest.net/"
    @Provides
    @Singleton
    fun providesSpeedTestServices():SpeedTestServices{
        return Retrofit.Builder()
            .baseUrl(BASE_URL_SPEEDTEST)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpeedTestServices::class.java)
    }
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
}