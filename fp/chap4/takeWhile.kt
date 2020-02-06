package chap4

// 4.7 리스트의 값을 조건 함수에 적용했을 때, 결과값이 참인 값의 리스트를 반환하라.

tailrec fun <P1> takeWhile(func : (P1) -> Boolean, list : List<P1>, acc : List<P1>) : List<P1> = when {
    list.isEmpty() -> acc
    else -> {
        val res = if (func(list[0])) list.subList(0, 1) else emptyList()
        takeWhile(func, list.subList(1, list.size), acc + res)
    }
}

fun main() {
    val func = { p1 : Int -> p1 >= 3}
    println(takeWhile(func, listOf(1,2,3,4,5,6,7,8), emptyList()))
}