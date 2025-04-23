package com.behnamuix.tenserpingx.Retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("get_hist.php")
    fun getHist(): Call<ApiResponseJson>
}