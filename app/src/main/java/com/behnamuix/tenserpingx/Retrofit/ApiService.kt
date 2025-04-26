package com.behnamuix.tenserpingx.Retrofit

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("get_hist.php")

    fun getHist(): Call<ApiResponseJson>


    @GET("send_hist.php")
    fun sendHist(
        @Query("hist_date") date:String,
        @Query("hist_type") type:String,
        @Query("hist_ip") ip:String,
        @Query("hist_ping") ping:String
    ):Call<ApiResponse>
}