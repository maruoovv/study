package chap7

interface Functor<out A> {
    fun <B> fmap(f: (A) -> B) : Functor<B>
    fun first() : A
    fun size() : Int
}

sealed class FunList<out T> : Functor<T> {
    abstract override fun <B> fmap(f: (T) -> B): FunList<B>
    abstract override fun first() : T
    abstract override fun size() : Int
}

object Nil : FunList<Nothing>() {
    override fun <B> fmap(f: (Nothing) -> B): FunList<B> = Nil

    override fun first(): Nothing = throw NoSuchElementException()

    override fun size(): Int = 0
}

data class Cons<T> (val head: T, val tail : FunList<T>) : FunList<T> () {
    override fun <B> fmap(f: (T) -> B): FunList<B> = Cons(f(head), tail.fmap(f))

    override fun first(): T = head

    override fun size(): Int = 1 + tail.size()
}


fun main() {
    val funList: FunList<Int> = Cons(1, Cons(2, Cons(3, Nil)))

    require(funList.fmap { it * 2 } ==
            Cons(2, Cons(4, Cons(6, Nil))))
    require(funList.first() == 1)
    require(funList.size() == 3)

    val funList2: FunList<Int> = Nil

    require(funList2.fmap { it * 2 } == Nil)
    require(funList2.size() == 0)
}
