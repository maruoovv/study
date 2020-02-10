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


fun main() {
    val intList = Cons(1, Cons(2, Cons(3, Cons(4, Cons(5, Nil)))))
    val doubleList = Cons(1.0, Cons(2.0, Cons(3.0, Cons(4.0, Cons(5.0, Nil)))))

    println(intList.getHead())
    println(intList.getTail())

    println(doubleList.getHead())
    println(doubleList.getTail())

    println(intList.drop(2))
    println(intList)

    println(intList.dropWhile { it > 2 })


}