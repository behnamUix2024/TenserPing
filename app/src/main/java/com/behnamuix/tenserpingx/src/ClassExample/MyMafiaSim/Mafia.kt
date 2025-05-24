package com.behnamuix.tenserpingx.src.ClassExample.MafiaSim

import com.behnamuix.tenserpingx.src.ClassExample.MafiaSim.Enums.ERoles

class Mafia : Players() {
    fun shotPlayer(name: String): MutableMap<String, ERoles> {
        if (name in nr && !(nr[name] == ERoles.MAFIA)) {
            nr.remove(name)

        }else{
            println("Mafia nemitone khodesh shot kone!")
        }
        return nr
    }

}