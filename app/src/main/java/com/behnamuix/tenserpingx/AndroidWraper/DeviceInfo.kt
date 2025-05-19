package com.behnamuix.tenserpingx.AndroidWraper

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
/**
 * دریافت Android ID دستگاه
 * (منحصر به فرد برای هر دستگاه + اپلیکیشن)
 */
object DeviceInfo {
    var androidId:String?=null
    @SuppressLint("HardwareIds")
    fun getAndroidId(ctx:Context):String{
        if(androidId==null){
            androidId=Settings.Secure.getString(
                ctx.contentResolver,
                Settings.Secure.ANDROID_ID
            )
        }
        return androidId?:""

    }

}