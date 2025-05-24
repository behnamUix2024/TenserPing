package com.behnamuix.tenserpingx.src.ClassExample.MafiaSim

import com.behnamuix.tenserpingx.src.ClassExample.MafiaSim.Enums.ERoles


class Kargah():Players()  {
    fun getEstelam(name: String): Boolean {
        if(nr[name]==ERoles.MAFIA){
            return true
        }
        return false
    }
}