package com.behnamuix.tenserpingx.src.ClassExample.MafiSim

fun main() {
    println("Mafia Simulator v1.0.0")
    println("Coded by:ArcaDroid/BehnamUix")
    println("1404/03/03")
    loading()
    val player = listOf("behnam", "ghazal", "ArcaDroid", "mohamad", "pari", "tara","raha")
    val game = MafiaGame(player)
    startGameLoop(game,player)


}

fun startGameLoop(mafiaGame: MafiaGame, player: List<String>) {
    println(player.toString())
    while (true) {
        // مرحله شب
        nightPhase(mafiaGame)

        // بررسی پایان بازی
        if (mafiaGame.checkGameEnd()) break

        // مرحله روز
        dayPhase(mafiaGame)

        // بررسی پایان بازی
        if (mafiaGame.checkGameEnd()) break
    }

    // نمایش تاریخچه بازی
    println("\n Tarikhche kole  bazi:")
    mafiaGame.getGameLog().forEach { println(it) }
    mafiaGame.printFinalPlayersStatus()


}

fun nightPhase(game: MafiaGame) {
    game.startNight()

    // دریافت بازیکنان زنده
    val alivePlayers = game.getAlivePlayer()

    // اقدامات مافیا
    println("\n--- Eghdamat mafia ---")
    // نمایش بازیکنان زنده
    println("bazikonan zende:\n")
    game.getAlivePlayer().forEach { player ->
        println("${player.id}. ${player.name}")
    }
    val mafiaPlayers = alivePlayers.filter { it.role == Role.MAFIA || it.role == Role.GODFATHER }
    mafiaPlayers.forEach { mafia ->
        println("${mafia.name} (mafia) kio mikhay bokoshi:")
        val targetId = readln().toInt()
        game.mafiaAction(targetId)
    }

    // اقدام دکتر
    println("\n--- Eghdamat doctor ---")
    val doctor = alivePlayers.find { it.role == Role.DOCTOR }
    doctor?.let {
        println("doctor kio mikhay darman koni:")
        val targetId = readln().toInt()
        game.doctorAction(targetId)
    }

    // اقدام کارآگاه
    println("\n--- Eghdamat karagah ---")
    val detective = alivePlayers.find { it.role == Role.KARAGAH }
    detective?.let {
        println("karagah az ki mikhay estelam begiri:")
        val targetId = readln().toInt()
        val role = game.detectivAction(targetId)
        println("naghshe in bazikon: $role ast")
    }
}

fun dayPhase(game: MafiaGame) {
    game.startDay()

    // نمایش بازیکنان زنده
    println("bazikonan zende:\n")
    game.getAlivePlayer().forEach { player ->
        println("${player.id}. ${player.name}")
    }

    // رأی‌گیری
    println("\n--- raygiri baraye hazf bazikon ---")
    val votes = mutableMapOf<Int, Int>()
    game.getAlivePlayer().forEach { player ->
        println("${player.name} be ki mikhay ray bedi:")
        val vote = readln().toInt()
        votes[vote] = (votes[vote] ?: 0) + 1
    }

    // محاسبه رأی‌ها
    val maxVote = votes.maxByOrNull { it.value }
    maxVote?.let {
        game.voteToKill(it.key)
    }
}

private fun loading() {
    for (p in 0..100) {
        print("\r[${"#".repeat(p)}${" ".repeat(100 - p)}] $p%")
        Thread.sleep(50)
    }
    println() // New line after loading completes

}


class MafiaGame(private val playerNames: List<String>) {
    private val players = mutableListOf<Player>()
    private var dayCount = 0
    private var isDayTime = true
    private val gameLog = mutableListOf<String>()

    init {
        require(playerNames.size >= 6) { "Hadeaghal 6 bazikon niaz ast!" }
        setupRoles()
    }
    fun printFinalPlayersStatus() {
        println("\nvaziyat nahayi player ha:")
        println("---------------------------------")
        println("name\t|\tnaghsh\t|\tvaziat")
        println("---------------------------------")

        players.sortedBy { it.id }.forEach { player ->
            val status = if (player.isAlive) "+ zende " else "- morde "
            println("${player.name}\t|\t${player.role}\t|\t$status")
        }
    }
    private fun setupRoles() {
        val roles =
            when (playerNames.size) {
                6 -> listOf(
                    Role.MAFIA,
                    Role.MAFIA,
                    Role.SHAHR,
                    Role.SHAHR,
                    Role.DOCTOR,
                    Role.KARAGAH
                )

                7 -> listOf(
                    Role.MAFIA,
                    Role.MAFIA,
                    Role.SHAHR,
                    Role.SHAHR,
                    Role.DOCTOR,
                    Role.KARAGAH,
                    Role.SNIPER
                )

                8 -> listOf(
                    Role.MAFIA,
                    Role.MAFIA,
                    Role.GODFATHER,
                    Role.SHAHR,
                    Role.LOVER,
                    Role.DOCTOR,
                    Role.KARAGAH,
                    Role.ZEREHPOSH
                )

                9 -> listOf()
                else -> generateComplexRoles(playerNames.size)
            }.shuffled()
        players.addAll(playerNames.mapIndexed { index, name ->
            Player(index + 1, name, roles[index])
        })
        logGame("bazi ba ${playerNames.size} bazikon shoro shod!")
    }

    fun startNight() {
        require(isDayTime) { "shab shode ast!" }
        isDayTime = false
        dayCount++
        players.forEach { it.resetNightStatus() }
        logGame("shabe ${dayCount} shoro shod lotfan  hame chshm haye khod ra bebandid")
    }

    fun mafiaAction(playerId: Int) {
        require(!isDayTime) { "In amal faghat dar shab momken ast!" }
        val target = players.find { it.id == playerId }
            ?: throw IllegalArgumentException("bazikon yaft nashod")
        target.kill()
        logGame("team mafia tasmim gereft ${target.name} ra bokoshad")

    }

    fun doctorAction(playerId: Int): Boolean {
        require(!isDayTime) { "In amal faghat dar shab momken ast!" }

        return players.firstOrNull { it.id == playerId && !it.isAlive }?.let { target ->
            target.protect()
            logGame("[doctor ${target.name}ra darman kard")
            true // عمل موفقیت‌آمیز بود
        } ?: run {
            logGame("[khata bazikon $playerId yaft nashod ya morde ast!]")
            false // عمل ناموفق بود
        }
    }

    fun detectivAction(playerId: Int): Role {
        require(!isDayTime) { "In amal faghat dar shab momken ast!" }
        return players.find { it.id == playerId }?.role
            ?: throw IllegalArgumentException("bazikon yaft nashod")

    }

    fun startDay() {
        require(!isDayTime) { "Rooz shode ast!" }
        isDayTime = true
        val killedPlayers = players.filter { !it.isAlive && !it.hasUsedAbility }
        killedPlayers.forEach {
            logGame("${it.name} ba naghshe ${it.role} dar shab koshte shod")
        }
        if (checkGameEnd()) return
        logGame("rooze $dayCount bahs va goftegoo shoro shod")


    }

    fun voteToKill(playerId: Int) {
        require(isDayTime) { "raygiri faghat dar rooz emkan darad" }
        val target = players.find { it.id == playerId }
            ?: throw IllegalArgumentException("bazikon yaft nashod")
        target.kill()
        logGame("bazikonan be koshatn ${target.name} ray dadand!")
        if (checkGameEnd()) return


    }

    fun checkGameEnd(): Boolean {
        val mafiaAlive =
            players.count { it.isAlive && (it.role == Role.MAFIA || it.role == Role.GODFATHER) }
        val citizensAlive =
            players.count { it.isAlive && it.role != Role.MAFIA && it.role != Role.GODFATHER }
        return when {
            mafiaAlive == 0 -> {
                logGame("shahrvandan barande shodand!")
                true
            }

            mafiaAlive >= citizensAlive -> {
                logGame("mafia barande shod")
                true
            }

            else -> false
        }
    }

    fun getAlivePlayer(): List<Player> {
        return players.filter { it.isAlive }
    }

    fun getPlayerRole(playerId: Int): Role {
        return players.find { it.id == playerId }?.role
            ?: throw IllegalArgumentException("bazikon yaft nashod")
    }

    private fun generateComplexRoles(playerCount: Int): List<Role> {
        // منطق پیچیده‌تر برای بازی‌های بزرگتر
        val mafiaCount = playerCount / 3
        return buildList {
            repeat(mafiaCount) { add(Role.MAFIA) }
            add(Role.GODFATHER)
            add(Role.DOCTOR)
            add(Role.KARAGAH)
            repeat(playerCount - mafiaCount - 3) { add(Role.SHAHR) }
        }
    }

    private fun logGame(message: String) {
        gameLog.add(message)
        println(message)
    }

    fun getGameLog(): List<String> {
        return gameLog.toList()
    }

}