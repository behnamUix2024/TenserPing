package com.behnamuix.tenserpingx.src.ClassExample.a

open class Vehicle(
    val type: VehicleType,
    val color:String,
    val weight:Int
) {
    fun accelerate(){
        println("GO!")
    }
}