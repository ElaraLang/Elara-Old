package io.github.elaralang.elara.lexer

class Token(val type: TokenType, val text: String) {

    override fun toString(): String {
        return "${type.name}($text)"
    }

}
