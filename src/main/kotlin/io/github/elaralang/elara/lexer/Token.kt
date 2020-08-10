package io.github.elaralang.elara.lexer

class Token(val type: TokenType, val value: String) {

    override fun toString(): String {
        return "${type.name} - $value"
    }

}
