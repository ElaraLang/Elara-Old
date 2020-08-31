package io.github.elaralang.elara.parser

import io.github.elaralang.elara.exceptions.unwindWithError
import io.github.elaralang.elara.lexer.Token
import io.github.elaralang.elara.lexer.TokenType
import java.util.*

class TokenStack(list: List<Token>) : Stack<Token>() {
    init {
        cleanAndReverseFrom(list)
    }

    private fun cleanAndReverseFrom(list: List<Token>) {
        var isLastNewLine = false
        for (token in list) {
            if (isLastNewLine && token.type == TokenType.NEWLINE) continue
            if (!isLastNewLine && token.type == TokenType.EOF) push(Token(TokenType.NEWLINE, "\n"))
            isLastNewLine = token.type == TokenType.NEWLINE
            push(token)

        }
        reverse()
    }

    infix fun expect(type: TokenType): Token {
        val token = pop()

        if (token.type != type) {
            throw unwindWithError(token, "Expected token of type: $type; Found token $token")
        }

        return token
    }

    infix fun popIf(type: TokenType): Boolean {
        return if (peek().type == type) {
            pop()
            true
        } else false
    }

    infix fun isAt(type: TokenType): Boolean {
        return peek().type == type
    }

    infix fun isAt(type: Set<TokenType>): Boolean {
        if (empty()) return false
        return peek().type in type
    }

    infix fun isNotAt(type: TokenType): Boolean {
        return peek().type != type
    }

    infix fun isNotAt(type: Set<TokenType>): Boolean {
        return peek().type !in type
    }

    fun cleanTop() {
        while (peek().type == TokenType.NEWLINE) pop()
    }
}
