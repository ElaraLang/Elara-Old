package io.github.elaralang.elara.parser

import io.github.elaralang.elara.evaluator.ElaraEvaluator

sealed class Statement {
    abstract fun accept(elaraEvaluator: ElaraEvaluator): Any
}

class ExpressionStatement(val expression: Expression): Statement() {
    override fun accept(elaraEvaluator: ElaraEvaluator): Any {
        return elaraEvaluator.visitExpressionStatement(this)
    }

    override fun toString(): String {
        return "(ExpressionStatement [$expression])"
    }
}
class StructDefinitionStatement(val identifier: String, val member: List<StructMember>): Statement() {
    override fun accept(elaraEvaluator: ElaraEvaluator): Any {
        return elaraEvaluator.visitStructDefinition(this)
    }
    override fun toString(): String {
        return "(StructDefinition name=$identifier [$member])"
    }
}

class VariableDeclarationStatement(val mutable: Boolean, val identifier: String, val value: Expression, val type: String? = null): Statement() {
    override fun accept(elaraEvaluator: ElaraEvaluator): Any {
        return elaraEvaluator.visitVariableDeclarationStatement(this)
    }
    override fun toString(): String {
        return "(VariableDeclaration mutable=$mutable name=$identifier [$value])"
    }
}
class BlockStatement(val statements: List<Statement>): Statement() {
    override fun accept(elaraEvaluator: ElaraEvaluator): Any {
        return elaraEvaluator.visitBlockStatement(this)
    }
    override fun toString(): String {
        return "(BlockStatement { $statements })"
    }
}
class IfElseStatement(val condition: Expression, val mainBranch: Statement, val elseBranch: Statement?): Statement() {
    override fun accept(elaraEvaluator: ElaraEvaluator): Any {
        return elaraEvaluator.visitIfElseStatement(this)
    }
    override fun toString(): String {
        return "(If ($condition) then || $mainBranch || else || $elseBranch ||)"
    }
}

class WhileStatement(val condition: Expression, val body: Statement): Statement() {
    override fun accept(elaraEvaluator: ElaraEvaluator): Any {
        return elaraEvaluator.visitWhileStatement(this)
    }
    override fun toString(): String {
        return "(while ($condition) => $body)"
    }
}
class ExtendStatement(val id: String, val body: Statement): Statement() {
    override fun accept(elaraEvaluator: ElaraEvaluator): Any {
        return elaraEvaluator.visitExtendStatement(this)
    }
    override fun toString(): String {
        return "(Extend ($id) => $body)"
    }
}

interface StatementVisitor<T> {
    fun visitExpressionStatement(exprStmt: ExpressionStatement): Any
    fun visitVariableDeclarationStatement(exprStmt: VariableDeclarationStatement)
    fun visitBlockStatement(exprStmt: BlockStatement)
    fun visitIfElseStatement(ifElseStmt: IfElseStatement)
    fun visitWhileStatement(whileStmt: WhileStatement)
    fun visitStructDefinition(structDef: StructDefinitionStatement)
    fun visitExtendStatement(extendStmt: ExtendStatement)
}


