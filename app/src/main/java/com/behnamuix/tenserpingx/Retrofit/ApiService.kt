package com.behnamuix.tenserpingx.Retrofit

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("get_hist.php")
    fun getHist(
        @Query("hist_mac") mac: String
    ): Call<ApiResponseJson>

    @GET("get_ping.php")
    suspend fun getPing(@Query("hist_mac") mac: String): ApiJsonPingResponse

    @GET("send_hist.php")
    fun sendHist(
        @Query("hist_mac") mac: String,
        @Query("hist_date") date: String,
        @Query("hist_type") type: String,
        @Query("hist_ip") ip: String,
        @Query("hist_ping") ping: String
    ): Call<ApiResponse>

    @GET("send_purchase.php")
    fun insertPurchaseLog(
        @Query("p_mac") p_mac: String,
        @Query("p_time") p_time: String,
        @Query("p_sku") p_sku: String,
        @Query("p_token") p_token: String,
        @Query("p_sig") p_sig: String,
        @Query("verify") verify: String
    ): Call<ApiResponse>
    @GET("check_verify.php")
    fun checkVerify(
        @Query("p_mac") p_mac:String
    ):Call<ApiResponseCheckVerifyJson>
}