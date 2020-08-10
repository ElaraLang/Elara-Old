package io.github.elaralang.elara.lexer

enum class TokenType(val re: Regex) {

    NEWLINE("\n".toRegex()),
    EOF("$(?![\r\n])".toRegex()),
    COMMENT("\\/\\/.+".toRegex()),
    LPAREN("\\(".toRegex()),
    RPAREN("\\)".toRegex()),
    LBRACE("\\{".toRegex()),
    RBRACE("\\}".toRegex()),
    LTRIANGLE("<".toRegex()),
    RTRIANGLE(">".toRegex()),
    LET("let".toRegex()),
    DEF("=(?!>)".toRegex()),
    ARROW("=>".toRegex()),
    STRING("\".*\"".toRegex()),
    NUMBER("[0-9.+]+".toRegex()),
    EXTEND("extend(?:\\s)".toRegex());

}