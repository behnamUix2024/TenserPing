package com.behnamuix.tenserpingx.src.ClassExample.animal

fun main() {
    val cat1 = Cat("Keyti", 2)
    val cat2 = Cat("Po", 1)
    val dog1 = Dog("Jeny", 4)
    val dog2 = Dog("Ron", 3)

    cat1.info()
    println("sound:${cat1.sound()}\n===================")
    cat2.info()
    println("sound:${cat2.sound()}\n===================")
    dog1.info()
    println("sound:${dog1.sound()}\n===================")
    dog2.info()
    println("sound:${dog2.sound()}\n===================")

}