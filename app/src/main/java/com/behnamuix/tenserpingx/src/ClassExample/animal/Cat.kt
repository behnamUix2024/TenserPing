package com.behnamuix.tenserpingx.src.ClassExample.animal

class Cat(name:String,age:Int):Animal(name,age) {
    fun sound():AnimalSound{
        val s=AnimalSound.MEOW
        return s
    }
}