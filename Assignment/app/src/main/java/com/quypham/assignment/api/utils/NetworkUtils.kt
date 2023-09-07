package com.quypham.assignment.api.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class NetworkUtils @Inject constructor(@ApplicationContext private val context: Context) {

    companion object {
        private var isNetworkAvailable = false
        fun isConnectedToInternet() = isNetworkAvailable
    }

    private val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    fun registerNetworkChanged() {
        val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
        connectivityManager?.registerNetworkCallback(networkRequest, networkCallback)
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        // network is available for use
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            isNetworkAvailable = true
        }

        // lost network connection
        override fun onLost(network: Network) {
            super.onLost(network)
            isNetworkAvailable = false
        }
    }
}