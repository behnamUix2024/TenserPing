package com.behnamuix.tenserping.Network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object NetworkCheck {
    fun isInternetAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val net = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(net) ?: return false
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
}