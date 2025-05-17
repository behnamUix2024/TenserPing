package com.behnamuix.tenserpingx.src

import kotlin.random.Random


fun main() {
    var ok = false
    val numPc = Random.nextInt(1, 1000)

    for (i in 1..6) {
        println("Enter your guesses number(1~1000)$numPc=>")
        val numUser = readln().toInt()
        if (numUser > numPc) {
            println("addad entehkabi man kochaktar ast.")
            continue
        } else if (numUser < numPc) {
            println("addad entehkabi man bozorgtar ast.")
            continue

        }
        if (numUser.equals(numPc)) {
            println("You are Winner")
            println("bazi tamam shod")
            ok = true
            break

        }
    }
    if (!ok) {
        println("You are Lose!")

    }


}