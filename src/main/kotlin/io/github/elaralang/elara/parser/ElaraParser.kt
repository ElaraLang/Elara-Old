package io.github.elaralang.elara.parser

import io.github.elaralang.elara.exceptions.invalidSyntax
import io.github.elaralang.elara.lexer.Token
import io.github.elaralang.elara.lexer.TokenType
import java.util.*

class ElaraParser(tokenList: List<Token>) {

    private val tokens = Stack<Token>()

    init {
        tokens.addAll(tokenList)
        tokens.reverse()
    }

    fun parse(): RootNode {
        val rootNode = RootNode()
        while (tokens.isNotEmpty()) {
            val child = parseExpression()
            child?.let { rootNode.addChild(it) }

        }
        return rootNode
    }

    private fun parseToken(token: Token?): ASTNode? {
        if (token == null) return null
        return when (token.type) {
            TokenType.IDENTIFIER -> IdentifierNode(token.text)
            TokenType.NUMBER -> NumberNode(token.text.toLong())
            TokenType.STRING -> StringNode(token.text)
            else -> invalidSyntax("Invalid expression at ${token.text}")
        }
    }

    private fun parseExpression(
        lastToken: Token? = null,
        endTokens: Set<TokenType>? = null,
        function: Boolean = false
    ): ASTNode? {
        val currentToken = tokens.pop()
        if (currentToken.type == TokenType.EOF) {
            return null
        }
        if (endTokens != null && currentToken.type in endTokens) {
            tokens.add(currentToken)
            return lastToken?.let { parseToken(it) ?: invalidSyntax("Invalid Expression!") }
        }

        if (lastToken == null) {
            //No prior context

            return when (currentToken.type) {
                // let test = <expression>
                TokenType.LET -> {
                    parseDeclaration()
                }
                // ambiguous => requires next Token for context
                TokenType.IDENTIFIER -> {
                    if (function) {
                        return IdentifierNode(currentToken.text)
                    }
                    val next = parseExpression(currentToken, endTokens)
                    if (next != null) {
                        return next
                    }
                    return IdentifierNode(currentToken.text)
                }
                TokenType.NUMBER -> {
                    NumberNode(currentToken.text.toLong())
                }
                TokenType.LBRACE -> {
                    parseScope()
                }
                TokenType.COLON -> {
                    parseFunction()
                }
                TokenType.STRUCT -> {
                    parseStruct()
                }
                TokenType.NEWLINE -> {
                    parseExpression(endTokens = endTokens)
                }
                else -> invalidSyntax("Unexpected token $currentToken")
            }

        } else {
            // Check last context
            return when (lastToken.type) {
                TokenType.IDENTIFIER -> {
                    when (currentToken.type) {
                        TokenType.LPAREN -> {
                            parseFunctionCall(lastToken, TokenType.COMMA, TokenType.RPAREN)
                        }
                        TokenType.DEF -> {
                            parseAssignment(lastToken)
                        }
                        TokenType.NEWLINE -> {
                            parseToken(lastToken)
                        }
                        else -> {
                            if (function) {
                                invalidSyntax("Unexpected token $currentToken")
                            }
                            tokens.push(currentToken)
                            parseFunctionCall(lastToken, null, TokenType.NEWLINE)
                        }
                    }
                }
                else -> invalidSyntax("Unexpected token $lastToken")
            }
        }
    }

    private fun parseScope(): ASTNode {
        val scope = ScopeNode()
        while (tokens.peek().type != TokenType.RBRACE) {
            val expression = parseExpression(endTokens = setOf(TokenType.RBRACE))
            if (expression != null)
                scope.addChild(expression)
        }
        tokens.pop()
        return scope
    }

    private fun parseParams(separator: TokenType?, endType: TokenType): ParameterNode {
        val paramNode = ParameterNode()
        val paramClosers = mutableSetOf(endType)
        if (separator != null) paramClosers.add(separator)
        val endTypes = setOf(TokenType.EOF, endType)
        while (tokens.isNotEmpty() && tokens.peek().type !in endTypes) {
            val token = tokens.peek()
            val param =
                parseExpression(null, paramClosers, true) ?: invalidSyntax("Unexpected token in function call $token")

            paramNode.addChild(param)
            if (separator != null) {
                if (tokens.peek().type !in setOf(separator, endType)) invalidSyntax("Invalid separator in function!")
                if (tokens.peek().type == separator) tokens.pop()
            }
        }
        tokens.pop()
        return paramNode
    }

    private fun parseFunctionCall(identifier: Token, separator: TokenType?, endType: TokenType): FunctionCallNode {
        val params = parseParams(separator, endType)
        return FunctionCallNode(identifier.text, params)
    }

    private fun parseFunction(): FunctionNode {
        val lToken = tokens.pop()
        if (lToken.type != TokenType.LPAREN) invalidSyntax("Parameter not specified for function definition")
        val params = parseTypedParams(TokenType.RPAREN, TokenType.COMMA)
        val arrow = tokens.pop()
        if (arrow.type != TokenType.ARROW) invalidSyntax("Function execution not defined! Expected ARROW got ${arrow.type}")
        val expression = parseExpression() ?: invalidSyntax("Function not defined properly!")
        return FunctionNode(params, expression)
    }

    private fun parseStruct(): StructNode {
        if (tokens.size < 3) invalidSyntax("Incomplete struct definition")
        val id = tokens.pop();
        if (id.type != TokenType.IDENTIFIER)
            invalidSyntax("Identifier not specified for struct declaration")
        if (tokens.pop().type != TokenType.LBRACE) invalidSyntax("Struct definition not specified")

        val typedParams = parseTypedParams(TokenType.RBRACE)
        return StructNode(id.text, typedParams)
    }

    private fun parseTypedParams(closingType: TokenType, separator: TokenType? = null): TypedParameterNode {
        val typedParams = TypedParameterNode()
        cleanNewLines()
        while (tokens.peek().type != closingType) {
            val typedIdentifier = parseTypedIdentifier(closingType)
            cleanNewLines()
            if (separator != null && tokens.peek().type !in setOf(separator, closingType)) invalidSyntax("Invalid separator in Typed parameter!")
            if (tokens.peek().type == separator)tokens.pop()
            typedParams.addChild(typedIdentifier)
        }
        tokens.pop()
        return typedParams
    }


    private fun parseTypedIdentifier(endType: TokenType): TypedIdentifierNode {
        if (tokens.size < 2) invalidSyntax("Incomplete Typed Identifier")
        val left = tokens.pop()
        val right = tokens.pop()
        if (left.type != TokenType.IDENTIFIER) invalidSyntax("Invalid syntax at Typed Identifier expression")

        return when (right.type) {
            TokenType.IDENTIFIER -> {
                if (tokens.peek().type == TokenType.DEF) {
                    tokens.pop()
                    val expr = parseExpression(endTokens = setOf(TokenType.NEWLINE, endType)) ?: invalidSyntax("Invalid default value for parameter -> ${right.text}")

                    TypedIdentifierNode(right.text, expr, left.text)
                } else {
                    TypedIdentifierNode(right.text, null, left.text)
                }
            }
            TokenType.DEF -> {
                val expr = parseExpression(endTokens = setOf(TokenType.NEWLINE, endType)) ?: invalidSyntax("Invalid default value for parameter -> ${right.text}")
                return TypedIdentifierNode(left.text, expr)
            }
            else -> invalidSyntax("Invalid syntax at Typed Identifier expression")
        }
    }

    private fun parseDeclaration(): DeclarationNode {
        var id = tokens.pop()

        val mutable: Boolean
        if (id.type == TokenType.MUT) {
            mutable = true
            id = tokens.pop()
        } else {
            mutable = false
        }

        if (id.type != TokenType.IDENTIFIER) {
            invalidSyntax("Identifier expected on declaration, found ${id.text} of type ${id.type}")
        }

        val eql = tokens.pop()

        if (eql.type != TokenType.DEF) {
            invalidSyntax("'=' expected on declaration, found ${id.text} of type ${id.type}")
        }

        val value: ASTNode = parseExpression() ?: invalidSyntax("Could not find expression to assign to ${id.text}")
        return DeclarationNode(id.text, mutable, value)
    }

    private fun parseAssignment(lastToken: Token): AssignmentNode {
        val value = parseExpression() ?: invalidSyntax("Value expected for assignment")
        return AssignmentNode(lastToken.text, value)
    }

    private fun cleanNewLines() {
        while ( !tokens.empty() && tokens.peek().type == TokenType.NEWLINE) tokens.pop()
    }
}

