package com.behnamuix.tenserpingx.MyTools.`object`

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object VpnChecker {
     fun checkVpnState(ctx: Context): Boolean {
        val cm=ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val net=cm.activeNetwork?:return false
        val capa=cm.getNetworkCapabilities(net)?:return false
        return capa.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
    }
}