package io.github.elaralang.elara.parser

sealed class ASTNode() {
    private val children = mutableListOf<ASTNode>()
    fun addChild(node: ASTNode) {
        children.add(node)
    }
    fun removeChild(node: ASTNode) {
        children.remove(node)
    }
}

class RootNode: ASTNode()

data class AssignmentNode(val identifier: String,val value: ASTNode): ASTNode()

