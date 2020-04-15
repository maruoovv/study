package chap7

interface EitherFunctor<out A> {
    fun <B> fmap(f: (A) -> B) : EitherFunctor<B>
}

sealed class Either <out L, out R> : EitherFunctor<R> {
    abstract override fun <R2> fmap(f: (R) -> R2) : Either<L, R2>
}

data class Left<out L>(val value: L): Either<L, Nothing>() {
    override fun <R2> fmap(f: (Nothing) -> R2): Either<L, R2> = this
}

data class Right<out R>(val value: R): Either<Nothing, R>() {
    override fun <R2> fmap(f: (R) -> R2): Either<Nothing, R2> = Right(f(value))
}

fun divideTenByN(n: Int): Either<String, Int> = try {
    Right(10 / n)
} catch(e: ArithmeticException) {
    Left("Divide by zero")
}

fun main() {
    println(divideTenByN(5))
    println(divideTenByN(0))
    println(divideTenByN(5).fmap { it*it })
    println(divideTenByN(0).fmap { it*it })
}
