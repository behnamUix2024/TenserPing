package com.behnamuix.tenserpingx.MyTools

object ConverterX {
    fun mbpsToKbpsConverter(vMbps: Double): Int {
        if (vMbps == 0.0) {
            return 0
        }
        val vKbps = vMbps * 1000
        return vKbps.toInt()
    }

}