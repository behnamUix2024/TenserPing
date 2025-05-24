package com.behnamuix.tenserpingx.src.ClassExample.MafiaSim

import com.behnamuix.tenserpingx.src.ClassExample.MafiaSim.Enums.ERoles
import kotlinx.coroutines.delay

open class Players() {
    var nr = mutableMapOf(
        "Ali" to ERoles.MAFIA,
        "Sara" to ERoles.DOCTOR,
        "Mohamad" to ERoles.SNIPER,
        "Zahra" to ERoles.SHAHR,
        "Behnam" to ERoles.KAREGAH
    )

    suspend fun pakhshsRole() {
        println("Dar hale pakhsh role ha:")
        for ((name, role) in nr) {
            println("[$name->$role]")
            delay(1000)
        }

    }

    suspend fun vote():String {
        var vote = mutableMapOf(
            "Ali" to 1,
            "Sara" to 1,
            "Mohamad" to 3,
            "Zahra" to 4,
            "Behnam" to 0
        )
        for ((name, v) in vote) {
            println("$name meghdare : $v ray avord!")
            delay(1000)


        }
        return (vote.maxBy { it.key }).toString()

    }
}