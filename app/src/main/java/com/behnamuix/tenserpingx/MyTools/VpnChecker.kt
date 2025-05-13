package com.behnamuix.tenserpingx.MyTools

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class VpnChecker {
     fun checkVpnState(ctx: Context): Boolean {
        val cm=ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val net=cm.activeNetwork?:return false
        val capa=cm.getNetworkCapabilities(net)?:return false
        return capa.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
    }
}