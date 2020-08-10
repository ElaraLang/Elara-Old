package io.github.elaralang.elara.lexer

class Token(private val type: TokenType, private val value: String) {

    override fun toString(): String {
        return "${type.name}, $value"
    }

}