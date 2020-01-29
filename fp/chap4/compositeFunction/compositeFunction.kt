package chap4.compositeFunction

import java.lang.IllegalArgumentException

// 4.15 숫자의 리스트를 받아서 최댓값의 제곱을 구하는 함수를 합성함수로 작성하라

fun maxPower(list : List<Int>) : Int = power(max(list))

fun max(list : List<Int>) : Int {
    if (list.isEmpty()) throw IllegalArgumentException("empty list")

    return list.max()!!
}

fun power(num : Int) : Int = num*num

infix fun <F, G, R> ((F) -> R).compose(g: (G) -> F): (G) -> R {
    return {gInput : G -> this(g(gInput))}
}

fun main(args : Array<String>) {
    println(maxPower(listOf(5,12,542,56)))

    val max2 = { i : List<Int> -> i.max()!!}
    val power2 = { i : Int -> i * i }

    val maxPower2 = power2 compose max2

    println(maxPower2(listOf(5, 12, 542, 56)))
}
