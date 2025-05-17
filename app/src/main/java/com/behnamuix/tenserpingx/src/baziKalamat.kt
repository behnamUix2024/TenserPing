import kotlinx.coroutines.delay

fun main() {
    var max = Int.MIN_VALUE
    var min = Int.MAX_VALUE
    var longestWord = ""
    var shortestWord = ""
    var count = 0
    val list = mutableListOf<String>()
    while (true) {
        print("enter your word:")
        val input = readln()
        list.add(input)
        if (input == "پایان") {
            break
        }

    }
    //manteghe mohasebe bozorgtarin kalame va kochaktarin kalame
    if (list.isNotEmpty()) {
        for (word in list) {
            if (word.length > max) {
                if(word.equals("پایان")){
                    longestWord=""
                }else{
                    max = word.length
                    longestWord = word
                }

            }
            if (word.length < min) {
                min = word.length
                shortestWord = word
            }
        }
    }
    count = list.size - 1
    println("all of the word entered:${count}")
    println("longest word is :$longestWord")
    println("shortest word is :$shortestWord")

}