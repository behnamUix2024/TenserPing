package com.behnamuix.tenserpingx.src.ClassExample.MyMafiaSim

import com.behnamuix.tenserpingx.src.ClassExample.MyMafiaSim.Enums.ERoles
import kotlinx.coroutines.delay

suspend fun main() {
    val start = Start()
    start.main()
}

class Start() {
    var b = true
    var nnr: MutableMap<String, ERoles>? = null
    var nr: MutableMap<String, ERoles>? = null
    val player = Players()
    val kargah = Kargah()
    val doctor = Doctor()
    val mafia = Mafia()
    suspend fun main() {
        while (b) {
            println("************************")
            println("*Mafia Simulator v1.0.0*")
            println("************************")
            loading()
            //val player = Players(nr)
            delay(1000)

            println("List player ha:")
            nr = player.nr
            for (name in nr!!.keys) {
                println("$name")
                delay(1000)
            }
            delay(1000)
            println("============================================")
            delay(1000)
            player.pakhshsRole()
            delay(1000)
            println("============================================")
            delay(1000)
            phaseShab("Mohamad","Zahra")
        }


    }

    private fun loading() {
        for (p in 0..100) {
            print("\r[${"#".repeat(p)}${" ".repeat(100 - p)}] $p%")
            Thread.sleep(50)
        }
        println() // New line after loading completes

    }

    suspend fun phaseRooz(count:Int) {
        delay(1000)
        println("============================================")
        println("Rooz mishe:")
        delay(1000)
        println("rooz $count")
        delay(1000)
        println("ajab shabi dashtim")
        delay(1000)
        println("ama koshtei dar kar nabood")
        println("tedade player haye baghimande:")
        delay(1000)
        for (name in nr?.keys!!) {
            println(name)
        }
        println("Zamane raygiri fara reside:")
        println("============================================")
        delay(1000)
        println("Natige ara:")
        delay(1000)
        val ha=player.vote()
        println("Ba tvajoh be raygiri $ha ray az bazi kharej shod!")
        phaseShab("Ali","Behnam")
        b = false

    }

    suspend fun phaseShab(n:String,s:String) {
        delay(1000)
        println("============================================")
        println("Shab muishe:")
        delay(1000)
        println("kargah bidar she!")
        delay(1000)
        println("karagah az ki estelam migiri?")
        delay(1000)
        if (n.isEmpty()) {
            b = false
        }
        val est = kargah.getEstelam(n)
        if (est) {
            println("[estelame $n -> + ]")

        } else {
            println("[estelame $n -> - ]")

        }
        println("kregah bekhabe")
        delay(1000)
        println("mafia bidar she")
        delay(1000)
        println("mafia kio shot mikoni?")
        delay(1000)
        if (s.isEmpty()) {
            b = false
        }
        nnr = mafia.shotPlayer(s)
        println("$s shot shod!")
        //println("$nnr")
        delay(1000)
        println("mafia bekhab!")
        delay(1000)
        println("doctor bidar she")
        println("doctor kio save midi?")
        delay(1000)
        val x = "Zahra"
        if (x.isEmpty()) {
            b = false
        }
        if (s == x) {
            print("doctor bekhabe")
        }
        doctor.save(x)
        delay(1000)
        phaseRooz(1)


    }
}
