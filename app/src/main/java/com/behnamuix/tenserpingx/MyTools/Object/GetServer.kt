package com.behnamuix.tenserpingx.MyTools.Object

import kotlin.random.Random

object GetServer {
    private var instance: GetServer? = null

    fun getInstance(): GetServer {
        return instance ?: synchronized(this) {
            instance ?: GetServer.also { instance = it }
        }
    }

    fun getRandomServer(s1: String, s2: String): String {
        val list = mutableListOf(s1, s2)
        val randPos = Random.nextInt(0, 2)
        return list[randPos]
    }
}