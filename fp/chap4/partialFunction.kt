package chap4.partialFunction

// 4.12 예제에 invokeOrElse, orElse 함수 추가.

class PartialFunction <P, R> (
        private val condition : (P) -> Boolean,
        private val f : (P) -> R
) : (P) -> R {
    override fun invoke(p : P): R = when {
        condition(p) -> f(p)
        else -> throw IllegalArgumentException("$p isn't supported")
    }

    fun isDefinedAt(p : P) : Boolean = condition(p)

    fun invokeOrElse(p : P, default : R) : R = when {
        condition(p) -> f(p)
        else -> default
    }

    fun orElse(that : PartialFunction<P, R>) : PartialFunction<P, R> =
            PartialFunction({ it: P ->
                this.isDefinedAt(it) || that.isDefinedAt(it)
            },
                    { it: P ->
                        when {
                            this.isDefinedAt(it) -> this(it)
                            that.isDefinedAt(it) -> that(it)
                            else -> throw IllegalArgumentException()
                        }
                    }
            )
}

fun <P, R> ((P) -> R).toPartialFunction(definedAt: (P) -> Boolean) : PartialFunction<P, R> = PartialFunction(definedAt, this)

fun main(args : Array<String>) {
    val condition: (Int) -> Boolean = { it.rem(2) == 0 }
    val body : (Int) -> String = { "$it is even" }
    val body2 : (Int) -> String = { "$it is odd"}

    val isEven = body.toPartialFunction(condition)
    val isOdd = body2.toPartialFunction{ !condition(it) }

    println(isEven(100))
    println(isEven.invokeOrElse(1,  "not even"))
    println(isEven.orElse(isOdd))
    println(isEven(1))
}

