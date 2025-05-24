package com.behnamuix.tenserpingx.src

import com.behnamuix.tenserpingx.src.ClassExample.a.Bus
import com.behnamuix.tenserpingx.src.ClassExample.a.Car
import com.behnamuix.tenserpingx.src.ClassExample.a.Van
import com.behnamuix.tenserpingx.src.ClassExample.a.Vehicle
import com.behnamuix.tenserpingx.src.ClassExample.a.VehicleType

fun main(){
    val vehicle= Vehicle(VehicleType.CAR,"Black",150000)
    val mycar= Car(vehicle.type,vehicle.color,vehicle.weight,true)
    val myBus= Bus(vehicle.type,vehicle.color,vehicle.weight,false)
    val myVan= Van(vehicle.type,vehicle.color,vehicle.weight,true)
    println(mycar.accelerate())
    println(myBus.doorAutomatic)
    println(myVan.type)
}