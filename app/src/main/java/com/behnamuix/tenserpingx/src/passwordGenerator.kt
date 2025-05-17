package com.behnamuix.tenserpingx.src

import java.util.Calendar
import kotlin.random.Random

fun main() {
    try {
        println("Password Generator(behnam)")
        for (i in 1..4) {
            print("Enter password szie(0-64):")
            val size = readln().toInt()
            val list = mutableListOf(1)
            for (i in 1..size - 1) {
                var randrom = Random.nextInt(1, 9)
                list.add(i, randrom)
            }
            val date = Calendar.getInstance()
            val h = date.get(Calendar.HOUR_OF_DAY)
            val m = date.get(Calendar.MINUTE)
            val s = date.get(Calendar.SECOND)
            val y = date.get(Calendar.YEAR)
            val month = date.get(Calendar.MONTH)
            val d = date.get(Calendar.DAY_OF_MONTH)

            println(
                "Your finally password is => ${
                    list.toString().replace(",", "")
                }  in [$y/$month/$d | $h:$m:$s ] "
            )
            println("======================================================")
        }
    } catch (e: NumberFormatException) {
        println("Enter number !")
    } catch (e: IndexOutOfBoundsException) {
        println("Enter number in valid range!")

    }


}