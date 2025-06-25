package com.behnamuix.tenserpingx.src.ClassExample.animal

class Dog(name:String,age:Int):Animal(name,age) {
    fun sound():AnimalSound{
        val s=AnimalSound.WOOOOOF
        return s
    }
}