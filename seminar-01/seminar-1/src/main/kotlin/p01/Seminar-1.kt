fun main(args: Array<String>) {
    println("sumAll = ${sumAll(1, 5, 20)}")
    println("sumAll = ${sumAll()}")
    println("sumAll = ${sumAll(2, 3, 4, 5, 6, 7)}")
    println(createOutputString("Alice"))
    println(createOutputString("Bob", 23))
    println(createOutputString(isStudent = true, name = "Carol", age = 19))
    println(createOutputString("Daniel", 32, isStudent = null))
    println(multiplyBy(null, 4))
    println(multiplyBy(3, 4))
    stars(1, 2, 4)
}

fun sumAll(vararg args: Int): Int = args.sum()

fun createOutputString(name: String, age: Int = 42, isStudent: Boolean? = null): String {
    var student = ""
    if (isStudent == true)
        student = "student "
    return "${student}${name} has age of $age"
}

fun multiplyBy(a: Int?, b: Int): Int? {
    return if (a != null) a * b
    else null
}

fun stars(a: Int, b: Int, c: Int) {
    var col: Int = a
    var d = 1
    for (l in 0..(b * 2)) {
        for (i in 0 until col) {
            print("*")
        }
        if (l >= b) d = -1
        print("\n")
        col += c * d
    }
}