package com.behnamuix.tenserpingx.Model

data class PurchaseModel(
    val p_mac:String,
    val p_time:String,
    val p_sku:String,
    val p_token:String,
    val p_sig:String,
    val verify:String
)
