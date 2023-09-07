package com.quypham.assignment

import android.app.Application
import androidx.work.Configuration
import com.quypham.assignment.api.utils.NetworkUtils
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application() {
    @Inject lateinit var networkUtils: NetworkUtils

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        networkUtils.registerNetworkChanged()
    }

}