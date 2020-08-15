package io.github.elaralang.elara.parser

import java.lang.instrument.ClassDefinition

sealed class ASTNode {
    protected val children = mutableListOf<ASTNode>()

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

class RootNode : ASTNode() {
    override fun toString(): String {
        return "RootNode(children=$children)"
    }
}

data class DeclarationNode(val identifier: String, val mutable: Boolean, val value: ASTNode) : ASTNode()
data class AssignmentNode(val identifier: String, val value: ASTNode) : ASTNode()
data class NumberNode(val number: Long) : ASTNode()
data class StringNode(val data: String) : ASTNode()
data class FunctionNode(val parameters: ParameterNode, val definition: ASTNode): ASTNode()
data class IdentifierNode(val identifier: String) : ASTNode()
class ParameterNode: ASTNode() {
    override fun toString(): String {
        return "ParameterNode(children=$children)"
    }
}
data class FunctionCallNode(val identifier: String, val parameters: ParameterNode): ASTNode()
class ScopeNode: ASTNode() {
    override fun toString(): String {
        return "ScopeNode(children=$children)"
    }
}
