package com.behnamuix.tenserpingx.Retrofit

import com.google.gson.annotations.SerializedName

data class ApiJsonPingResponse(
     val status: String,
     val data: List<String>? // یا نوع مناسب دیگر
)
