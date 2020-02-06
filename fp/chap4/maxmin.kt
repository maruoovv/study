package chap4

fun max(a : Int)= { b : Int -> kotlin.math.max(a, b) }

fun min(a : Int) = { b : Int -> kotlin.math.min(a ,b) }

fun main(args : Array<String>) {
    println(max(3) (5))
    println(min(3) (5))
}
