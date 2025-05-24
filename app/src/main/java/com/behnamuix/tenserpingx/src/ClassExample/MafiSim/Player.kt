package com.behnamuix.tenserpingx.src.ClassExample.MafiSim

data class Player(
    var id:Int,
    var name:String,
    var role:Role,
    var isAlive:Boolean=true,
    var isProtected:Boolean=false,
    var hasUsedAbility:Boolean=false
){
    fun kill(){
        if(!isProtected){
            isAlive=false
        }
    }
    fun protect() {
        isProtected = true
        // برای اطمینان از کارکرد:
        println("${name} protected: $isProtected") // دیباگ
    }
    fun resetNightStatus(){
        isProtected=false
    }
}
