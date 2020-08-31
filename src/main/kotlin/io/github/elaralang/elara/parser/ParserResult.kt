package io.github.elaralang.elara.parser

import io.github.elaralang.elara.exceptions.ParseError
import io.github.elaralang.elara.exceptions.unwindWithError
import io.github.elaralang.elara.lexer.Token
import io.github.elaralang.elara.lexer.TokenType


class ParserResult(val stack: TokenStack) {
    var parseError = false
        private set

    fun parse(): List<Statement> {
        val statements = mutableListOf<Statement>()
        while (!stack.isAt(TokenType.EOF)) {
            parseDeclaration()?.let { statements.add(it) }
        }
        return statements
    }

    private fun parseDeclaration(): Statement? {
        return try {
            val stmt = if (stack isAt TokenType.LET) {
                parseVariableDeclaration()
            } else parseStatement()
            //if (stack.isEmpty() || stack.peek().type == TokenType.EOF) stack.push(Token(TokenType.NEWLINE, "\n"))
            stack expect TokenType.NEWLINE
            stmt
        } catch (error: ParseError) {
            parseError = true
            synchronizeWithErrors()
            null
        }
    }

    private fun parseStatement(): Statement {
        return when (stack.peek().type) {
            TokenType.WHILE -> parseWhileStatement()
            TokenType.IF -> parseIfElseStatement()
            TokenType.LBRACE -> parseStatementBlock()
            TokenType.STRUCT -> parseStructDefinition()
            TokenType.EXTEND -> parseExtendStatement()
            else -> parseExpressionStatement()
        }
    }

    private fun parseExtendStatement(): Statement {
        stack expect TokenType.EXTEND
        val id = (stack expect TokenType.IDENTIFIER).text
        val statement = parseStatement()
        return ExtendStatement(id, statement)
    }

    private fun parseStructDefinition(): Statement {
        fun parseStructMembers(): List<StructMember> {
            val args = mutableListOf<StructMember>()
            if (stack isAt TokenType.RBRACE) return args
            do {
                stack.cleanTop()
                if (stack isAt  TokenType.RBRACE) break;
                val first = (stack expect TokenType.IDENTIFIER)
                val identifier = if (stack isAt TokenType.IDENTIFIER) {
                    (stack expect TokenType.IDENTIFIER).text
                } else null
                val value = if (stack popIf TokenType.DEF) {
                    parseExpression()
                } else null
                if (identifier == null && value == null) throw unwindWithError(first, "Invalid argument")
                if (identifier == null && value != null) {
                    args.add(StructMember(null, first.text, value))
                } else if (identifier != null) {
                    args.add(StructMember(first.text, identifier, value))
                } else throw unwindWithError(first, "Invalid struct member")
            } while (stack popIf TokenType.NEWLINE)
            return args
        }
        stack expect TokenType.STRUCT
        val id = stack expect TokenType.IDENTIFIER
        stack expect TokenType.LBRACE
        val members = parseStructMembers()
        stack expect TokenType.RBRACE
        return StructDefinitionStatement(id.text, members)
    }

    private fun parseWhileStatement(): WhileStatement {
        stack expect TokenType.WHILE
        val condition = parseExpression()
        stack expect TokenType.ARROW
        val body = parseStatement()
        return WhileStatement(condition, body)
    }

    private fun parseIfElseStatement(): IfElseStatement {
        stack expect TokenType.IF
        val condition = parseExpression()
        stack expect TokenType.ARROW
        val mainBranch = parseStatement()
        stack.cleanTop()
        val elseBranch = if (stack popIf TokenType.ELSE){
            if (!(stack isAt TokenType.IF)) stack expect TokenType.ARROW
            stack.cleanTop()
            parseStatement()
        } else null
        if (stack isAt TokenType.EOF) stack.push(Token(TokenType.NEWLINE, ""))
        return IfElseStatement(condition, mainBranch, elseBranch)
    }

    private fun parseStatementBlock(): BlockStatement {
        val statements = mutableListOf<Statement>()
        stack expect TokenType.LBRACE

        while (!(stack isAt TokenType.RBRACE)) {
            stack.cleanTop()
            parseDeclaration()?.let { statements.add(it) }
        }
        stack.cleanTop()
        stack expect TokenType.RBRACE
        return BlockStatement(statements)
    }

    private fun parseExpressionStatement(): Statement {
        val expr= parseExpression()
        //stack expect TokenType.NEWLINE
        return ExpressionStatement(expr)
    }

    private fun parseVariableDeclaration(): Statement {
        stack expect TokenType.LET
        val mutable = stack popIf TokenType.MUT
        val identifier = (stack expect TokenType.IDENTIFIER).text
        stack expect TokenType.DEF
        val expr = parseExpression()
        return VariableDeclarationStatement(mutable, identifier, expr)
    }

    private fun parseExpression(): Expression {
        return parseAssignment()
    }

    private fun parseAssignment(): Expression {
        val lhs = parseOr()
        if (stack isAt TokenType.DEF) {
            val def = stack expect TokenType.DEF
            val rhs = parseOr()
            if (lhs is Variable) {
                return Assignment(lhs.identifier, rhs)
            }else if (lhs is ContextExpression) {
                return ContextualAssignment(lhs.context, lhs.identifier, rhs)
            }
            throw unwindWithError(def, "Assignment Left-Hand-Side invalid!")
        }
        return lhs
    }
    private fun parseOr(): Expression {
        var lhs = parseAnd()
        while (stack isAt TokenType.OR) {
            val operator = Operation(stack.pop())
            val rhs = parseAnd()
            lhs = LogicalExpression(lhs, operator, rhs)
        }
        return lhs
    }

    private fun parseAnd(): Expression {
        var lhs = parseEquality()
        while (stack isAt TokenType.AND) {
            val operator = Operation(stack.pop())
            val rhs = parseEquality()
            lhs = LogicalExpression(lhs, operator, rhs)
        }
        return lhs
    }

    private fun parseEquality(): Expression {
        var lhs = parseComparison()
        while (stack isAt setOf(TokenType.EQUALS, TokenType.NOTEQUALS)) {
            val operator = Operation(stack.pop())
            val rhs: Expression = parseComparison()
            lhs = BinaryExpression(lhs, operator, rhs)
        }
        return lhs
    }

    private fun parseComparison(): Expression {
        var lhs = parseAddition()

        while (stack isAt setOf(TokenType.GREATER, TokenType.GREATEREQUAL, TokenType.LESSER, TokenType.LESSEREQUAL)) {
            val operator = Operation(stack.pop())
            val rhs = parseAddition()
            lhs = BinaryExpression(lhs, operator, rhs)
        }

        return lhs
    }

    private fun parseAddition(): Expression {
        var lhs = parseMultiplication()

        while (stack isAt setOf(TokenType.ADD, TokenType.SUBTRACT)) {
            val operator= Operation(stack.pop())
            val rhs =  parseMultiplication()
            lhs = BinaryExpression(lhs, operator, rhs)
        }

        return lhs
    }

    private fun parseMultiplication(): Expression {
        var lhs = parseUnary()

        while (stack isAt setOf(TokenType.MULTIPLY, TokenType.DIVIDE, TokenType.REMAINDER)) {
            val operator = Operation(stack.pop())
            val rhs= parseUnary()
            lhs = BinaryExpression(lhs, operator, rhs)
        }

        return lhs
    }

    private fun parseUnary(): Expression {
        if (stack isAt setOf(TokenType.NOT, TokenType.SUBTRACT, TokenType.ADD)) {
            val operator = Operation(stack.pop())
            val right= parseUnary()
            return UnaryExpression(operator, right)
        }
        return parseInvoke()
    }

    private fun parseInvoke(): Expression {
        fun parseInvocationWithParameters(invoker: Expression): Expression {
            val arguments= mutableListOf<Expression>()
            if (stack isNotAt TokenType.RPAREN) do {
                arguments.add(parseExpression())
            } while (stack popIf TokenType.COMMA)
            stack expect TokenType.RPAREN
            return FunctionInvocation(invoker, arguments)
        }

        var expr = parseFunctionDefinition()
        while (true) expr = when (stack.peek().type) {
            TokenType.LPAREN -> {
                stack.pop()
                parseInvocationWithParameters(expr)
            }
            TokenType.DOT -> {
                stack.pop()
                ContextExpression(expr, stack.pop().text)
            }
            else -> {
                break
            }
        }
        while (stack popIf TokenType.LPAREN) {
            expr = parseInvocationWithParameters(expr)
        }
        return expr
    }

    private fun parseFunctionDefinition(): Expression {
        fun parseFunctionArguments(): List<FunctionArgument> {
            val args = mutableListOf<FunctionArgument>()
            if (stack isAt TokenType.RPAREN) return args
            do {
                val first = (stack expect TokenType.IDENTIFIER)
                val identifier = if (stack isAt TokenType.IDENTIFIER) {
                    (stack expect TokenType.IDENTIFIER).text
                } else null
                val value = if (stack popIf TokenType.DEF) {
                    parseExpression()
                } else null
                if (identifier == null && value == null) throw unwindWithError(first, "Invalid argument")
                if (identifier == null && value != null) {
                    args.add(FunctionArgument(null, first.text, value))
                } else if (identifier != null) {
                    args.add(FunctionArgument(first.text, identifier, value))
                } else throw unwindWithError(first, "Invalid argument")
            } while (stack popIf TokenType.COMMA)
            return args
        }

        val type = if (stack isAt TokenType.COLON) Variable("ElaraUnit") else parsePrimary()
        if (stack popIf TokenType.COLON) {
            stack expect TokenType.LPAREN
            val args = parseFunctionArguments()
            stack expect TokenType.RPAREN
            stack expect TokenType.ARROW
            val body = parseStatement()
            return FunctionDefinition((type as Variable).identifier, args, body)
        }
        return type
    }

    private fun parsePrimary(): Expression {
        if (stack isAt TokenType.BOOLEAN) {
            return Literal(stack.pop().text == "true")
        }
        return when (stack.peek().type) {
            TokenType.BOOLEAN -> Literal(stack.pop().text == "true")
            TokenType.NUMBER -> {
                val value = stack.pop().text
                return if (value.contains("(\\.)".toRegex()) || value.endsWith("D")) Literal(value.toDouble())
                else Literal(value.toLong())
            }
            TokenType.STRING -> {
                val str = stack.pop().text;
                Literal(str.substring(1, str.length - 1))
            }
            TokenType.IDENTIFIER -> {
                val v = stack.pop().text
                if (stack.peek().type == TokenType.IDENTIFIER) {
                    // Infix function
                    val functionName = stack.pop().text;
                    val args = mutableListOf<Expression>()
                    while (stack isNotAt TokenType.NEWLINE) {
                        args.add(parseExpression())
                    }
                    FunctionInvocation(ContextExpression(Variable(v),functionName), args)
                } else Variable(v)
            }
            TokenType.LPAREN -> {
                stack expect TokenType.LPAREN
                val expr = parseExpression()
                stack expect TokenType.RPAREN
                GroupExpression(expr)
            }
            else -> throw unwindWithError(stack.pop(), "Expected a Expression.")
        }
    }

    //TODO: Error handle on statements
    private fun synchronizeWithErrors() {
        while (stack.pop().type != TokenType.NEWLINE || stack.pop().type != TokenType.EOF) continue
        while (stack.popIf(TokenType.NEWLINE)) continue
        if (stack.peek().type == TokenType.EOF) stack.push(Token(TokenType.NEWLINE, "\n"))
    }

}

data class FunctionArgument(val type: String?, val identifier: String, val default: Any?)
data class StructMember(val type: String?, val identifier: String, val default: Any?)



