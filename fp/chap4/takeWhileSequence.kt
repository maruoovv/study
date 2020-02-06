package chap4

// 4.8 4.7을 무한대를 입력 받을수 있게 수정하라

tailrec fun <P1> takeWhileSequence(func : (P1) -> Boolean, list : Sequence<P1>, acc : List<P1>) : List<P1> = when {
    list.none() || !func(list.first()) -> acc
    else -> {
        takeWhileSequence(func, list.drop(1), acc + list.first())
    }
}

fun main() {
    val func = { p1 : Int -> p1 < 10}
    println(takeWhileSequence(func, generateSequence(1) { it + 1 }, emptyList()))
}