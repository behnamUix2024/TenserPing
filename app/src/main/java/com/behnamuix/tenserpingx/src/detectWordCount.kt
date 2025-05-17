import kotlinx.coroutines.delay

fun main() {
    var foundWord = ""
    print("Enter your word list: ")
    val input = readln()
    val words = input.split(" ")
    print("Enter your word length: ")
    val size = readln().toInt()
    for (element in words) {
        if (element.length == size) {
            foundWord = element
            break
        }
    }
    if (foundWord.isNotEmpty()) {
        println("The word is: '$foundWord' with length: ${foundWord.length}")
    } else {
        println("No word found ")
    }

}