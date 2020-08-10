package io.github.elaralang.elara.lexer

enum class TokenType(val re: Regex) {

    NEWLINE("\n".toRegex()),
    EOF("$(?![\r\n])".toRegex()),
    COMMENT("\\/\\/.+".toRegex()),
    LPAREN("\\(".toRegex()),
    RPAREN("\\)".toRegex()),
    LCURLYBRACKET("\\{".toRegex()),
    RCURLYBRACKET("\\}".toRegex()),
    LTRIANGLE("<".toRegex()),
    RTRIANGLE(">".toRegex()),
    LET("let".toRegex()),
    DEF("=(?!>)".toRegex()),
    ARROW("=>".toRegex())

}