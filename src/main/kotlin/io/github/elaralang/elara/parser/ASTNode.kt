package io.github.elaralang.elara.parser

import io.github.elaralang.elara.evaluator.operateMultiply
import io.github.elaralang.elara.lexer.TokenType
import kotlin.reflect.KClass


sealed class ASTNode {
    val children = mutableListOf<ASTNode>()

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
class ScopeNode: ASTNode() {
    override fun toString(): String {
        return "ScopeNode(children=$children)"
    }
}
class ParameterNode: ASTNode() {
    override fun toString(): String {
        return "ParameterNode(children=$children)"
    }
}
class TypedParameterNode: ASTNode() {
    override fun toString(): String {
        return "TypedParameterNode(children=$children)"
    }
}
class ArithmeticNode: ASTNode() {
    override fun toString(): String {
        return "ArithmeticNode(children=$children)"
    }
}
open class ArithTermNode(val negative: Boolean = false): ASTNode() {
    override fun toString(): String {
        return "ArithmeticTerm(${if (negative) "negative" else "positive"}, operations=$children)"
    }
    fun multiply(expr: ASTNode) {
        addChild(MultiplicationNode(expr))
    }
    fun divide(expr: ASTNode) {
        addChild(DivisionNode(expr))
    }
    fun reminder(expr: ASTNode) {
        addChild(ReminderNode(expr))
    }
}
class LastArithTermNode: ArithTermNode() {
    override fun toString(): String {
        return "Term(refers to last expression,  operations=$children)"
    }
}
data class MultiplicationNode(val value: ASTNode): ASTNode()
data class DivisionNode(val value: ASTNode): ASTNode()
data class ReminderNode(val value: ASTNode): ASTNode()

data class DeclarationNode(val identifier: String, val mutable: Boolean, val value: ASTNode) : ASTNode()
data class AssignmentNode(val identifier: String, val value: ASTNode) : ASTNode()
data class NumberNode(val number: Long) : ASTNode()
data class StringNode(val data: String) : ASTNode()
data class FunctionNode(val parameters: TypedParameterNode, val definition: ASTNode): ASTNode()
data class IdentifierNode(val identifier: String) : ASTNode()
data class FunctionCallNode(val identifier: String, val parameters: ParameterNode): ASTNode()
data class TypedIdentifierNode(val identifier: String, val value: ASTNode? = null,val type: String? = null): ASTNode()
data class StructNode(val identifier: String, val typedParams: TypedParameterNode): ASTNode()
data class ConditionalNode(val expr: ASTNode, val mainBranch: ASTNode, val elseBranch: ASTNode?): ASTNode()
data class NamedParamNode(val identifier: String, val expr: ASTNode): ASTNode()
data class BooleanExprNode(val booleanExprType: TokenType): ASTNode()
data class ContextNode(val contextIdentifier: String): ASTNode() {
    override fun toString(): String {
        return "ContextNode(context=${contextIdentifier},children=$children)"
    }
}
data class ExtensionNode(val contextIdentifier: String): ASTNode() {
    override fun toString(): String {
        return "ExtensionNode(context=${contextIdentifier},children=$children)"
    }
}
//---- test rewrite
class ExpressionNode: ASTNode(){
    override fun toString(): String {
        return "ExpressionNode(children=$children)"
    }
}

