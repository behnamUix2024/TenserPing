package com.behnamuix.tenserpingx.src.ClassExample.MyMafiaSim

import com.behnamuix.tenserpingx.src.ClassExample.MyMafiaSim.Enums.ERoles


class Kargah():Players()  {
    fun getEstelam(name: String): Boolean {
        if(nr[name]==ERoles.MAFIA){
            return true
        }
        return false
    }
}