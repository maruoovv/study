package chap5

import chap5.FunList.*
import java.lang.IllegalArgumentException

sealed class FunList<out T> {
    object Nil : FunList<Nothing>()
    data class Cons<out T> (val head: T, val tail : FunList<T>) : FunList<T> ()
}

fun <T> FunList<T>.addHead(head : T) : FunList<T> = FunList.Cons(head, this)

fun <T> FunList<T>.appendTail(value : T) : FunList<T> = when(this) {
    Nil -> Cons(value, Nil)
    is Cons -> Cons(head, tail.appendTail(value))
}

tailrec fun <T> FunList<T>.appendTail(value : T, acc : FunList<T> = Nil) : FunList<T> = when(this) {
    Nil -> Cons(value, acc).reverse()
    is Cons -> tail.appendTail(value, acc.addHead(head))
}

tailrec fun <T> FunList<T>.reverse(acc : FunList<T> = Nil) : FunList<T> = when(this) {
    Nil -> acc
    is Cons -> tail.reverse(acc.addHead(head))
}

fun <T> FunList<T>.getTail() : FunList<T> = when(this) {
    Nil -> throw IllegalArgumentException()
    is Cons -> tail
}

fun <T> FunList<T>.getHead() : T = when(this) {
    Nil -> throw NoSuchElementException()
    is Cons -> head
}

tailrec fun <T> FunList<T>.filter(acc : FunList<T> = Nil, p: (T) -> Boolean) : FunList<T> = when(this) {
    Nil -> acc.reverse()
    is Cons -> if (p(head)) {
        tail.filter(acc.addHead(head), p)
    } else {
        tail.filter(acc, p)
    }
}

tailrec fun <T, R> FunList<T>.map(acc : FunList<R> = Nil, f : (T) -> R) : FunList<R> = when(this) {
    Nil -> acc.reverse()
    is Cons -> tail.map(acc.addHead(f(head)), f)
}

fun <T> funListOf (vararg elements : T) : FunList<T> = elements.toFunList()

private fun <T> Array<out T>.toFunList(): FunList<T> = when {
    this.isEmpty() -> Nil
    else -> Cons(this[0], this.copyOfRange(1, this.size).toFunList())
}

tailrec fun <T, R> FunList<T>.foldLeft(acc: R, f : (R, T) -> R) : R = when (this) {
    Nil -> acc
    is Cons -> tail.foldLeft(f(acc, head), f)
}

// 5.4 앞의 값이 N개 제외된 리스트를 반환하는 함수를 구현하라.
// 원본 리스트는 바뀌지 않고, 새로운 리스트를 반환할 때마다 새로운 리스트를 생성하면 안된다.

tailrec fun <T> FunList<T>.drop(n : Int) : FunList<T> = when(n) {
    1 -> this.getTail()
    else -> this.getTail().drop(n - 1)
}

// 5.5 타입 T를 입력 받아 Boolean 을 반환하는 함수p 를 입력받아, 리스트의 앞에서부터 함수 p 를 만족하기 전까지 drop 하고 나머지 리스트를 반환하라.
// 원본 리스트는 바뀌지 않아야 하고, 새로운 리스트를 반환할 때마다 리스트를 생성하면 안 된다.

tailrec fun <T> FunList<T>.dropWhile(p : (T) -> Boolean) : FunList<T> = when(this) {
    Nil -> throw NoSuchElementException()
    is Cons -> if (p(this.head)) {
        this
    } else {
        this.getTail().dropWhile(p)
    }
}

// 5.6 리스트의 앞에서부터 N개의 값을 가진 리스트를 반환하는 take 함수를 구현하라.
// 원본 리스트는 바뀌지 않아야 하고, 새로운 리스트를 반환할 때마다 리스트를 생성하면 안 된다.
tailrec fun <T> FunList<T>.take(n : Int, acc : FunList<T> = Nil) : FunList<T> = when(n) {
    0 -> acc.reverse()
    else -> {
        this.getTail().take(n - 1, acc.addHead(this.getHead()))
    }
}

// 5.7 T를 입력받아 Boolean 을 반환하는 함수 p를 받는다. 리스트의 앞에서부터 함수 p 를 만족하는 값들의 리스트를 반환하라.
// 모든값이 만족하지 않으면 원본을 반환.
// 원본 리스트는 바뀌지 않아야 하고, 새로운 리스트를 반환할 때마다 리스트를 생성하면 안 된다.

tailrec fun <T> FunList<T>.takeWhile(acc : FunList<T> = Nil, p : (T) -> Boolean) : FunList<T> = when(this) {
    Nil -> if (acc == Nil) this else acc.reverse()
    is Cons -> {
        if (p(this.getHead())) {
            this.getTail().takeWhile(acc.addHead(this.getHead()), p)
        } else {
            this.getTail().takeWhile(acc, p)
        }
    }
}

// 5.8 map 함수에서 고차 함수가 값들의 순서값도 같이 받아 올 수 있는 indexedMap 을 작성하라.

tailrec fun <T, R> FunList<T>.indexedMap(index : Int = 0, acc : FunList<R> = Nil, f : (Int, T) -> R) : FunList<R> = when(this) {
    Nil -> acc.reverse()
    is Cons -> tail.indexedMap(index + 1, acc.addHead(f(index, head)), f)
}

// 5.9 3장에서 작성한 maximum 함수를 foldLeft 함수를 사용해서 다시 작성해보자
fun FunList<Int>.maximumByFoldLeft() : Int = this.foldLeft(0) {
    acc, elem -> if (acc > elem) acc else elem
}

// 5.10 filter 함수를 foldLeft 함수를 사용해서 다시 작성해 보자
fun <T> FunList<T>.filterByFoldLeft(p : (T) -> Boolean) : FunList<T> = foldLeft(Nil) {
    acc : FunList<T>, elem -> if(p(elem)) acc.appendTail(elem) else acc
}

fun <T, R> FunList<T>.foldRight(acc : R, f : (T, R) -> R) : R = when(this) {
    Nil -> acc
    is Cons -> f(head, tail.foldRight(acc, f))
}

// 5.11 3장에서 작성한 reverse 함수를 foldRight 함수를 사용해 작성하라
fun <T> FunList<T>.reverseByFoldRight() : FunList<T> = foldRight(Nil as FunList<T>) {
   x, acc -> acc.appendTail(x)
}

// 5.12 filter 함수를 foldRight 를 사용하여 다시 작성하라.
fun <T> FunList<T>.filterByFoldRight(p : (T) -> Boolean): FunList<T> = foldRight(Nil as FunList<T>) {
    x, acc -> if (p(x)) acc.appendTail(x) else acc
}

fun main() {
    val intList = Cons(1, Cons(2, Cons(3, Cons(4, Cons(5, Nil)))))
    val doubleList = Cons(1.0, Cons(2.0, Cons(3.0, Cons(4.0, Cons(5.0, Nil)))))

    require(intList.getHead() == 1)
    require(intList.getTail() == funListOf(2,3,4,5))

    require(doubleList.getHead() == 1.0)
    require(doubleList.getTail() == funListOf(2.0, 3.0, 4.0, 5.0))

    require(intList.drop(2) == funListOf(3,4,5))

    require(intList.dropWhile { it > 2 } == funListOf(3,4,5))

    require(intList.take(3) == funListOf(1,2,3))

    require(intList.takeWhile { it > 3 } == funListOf(4, 5))
    require(intList.takeWhile { it > 5 } == Nil)

    require(intList.indexedMap{index, elem -> elem + index} == funListOf(1,3,5,7,9))

    require(intList.maximumByFoldLeft() == 5)

    require(intList.filterByFoldLeft { it > 3 } == funListOf(4, 5))

    require(intList.reverseByFoldRight() == funListOf(5,4,3,2,1))

    require(intList.filterByFoldRight { it > 3 } == funListOf(5, 4))
}