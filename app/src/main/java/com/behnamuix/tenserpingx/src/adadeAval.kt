import kotlin.math.sqrt

fun main() {
    print("Enter your number:")
    val num = readln().toInt()
    if (num <= 1) {
        println("$num is not prime! ")
        return
    }

    val jazr = sqrt(num.toDouble()).toInt()
    var avalAst = true
    for (i in 2..jazr) {
        if (num % i == 0) {
            avalAst = false
            break
        }
    }

    if (avalAst) {
        println(" $num is a prime number.")
    } else {
        println(" $num is not prime!")
    }
}