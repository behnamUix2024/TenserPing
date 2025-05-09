package com.behnamuix.tenserpingx.Network.Location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class UserLocationProvider(val ctx: Context) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(ctx)


    companion object {
        private const val TAG = "UserLocationProvider"
        private const val IP_LOOKUP_URL = "https://api.ipify.org/?format=json"
        private const val GEO_LOOKUP_URL = "http://ip-api.com/json/"
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val net = cm.activeNetwork ?: return false
        val actNw = cm.getNetworkCapabilities(net) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }

    suspend fun getUserIPAddress(): String? = withContext(Dispatchers.IO) {
        if (isNetworkAvailable()) {
            try {
                val jsonString = URL(IP_LOOKUP_URL).readText()
                val jsonObject = JSONObject(jsonString)
                return@withContext jsonObject.getString("ip")
            } catch (e: Exception) {
                Log.e(TAG, "Error in fetch IP address.")
                return@withContext null
            }
        } else {
            Log.w(TAG, "No Intenet Connection!")
            return@withContext null
        }
    }

    suspend fun getNetworkType(): String {
        val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (cm == null) {
            return "No network connection"
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val net = cm.activeNetwork ?: return "No active network"
            val capa = cm.getNetworkCapabilities(net) ?: return "No network capabilitis"
            return when {
                capa.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WIFI"
                capa.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    getCellularNetworkType()
                }

                capa.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"

                else -> "Unknown network type"
            }
        } else {
            @Suppress("DEPRECATION")
            val activeNetworkInfo = cm.activeNetworkInfo ?: return "No Active Network Info"
            @Suppress("DEPRECATION")
            return when (activeNetworkInfo.type) {
                ConnectivityManager.TYPE_WIFI -> "WiFi"
                ConnectivityManager.TYPE_MOBILE -> getDeprecatedMobileNetworkType(activeNetworkInfo.subtype)
                ConnectivityManager.TYPE_ETHERNET -> "Ethernet"
                else -> "Unknown Network Type"
            }
        }

    }

    @Suppress("DEPRECATION")
    private fun getDeprecatedMobileNetworkType(subtype: Int): String {
        return when (subtype) {
            TelephonyManager.NETWORK_TYPE_GPRS,
            TelephonyManager.NETWORK_TYPE_EDGE,
            TelephonyManager.NETWORK_TYPE_CDMA,
            TelephonyManager.NETWORK_TYPE_1xRTT,
            TelephonyManager.NETWORK_TYPE_IDEN -> "2G"

            TelephonyManager.NETWORK_TYPE_UMTS,
            TelephonyManager.NETWORK_TYPE_EVDO_0,
            TelephonyManager.NETWORK_TYPE_EVDO_A,
            TelephonyManager.NETWORK_TYPE_HSDPA,
            TelephonyManager.NETWORK_TYPE_HSUPA,
            TelephonyManager.NETWORK_TYPE_HSPA,
            TelephonyManager.NETWORK_TYPE_EVDO_B,
            TelephonyManager.NETWORK_TYPE_EHRPD,
            TelephonyManager.NETWORK_TYPE_HSPAP -> "3G"

            TelephonyManager.NETWORK_TYPE_LTE -> "4G"

            // NETWORK_TYPE_NR (5G) is only available on API level 29+
            else -> "Unknown Cellular Generation"
        }
    }

    private fun getCellularNetworkType(): String {
        val telephonyManager = ctx.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (ActivityCompat.checkSelfPermission(
                ctx,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            return when (telephonyManager.dataNetworkType) {
                TelephonyManager.NETWORK_TYPE_GPRS,
                TelephonyManager.NETWORK_TYPE_EDGE,
                TelephonyManager.NETWORK_TYPE_CDMA,
                TelephonyManager.NETWORK_TYPE_1xRTT,
                TelephonyManager.NETWORK_TYPE_IDEN -> "2G"

                TelephonyManager.NETWORK_TYPE_UMTS,
                TelephonyManager.NETWORK_TYPE_EVDO_0,
                TelephonyManager.NETWORK_TYPE_EVDO_A,
                TelephonyManager.NETWORK_TYPE_HSDPA,
                TelephonyManager.NETWORK_TYPE_HSUPA,
                TelephonyManager.NETWORK_TYPE_HSPA,
                TelephonyManager.NETWORK_TYPE_EVDO_B,
                TelephonyManager.NETWORK_TYPE_EHRPD,
                TelephonyManager.NETWORK_TYPE_HSPAP -> "3G"

                TelephonyManager.NETWORK_TYPE_LTE -> "4G"

                TelephonyManager.NETWORK_TYPE_NR -> "5G"

                else -> "Unknown Cellular Generation"

            }

        } else {
            return ctx.getString(R.string.access_denied)
        }

    }


}
