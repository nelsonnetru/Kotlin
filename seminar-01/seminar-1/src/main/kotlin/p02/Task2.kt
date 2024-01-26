package p02

fun printOperationResult(operation: Operation) {
    val result = operation.calculate()
    println(result)
}

interface Operation {
    fun calculate(): Double
}

class Value(val d: Double) : Operation {
    override fun calculate(): Double  = this.d
}

class Plus(val left: Operation, val right: Operation) : Operation {
    override fun calculate(): Double = left.calculate() + right.calculate()
}

class Multiply(val left: Operation, val right: Operation) : Operation {
    override fun calculate(): Double = left.calculate() * right.calculate()
}

fun main() {
    // Соответствует формуле 4 + 2.5 * 2
    printOperationResult(
        Plus(
            Value(4.0),
            Multiply(
                Value(2.5),
                Value(2.0)
            )
        )
    )

    // Соответствует формуле (1 + 3.5) * (2.5 + 2)
    printOperationResult(
        Multiply(
            Plus(
                Value(1.0),
                Value(3.5)
            ),
            Plus(
                Value(2.5),
                Value(2.0)
            )
        )
    )

    // Соответствует формуле 10
    printOperationResult(
        Value(10.0)
    )

}