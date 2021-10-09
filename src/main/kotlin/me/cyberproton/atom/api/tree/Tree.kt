package me.cyberproton.atom.api.tree

class Tree<T>(val value: T) {
    val root: Node<T> = Node(value)

    class Node<T>(val value: T, val parent: Node<T>? = null) {
        private val children: MutableList<Node<T>> = arrayListOf()

        fun addChild(child: T) {
            children.add(Node(child, this))
        }

        fun removeChild(child: T) {
            children.removeIf { it.value == child }
        }
    }
}