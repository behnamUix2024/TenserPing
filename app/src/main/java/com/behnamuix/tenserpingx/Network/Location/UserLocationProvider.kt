package com.behnamuix.tenserpingx.Network.Location

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
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

    // دریافت اطلاعات موقعیت مکانی از IP (نیاز به اینترنت)
    suspend fun getLocationFromIP(): Map<String, String>? = withContext(Dispatchers.IO) {
        val ipAddress = getUserIPAddress()
        if (ipAddress != null && isNetworkAvailable()) {
            try {
                val jsonString = URL(GEO_LOOKUP_URL + ipAddress).readText()
                val jsonObject = JSONObject(jsonString)
                val latitude = jsonObject.optString("lat")
                val longitude = jsonObject.optString("lon")
                val city = jsonObject.optString("city")
                val country = jsonObject.optString("country")
                return@withContext mapOf(
                    "ip" to ipAddress,
                    "latitude" to latitude,
                    "longitude" to longitude,
                    "city" to city,
                    "country" to country
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching location from IP: ${e.message}")
                return@withContext null
            }
        } else {
            Log.w(TAG, "Could not fetch IP or no internet to get location from IP.")
            return@withContext null
        }
    }
}