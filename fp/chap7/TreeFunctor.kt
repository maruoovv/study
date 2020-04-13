package chap7

interface TreeFunctor<out A> {
    fun <B> fmap(f : (A) -> B) : TreeFunctor<B>
}

sealed class Tree<out T> : TreeFunctor<T> {
    abstract override fun toString(): String
    abstract override fun <B> fmap(f : (T) -> B) : Tree<B>
}

object EmptyTree : Tree<Nothing>() {
    override fun toString(): String = "EMPTY"

    override fun <B> fmap(f: (Nothing) -> B): Tree<B> = EmptyTree
}

data class Node<out A>(val value: A, val left: Tree<A> = EmptyTree, val right: Tree<A> = EmptyTree) : Tree<A> () {
    override fun toString(): String = "(N $value $left $right)"
    override fun <B> fmap(f: (A) -> B): Tree<B> = Node(f(value), left.fmap(f), right.fmap(f))
}

fun <T> treeOf(value: T, left: Tree<T> = EmptyTree, right: Tree<T> = EmptyTree) : Tree<T> = Node(value, left, right)

fun main() {
    val tree = treeOf(1, treeOf(2, treeOf(3), treeOf(4)), treeOf(5, treeOf(6), treeOf(7)))

    println(tree)

    val t = tree.fmap { it -> it * 2 }

    println(t)
}
