package io.github.elaralang.elara.parser

import io.github.elaralang.elara.exceptions.invalidSyntax
import io.github.elaralang.elara.lexer.ElaraLexer
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

    private fun parseExpression(lastToken: Token? = null,endTokens: Set<TokenType>? = null): ASTNode? {
        val currentToken = tokens.pop()
        if (endTokens != null && currentToken.type in endTokens) {
            tokens.add(currentToken)
            if (lastToken != null)
                return parseToken(lastToken) ?: invalidSyntax("Invalid Expression!")
            else
                invalidSyntax("Expression not found!")
        }
        if (lastToken == null) {
            //No prior context

            when (currentToken.type) {
                // let test = <expression>
                TokenType.LET -> {
                    return parseDeclaration()
                }
                // ambiguous => requires next Token for context
                TokenType.IDENTIFIER -> {
                    return parseExpression(currentToken, endTokens)
                }
                TokenType.NUMBER -> {
                    return NumberNode(currentToken.text.toLong())
                }
                else -> return null
            }

        } else {
            // Check last context

            when (lastToken.type) {
                TokenType.IDENTIFIER -> {
                    when (currentToken.type) {
                        TokenType.LPAREN -> {
                            return parseFunctionCall(lastToken, TokenType.COMMA, TokenType.RPAREN)
                        }
                        TokenType.DEF -> {
                            return parseAssignment(lastToken)
                        }
                        else -> return null
                    }
                }
                else -> return null
            }
        }
    }

    private fun parseAssignment(lastToken: Token): AssignmentNode {
        val value = parseExpression() ?: invalidSyntax("Value expected for assignment")
        return AssignmentNode(lastToken.text, value)
    }


    private fun parseFunctionCall(identifier: Token, separator: TokenType, endtype: TokenType): FunctionCallNode {
        val params = parseParams(separator, endtype)
        return FunctionCallNode(identifier.text, params)
    }

    private fun parseParams(separator: TokenType?, endType: TokenType): ParameterNode {
        val paramNode = ParameterNode()
        val paramClosers = mutableSetOf(endType)
        if (separator != null) paramClosers.add(separator)
        while (tokens.peek().type != endType) {

            val param = parseExpression(null, paramClosers) ?: invalidSyntax("Invalid argument in function call")
            paramNode.addChild(param)
            if (separator != null) {
                if (tokens.peek().type !in setOf(separator,endType)) invalidSyntax("Invalid separator in function!")
                if (tokens.peek().type == separator) tokens.pop()
            }
        }
        return paramNode
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

        val value = parseExpression() ?: invalidSyntax("Could not find expression to assign to ${id.text}")
        return DeclarationNode(id.text, mutable, value)
    }
}

