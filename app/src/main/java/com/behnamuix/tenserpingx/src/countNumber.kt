package com.behnamuix.tenserpingx.src

import kotlinx.coroutines.delay

suspend fun main(){
    val num= mutableListOf(2,4,5)
    var param=0
    for(e in num) {
        for(i in 0 ..  e) {
            print("\u001b[H\u001b[2J")
            System.out.flush()
            print(i)

            delay(1000)
            if(i==e){
                print("*")
            }
        }

    }

}