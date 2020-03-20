package com.example.trashdetector.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object InternetUtils {

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val networkStatus =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        return when {
            networkStatus.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            networkStatus.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            networkStatus.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}
