package chap5

sealed class FunStream<out T> {
    object Nil : FunStream<Nothing>()
    data class Cons<out T>(val head: () -> T, val tail: () -> FunStream<T>) : FunStream<T>() {
        override fun equals(other: Any?): Boolean {
            return if (other is Cons<*>) {
                if (head() == other.head()) {
                    tail() == other.tail()
                } else {
                    false
                }
            } else {
                false
            }
        }

        override fun hashCode(): Int {
            var result = head.hashCode()
            result = 31 * result + tail.hashCode()
            return result
        }
    }
}

fun <T> FunStream<T>.getHead(): T = when (this) {
    FunStream.Nil -> throw NoSuchElementException()
    is FunStream.Cons -> head()
}

fun <T> FunStream<T>.getTail(): FunStream<T> = when (this) {
    FunStream.Nil -> throw NoSuchElementException()
    is FunStream.Cons -> tail()
}

tailrec fun <T, R> FunStream<T>.foldLeft(acc: R, f: (R, T) -> R): R = when (this) {
    FunStream.Nil -> acc
    is FunStream.Cons -> tail().foldLeft(f(acc, head()), f)
}

fun <T> funStreamOf(vararg elements: T): FunStream<T> = elements.toFunStream()

private fun <T> Array<out T>.toFunStream(): FunStream<T> = when {
    this.isEmpty() -> FunStream.Nil
    else -> FunStream.Cons({ this[0] }, { this.copyOfRange(1, this.size).toFunStream() })
}

fun <T> FunStream<T>.dropWhile(p: (T) -> Boolean): FunStream<T> = when (this) {
    FunStream.Nil -> this
    is FunStream.Cons -> {
        if (p(getHead())) this else getTail().dropWhile(p)
    }
}

// 5.17 sum 함수를 추가하라.
fun FunStream<Int>.sum(): Int = foldLeft(0) { it, it2 -> it + it2 }

// 5.18 product 함수를 추가하라.
fun FunStream<Int>.product(): Int = foldLeft(1) { it, it2 -> it * it2 }

// 5.19 appendTail 함수를 추가하라.
fun <T> FunStream<T>.appendTail(value: T): FunStream<T> = when (this) {
    FunStream.Nil -> FunStream.Cons({ value }, { FunStream.Nil })
    is FunStream.Cons -> FunStream.Cons(head, { tail().appendTail(value) })
}

// 5.20 filter 함수를 추가하라.
fun <T> FunStream<T>.filter(p: (T) -> Boolean): FunStream<T> = when (this) {
    FunStream.Nil -> FunStream.Nil
    is FunStream.Cons -> {
        val first = dropWhile(p)
        if (first == FunStream.Nil) FunStream.Nil else FunStream.Cons({ first.getHead() }, { first.getTail().filter(p) })
    }
}

// 5.21 map 함수를 추가하라.
fun <T, R> FunStream<T>.map(f: (T) -> R): FunStream<R> = when (this) {
    FunStream.Nil -> FunStream.Nil
    is FunStream.Cons -> {
        FunStream.Cons({ f(getHead()) }, { getTail().map(f) })
    }
}

fun main() {
    require(funStreamOf(1, 2, 3).sum() == 6)
    require(funStreamOf(2, 3, 4).product() == 24)
    require(funStreamOf(1, 2, 3).appendTail(4) == funStreamOf(1, 2, 3, 4))

    require(funStreamOf(1, 2, 3).filter { it >= 2 } == funStreamOf(2, 3))
    require(funStreamOf(1, 2, 3).map { it * it } == funStreamOf(1, 4, 9))
    require(funStreamOf("a", "b", "c").map { it.toUpperCase() } == funStreamOf("A", "B", "C"))
}
