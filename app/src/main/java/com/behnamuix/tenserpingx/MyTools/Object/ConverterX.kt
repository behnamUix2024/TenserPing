package com.behnamuix.tenserpingx.MyTools.Object

object ConverterX {
    fun mbpsToKBpsConverter(vMbps: Double): Double {
        if (vMbps == 0.0) {
            return 0.0
        }
        val vKBps = vMbps * 1000
        return vKBps
    }

}