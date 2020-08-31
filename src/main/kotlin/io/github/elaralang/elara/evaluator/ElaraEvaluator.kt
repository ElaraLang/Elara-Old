package io.github.elaralang.elara.evaluator

import io.github.elaralang.elara.exceptions.elaraRuntimeException
import io.github.elaralang.elara.exceptions.invalidType
import io.github.elaralang.elara.exceptions.parserException
import io.github.elaralang.elara.parser.*

class ElaraEvaluator: ExpressionVisitor<Any>, StatementVisitor<Unit> {

    private var env = Environment()

    fun evaluate(statements: List<Statement>): Any {
        var last: Any = Unit
        for (statement in statements) {
            last = execute(statement)
        }
        return last
    }

    fun evaluate(expr: Expression): Any {
        return expr.accept(this)
    }
    private fun execute(statement: Statement): Any {
        return statement.accept(this)
    }

    override fun visitLiteral(literal: Literal<*>): Any {
        return literal.value
    }

    override fun visitGroupExpr(grpExpr: GroupExpression): Any {
        return evaluateExpr(grpExpr)
    }

    override fun visitUnaryExpr(unaryExpr: UnaryExpression): Any {
        val expr = evaluateExpr(unaryExpr.rhs)
        return when(unaryExpr.op.op) {
            Operator.NOT -> !coerceToBoolean(expr)
            Operator.SUBTRACT -> -(expr as Long)
            Operator.ADD -> (expr as Long)
            else -> throw parserException("Invalid Unary Operator parsed... ${unaryExpr.op}")
        }
    }

    override fun visitLogicalExpr(assignment: LogicalExpression): Any {
        val lhs = coerceToBoolean(evaluateExpr(assignment.lhs))
        val rhs = coerceToBoolean(evaluateExpr(assignment.rhs))
        return when(assignment.op.op) {
            Operator.AND -> lhs && rhs
            Operator.OR -> lhs || rhs
            Operator.XOR -> lhs xor rhs
            else -> throw parserException("Invalid Logical Operator parsed... ${assignment.op}")
        }
    }

    override fun visitBinaryExpr(binaryExpr: BinaryExpression): Any {
        val lhs = evaluateExpr(binaryExpr.lhs)
        val rhs = evaluateExpr(binaryExpr.rhs)
        return when (binaryExpr.op.op) {
            Operator.EQUALS -> lhs == rhs
            Operator.NOTEQUALS -> lhs != rhs
            Operator.GREATER -> compareGreater(lhs, rhs)
            Operator.GREATEREQUAL -> compareGreaterOrEqual(lhs, rhs)
            Operator.LESSER -> compareLesser(lhs, rhs)
            Operator.LESSEREQUAL -> compareLesserOrEqual(lhs, rhs)
            Operator.ADD -> operateAdd(lhs, rhs)
            Operator.SUBTRACT -> operateSubtract(lhs, rhs)
            Operator.MULTIPLY -> operateMultiply(lhs, rhs)
            Operator.DIVIDE -> operateDivide(lhs, rhs)
            Operator.REMAINDER -> operateRemainder(lhs, rhs)
            else -> parserException("Parser invalidly parsed unknown operator as Binary Operator")
        }
    }
    override fun visitExpressionStatement(exprStmt: ExpressionStatement): Any {
        return evaluateExpr(exprStmt.expression)
    }

    override fun visitVariableDeclarationStatement(exprStmt: VariableDeclarationStatement) {
        env.defineVariable(exprStmt.identifier, "TYPE", evaluateExpr(exprStmt.value))
    }

    override fun visitIfElseStatement(ifElseStmt: IfElseStatement) {
        val condition = coerceToBoolean(evaluateExpr(ifElseStmt.condition))
        if (condition) {
            execute(ifElseStmt.mainBranch)
        } else ifElseStmt.elseBranch?.let { execute(it) }
    }

    override fun visitBlockStatement(exprStmt: BlockStatement) {
        executeBlock(exprStmt.statements, Environment(env))
    }

    override fun visitWhileStatement(whileStmt: WhileStatement) {
        val condition = whileStmt.condition
        val body = whileStmt.body
        while (coerceToBoolean(evaluateExpr(condition))) execute(body)
    }
    override fun visitVariable(variableExpr: Variable): Any {
        return env[variableExpr.identifier] ?: elaraRuntimeException("Undeclared variable accessed")
    }

    override fun visitAssignment(assignment: Assignment): Any {
        return env.assign(assignment.identifier, evaluateExpr(assignment.value))
    }

    private fun evaluateExpr(expr: Expression): Any {
        return expr.accept(this)
    }

    private fun coerceToBoolean(expr: Any): Boolean {
        when (expr) {
            is Long -> return expr > 0
            is Double -> return expr > 0
            is Boolean -> return expr
        }
        invalidType("Boolean", expr.javaClass.simpleName, "Unary NOT: !$expr")
    }

    private fun executeBlock(statements: List<Statement>, environment: Environment) {
        val previous= env
        try {
            env = environment
            for (statement in statements) {
                execute(statement)
            }
        } finally {
            env = previous
        }
    }

    override fun visitFunctionDefinition(funcDef: FunctionDefinition): Any {
//        val returns = funcDef.returnType
//        val args = funcDef.arguments
//        val body = funcDef.body
        TODO("Define function type")
    }

    override fun visitInvocation(invoke: FunctionInvocation): Any {
//        val function = evaluate(invoke.invoker)
//        val args = invoke.parameters
        TODO("Define function type")
    }


}
