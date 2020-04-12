package chap6

sealed class Tree<out T>
object EmptyTree : Tree<Nothing>()
data class Node<out T>(val data: T, val left: Tree<T> = EmptyTree, val right: Tree<T> = EmptyTree) : Tree<T>()


fun Tree<Int>.insert(elem: Int): Tree<Int> =
        when (this) {
            EmptyTree -> Node(elem)
            is Node -> when {
                elem <= data -> Node(data, left.insert(elem), right)
                else -> Node(data, left, right.insert(elem))
            }
        }

fun Tree<Int>.contains(elem: Int): Boolean =
        when (this) {
            EmptyTree -> false
            is Node ->
                if (elem == data) true
                else {
                    if (elem > data) right.contains(elem)
                    else left.contains(elem)
                }
        }
