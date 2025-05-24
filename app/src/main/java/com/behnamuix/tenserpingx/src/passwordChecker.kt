package com.behnamuix.tenserpingx.src

fun main() {
    print("Enter your password:")
    val metereH = "your password is highSecurity"
    val metereL = "your password is lowSecurity"
    var pass = readln().toString()
    val p = passwordChecker(pass)
    if (p) {
        print(metereH)
    } else {
        print(metereL)
    }
}

fun passwordChecker(pass: String): Boolean {
    if (pass.length >= 8 &&
        pass.any { it.isDigit() } &&
        pass.contains("@") ||
        pass.contains("#") ||
        pass.contains("%")
    ) {

        return true
    }
    return false
}