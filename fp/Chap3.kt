
// 3.2 X의 N승을 구하는 함수를 재귀로 구현
fun power(x : Double, n : Int) : Double {
    return when (n) {
        1 -> x
        else -> x * power(x, n - 1)
    }
}

// 3.3 n의 팩터리얼을 구하는 함수를 재귀로 표현
fun factorial(n : Int) : Int {
    return when(n) {
        0, 1 -> 1
        else -> n * factorial(n - 1)
    }
}

// 3.4 10진수 숫자를 입력받아 2진수 문자열로 변환하는 함수를 작성하라
fun toBinary(n : Int) : String {
    return when(n) {
        0 -> ""
        else -> {
            val rest = n % 2
            val divideN = n / 2
            toBinary(divideN) + rest
        }
    }
}

// 3.5 숫자를 두개 입력받은 후 두 번째 숫자를 첫 번째 숫자만큼 가지고 있는 리스트를 반환하라.
fun replicate(n : Int, element : Int) : List<Int> {
    return when(n) {
        0 -> listOf()
        else -> listOf(element) + replicate(n - 1, element)
    }
}

// 3.6 입력값 n 이 리스트에 존재하는지 확인하는 함수를 작성하라.
fun elem(num : Int, list: List<Int>) : Boolean {
    return when {
        list.isEmpty() -> false
        else -> {
            if (list.first() == num) true
            else elem(num, list.drop(1))
        }
    }
}

// 3.9 최대공약수를 구하는 gcd 함수를 작성하라.

fun gcd(m : Int, n : Int) : Int {
    return when (n) {
        0 -> m
        else -> gcd(n, m%n)
    }
}

// 3.12 factorial + tailrec + memoization

fun factorialFP(n : Int) : Int {
    return factorialFP(n, 1)
}

tailrec fun factorialFP(n : Int, sum : Int) : Int {
    return when(n) {
        0, 1 -> sum
        else -> factorialFP(n - 1, n * sum)
    }
}

// 3.13 power + tailrec + memoization

fun powerFP(x : Double, n : Int) : Double {
    return powerFP(x, n, 1.0)
}

tailrec fun powerFP(x : Double, n : Int, sum : Double) : Double {
    return when (n) {
        0 -> sum
        else -> powerFP(x, n - 1, x * sum)
    }
}
