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


    private fun parseExpression(lastToken: Token? = null): ASTNode? {
        val currentToken = tokens.pop()

        if (lastToken == null) {
            //No prior context

            when (currentToken.type) {
                // let test = <expression>
                TokenType.LET -> {
                    return parseDeclaration()
                }
                // ambiguous => requires next Token for context
                TokenType.IDENTIFIER -> {
                    return parseExpression(currentToken)
                }
                TokenType.NUMBER -> {
                    return NumberNode(currentToken.text.toLong())
                }
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
                    }
                }
            }
        }
        return null
    }

    fun parseAssignment(lastToken: Token): AssignmentNode {
        val value = parseExpression() ?: invalidSyntax("Value expected for assignment")
        return AssignmentNode(lastToken.text, value)
    }


    private fun parseFunctionCall(identifier: Token, separator: TokenType, endtype: TokenType): ASTNode {
        val params = parseParams(separator, endtype)
        TODO("Parse the scope")
    }

    private fun parseParams(separator: TokenType, endtype: TokenType): ASTNode {
        TODO("Parse params with separator such that it functions ")
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

