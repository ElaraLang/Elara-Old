package io.github.elaralang.elara.parser

import io.github.elaralang.elara.exceptions.invalidSyntax
import io.github.elaralang.elara.lexer.ElaraLexer
import io.github.elaralang.elara.lexer.Token
import io.github.elaralang.elara.lexer.TokenType
import java.util.*
import kotlin.coroutines.Continuation

class ElaraParser(tokenList: List<Token>) {
    companion object {
        private val continuation = Token(TokenType.IDENTIFIER, "#CONTINUATION")
    }
    private val tokens = Stack<Token>()

    init {
        tokens.addAll(tokenList)
        tokens.reverse()
    }

    fun parse(): RootNode {
        val rootNode = RootNode()
        while (tokens.isNotEmpty() && tokens.peek().type != TokenType.EOF) {
            cleanTopOfStack()
            val child = parseExpressionTill(TokenType.NEWLINE)
            rootNode.addChild(child)
        }
        return rootNode
    }

    // General expression parse function
    private fun parseExpressionTill(vararg closingType: TokenType): ASTNode {
        val exprNode = ExpressionNode()
        while (!tokens.empty() && tokens.peek().type !in closingType) {
            cleanTopOfStack()
            val child =
                    if (exprNode.children.size == 0)parseToken(*closingType) ?: return exprNode
                    else parseIdentifierWithLookahead(continuation,*closingType) ?: return exprNode

            exprNode.addChild(child)
        }
        return exprNode
    }

    // Non-specific Token parsers
    private fun parseToken(vararg closingType: TokenType): ASTNode? {
       return when (tokens.peek().type) {
           in closingType, TokenType.EOF -> null
           TokenType.NUMBER -> NumberNode(tokens.pop().text.toLong())
           TokenType.STRING -> StringNode(tokens.pop().text)

           TokenType.IDENTIFIER -> parseIdentifierWithLookahead(tokens.pop(), *closingType)

           TokenType.LPAREN -> parseParenthesisExpression()
           TokenType.LBRACE -> parseScope()
           TokenType.LET -> parseDeclaration(*closingType)
           TokenType.COLON -> parseFunctionDefinition()
           TokenType.STRUCT -> parseStruct()
           TokenType.IF -> parseIfExpr(*closingType)
           TokenType.EXTEND -> parseExtendExpr()
           else -> invalidSyntax("Unexpected token : ${tokens.peek().text} of type ${tokens.peek().type}")
       }
    }

    private fun parseIdentifierWithLookahead(lastToken: Token, vararg closingType: TokenType): ASTNode? {
        return when (tokens.peek().type) {
            in closingType, TokenType.EOF -> {
                if (lastToken == continuation) null
                else IdentifierNode(lastToken.text)
            }
            TokenType.DOT -> {
                tokens.pop()
                parseContextual(lastToken.text, *closingType)
            }
            TokenType.IDENTIFIER -> parseInfixFunctionCall(lastToken.text, tokens.pop().text,)
            TokenType.LPAREN -> parseFunctionCall(lastToken.text)
            TokenType.DEF -> parseAssignment(lastToken.text, *closingType)
            TokenType.OPERATOR -> if (lastToken != continuation) IdentifierNode(lastToken.text) else parseOperation(false, *closingType)
            TokenType.AND,
            TokenType.OR,
            TokenType.XOR,
            TokenType.EQUALS -> if (lastToken != continuation) IdentifierNode(lastToken.text) else parseBooleanExpr(closingType)
            else -> invalidSyntax("Unexpected token : ${tokens.peek().text}")
        }
    }

    private fun parseBooleanExpr(closingType: Array<out TokenType>): BooleanExprNode {
        val op = tokens.pop()
        val rhs = parseExpressionTill(*closingType)
        return BooleanExprNode(op.type).apply {
            addChild(rhs)
        }
    }

    private fun parseOperation(start: Boolean = false, vararg closingType: TokenType): ArithmeticNode {
        val arithmetic = ArithmeticNode()
        var lastExpr: ArithTermNode = if (start) parseArithmeticExpr(false, *closingType) else LastArithTermNode()
        while (tokens.isNotEmpty() && tokens.peek().type == TokenType.OPERATOR && tokens.peek().type !in closingType) {
            val op = TokenType.OPERATOR.expect()
            when (op.text) {
                "+" -> {
                    arithmetic.addChild(lastExpr)
                    lastExpr = parseArithmeticExpr(false, TokenType.OPERATOR, *closingType)
                }
                "-" -> {
                    arithmetic.addChild(lastExpr)
                    lastExpr = parseArithmeticExpr(true, TokenType.OPERATOR, *closingType)
                }
                "*" -> {
                    lastExpr.multiply(parseTillOperation(TokenType.OPERATOR, *closingType))
                }
                "/" -> {
                    lastExpr.divide(parseTillOperation(TokenType.OPERATOR, *closingType))
                }
                "%" -> {
                    lastExpr.reminder(parseTillOperation(TokenType.OPERATOR, *closingType))
                }
                else -> invalidSyntax("UNDECLARED OPERATOR TYPE FOUND")
            }
        }
        arithmetic.addChild(lastExpr)
        return arithmetic
    }
    private fun parseArithmeticExpr(negative: Boolean = false, vararg closingToken: TokenType): ArithTermNode {

        return ArithTermNode(negative).apply {
            addChild(parseTillOperation(*closingToken))
        }
    }
    private fun parseTillOperation(vararg tokenType: TokenType): ASTNode {
        return parseTokenLimited(*tokenType) ?: invalidSyntax("Arithmetic ended unexpectedly")
    }
    private fun parseTokenLimited(vararg closingType: TokenType): ASTNode? {
        return when (tokens.peek().type) {
            in closingType -> null
            TokenType.NUMBER -> NumberNode(tokens.pop().text.toLong())
            TokenType.STRING -> StringNode(tokens.pop().text)
            TokenType.IDENTIFIER -> parseTokenLimitedWithLookAhead(tokens.pop(), *closingType)
            TokenType.LPAREN -> if (closingType.any { it == TokenType.OPERATOR }) parseParenthesisExpression() else invalidSyntax("Unexpected token in context : ${tokens.peek().text} of type ${tokens.peek().type}")
            else -> invalidSyntax("Unexpected token in context : ${tokens.peek().text} of type ${tokens.peek().type}")
        }
    }

    private fun parseTokenLimitedWithLookAhead(lastToken: Token, vararg closingType: TokenType): ASTNode? {
        return when (tokens.peek().type) {
            in closingType -> IdentifierNode(lastToken.text)
            TokenType.DOT -> {
                tokens.pop()
                parseContextualLimited(lastToken.text, *closingType)
            }
            TokenType.LPAREN -> parseFunctionCall(lastToken.text)
            else -> IdentifierNode(lastToken.text)
        }
    }




    // Specific parse functions

    private fun parseParenthesisExpression(): ASTNode {
        TokenType.LPAREN.expect() // remove (
        val exp = parseExpressionTill(TokenType.RPAREN)
        TokenType.RPAREN.expect() // remove )
        return exp
    }

    private fun parseScope(): ScopeNode {
        TokenType.LBRACE.expect() // remove {
        cleanTopOfStack()
        return ScopeNode().apply {
            while (!tokens.empty() && tokens.peek().type != TokenType.RBRACE) {
                addChild(parseExpressionTill(TokenType.NEWLINE, TokenType.RBRACE))
                cleanTopOfStack()
            }
            TokenType.RBRACE.expect() // remove }
        }
    }

    private fun parseDeclaration(vararg closingType: TokenType): DeclarationNode {
        TokenType.LET.expect()
        val mutable = TokenType.MUT.tryPop()
        val identifier = TokenType.IDENTIFIER.expect()
        TokenType.DEF.expect()
        val expr = parseExpressionTill(*closingType)
        return DeclarationNode (
                identifier = identifier.text,
                mutable = mutable,
                value = expr
        )
    }

    private fun parseAssignment(identifier: String, vararg closingType: TokenType): AssignmentNode {
        TokenType.DEF.expect()
        val expr = parseExpressionTill(*closingType)
        return AssignmentNode(identifier, expr)
    }

    private fun parseFunctionDefinition(): FunctionNode {
        TokenType.COLON.expect()
        val typedParams: TypedParameterNode = parseTypedParams(TokenType.LPAREN, TokenType.RPAREN, TokenType.COMMA)
        TokenType.ARROW.expect()
        val scope = parseToken() ?: invalidSyntax("Function scope not defined")
        return FunctionNode(typedParams, scope)
    }

    private fun parseTypedParams(openingToken: TokenType, closingToken: TokenType, vararg separator: TokenType): TypedParameterNode {
        openingToken.expect()
        val paramNode = TypedParameterNode()
        while (tokens.peek().type != closingToken) {
            paramNode.addChild(parseTypedIdentifier(closingToken, *separator))
            separator.any { it.tryPop() }
        }
        cleanTopOfStack()
        closingToken.expect()
        return paramNode
    }

    private fun parseTypedIdentifier(closingToken: TokenType, vararg separator: TokenType): TypedIdentifierNode {
        cleanTopOfStack()
        val first = TokenType.IDENTIFIER.expect()
        val second = tokens.pop()
        return when (second.type) {
            TokenType.IDENTIFIER -> {
                val expr = if (TokenType.DEF.tryPop()) {
                    parseExpressionTill(closingToken, *separator)
                } else null

                TypedIdentifierNode(second.text, expr, first.text)
            }
            TokenType.DEF -> {
                val expr = parseExpressionTill(closingToken, *separator)


                TypedIdentifierNode(first.text, expr)
            }
            else -> invalidSyntax("Unexpected token on typed param parse, ${second.text} of type ${second.type}")
        }
    }

    private fun parseStruct(): StructNode {
        TokenType.STRUCT.expect()
        val id = TokenType.IDENTIFIER.expect()
        val typedParams = parseTypedParams(TokenType.LBRACE, TokenType.RBRACE, TokenType.NEWLINE)
        return StructNode(id.text, typedParams)
    }

    private fun parseContextual(context: String, vararg closingType: TokenType): ContextNode {
        val expr = parseToken(*closingType) ?: invalidSyntax("Invalid token in context : $context")
        if (expr is ScopeNode) invalidSyntax("Cannot create a scope in a context call : $context")
        when (expr) {
            is AssignmentNode, is FunctionCallNode, is IdentifierNode -> Unit
            else -> invalidSyntax("${expr::class.java.simpleName} cannot be used in a context call")
        }
        return ContextNode(context).apply {
            addChild(expr)
        }
    }
    private fun parseContextualLimited(context: String, vararg closingType: TokenType): ContextNode {
        val expr = parseTokenLimited(*closingType) ?: invalidSyntax("Invalid token in limited context : $context")
        if (expr is ScopeNode) invalidSyntax("Cannot create a scope in a limited context call : $context")
        when (expr) {
            is FunctionCallNode, is IdentifierNode -> Unit
            else -> invalidSyntax("${expr::class.java.simpleName} cannot be used in a context call")
        }
        return ContextNode(context).apply {
            addChild(expr)
        }
    }

    private fun parseInfixFunctionCall(context: String, functionName: String): ContextNode {
        val param = parseCallParameters(null, TokenType.NEWLINE)
        val functionCall = FunctionCallNode(functionName, param)
        return ContextNode(context).apply {
            addChild(functionCall)
        }
    }

    private fun parseFunctionCall(functionName: String): FunctionCallNode {
        val param = parseCallParameters(TokenType.LPAREN, TokenType.RPAREN, TokenType.COMMA)
        return FunctionCallNode(functionName, param)
    }

    private fun parseCallParameters(openingToken: TokenType?, closingToken: TokenType, vararg separator: TokenType): ParameterNode {
        val params = ParameterNode()
        openingToken?.expect()

        while (tokens.peek().type != closingToken) {
            val curToken = tokens.pop()
            val expr = if (TokenType.DEF.tryPop(false)) {
                if (curToken.type != TokenType.IDENTIFIER) invalidSyntax("Excepted identifier at $curToken for named parameter")
                val value  = parseTokenLimited(closingToken,TokenType.EOF, *separator) ?: break
                NamedParamNode(curToken.text, value)
            } else {
                tokens.push(curToken)
                parseTokenLimited(closingToken,TokenType.EOF, *separator) ?: break
            }
            params.addChild(expr)
            if (tokens.peek().type != closingToken && tokens.peek().type != TokenType.EOF && separator.isNotEmpty()) separator[0].expect()
        }

        val closed = closingToken.expect()
        if (closed.type == TokenType.NEWLINE) tokens.push(closed)
        return params
    }


    private fun parseIfExpr(vararg closingToken: TokenType): ConditionalNode {
        TokenType.IF.expect()
        val expr = parseExpressionTill(TokenType.ARROW)
        TokenType.ARROW.expect()
        val result = parseExpressionTill(TokenType.ELSE, *closingToken)
        val elseBranch = if (TokenType.ELSE.tryPop()) {
            if (tokens.peek().type != TokenType.IF) TokenType.ARROW.expect()
            parseToken(*closingToken)
        } else null
        return ConditionalNode(expr, result, elseBranch)
    }

    private fun parseExtendExpr(): ExtensionNode {
        TokenType.EXTEND.expect()
        val identifier = TokenType.IDENTIFIER.expect()
        val extensionScope = parseScope()
        return ExtensionNode(identifier.text).apply {
            addChild(extensionScope)
        }
    }


    // Utility functions

    private fun cleanTopOfStack(token: TokenType = TokenType.NEWLINE) {
        while (tokens.peek().type == token) tokens.pop()
    }
    private fun TokenType.expect(): Token {
        if (this != TokenType.NEWLINE) cleanTopOfStack()
        if (tokens.empty()) invalidSyntax("Tokens ended unexpectedly! Expected token of type $this")
        val token = tokens.pop()
        if (token.type != this && token.type != TokenType.EOF) invalidSyntax("Expected token of type $this")
        return token
    }
    private fun TokenType.tryPop(cleanTop: Boolean = true): Boolean {
        if (cleanTop) cleanTopOfStack()
        return if (tokens.isNotEmpty() && tokens.peek().type == this) {
            tokens.pop()
            true
        } else false
    }

}


