package io.github.elaralang.elara.parser

sealed class ASTNode {
    private val children = mutableListOf<ASTNode>()
    fun addChild(node: ASTNode) {
        children.add(node)
    }

    fun removeChild(node: ASTNode) {
        children.remove(node)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ASTNode) return false

        if (children != other.children) return false

        return true
    }

    override fun hashCode(): Int {
        return children.hashCode()
    }

}

class RootNode : ASTNode()

data class AssignmentNode(val identifier: String, val value: ASTNode) : ASTNode()
data class NumberNode(val number: Long) : ASTNode()

