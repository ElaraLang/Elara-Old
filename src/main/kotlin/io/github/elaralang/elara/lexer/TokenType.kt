package io.github.elaralang.elara.lexer

enum class TokenType(val regex: Regex) {

    NEWLINE("\n|[\r\n]".toRegex()),
    EOF("$(?![\r\n])".toRegex()),
    COMMENT("//.+".toRegex()),
    LPAREN("\\(".toRegex()),
    RPAREN("\\)".toRegex()),
    LBRACE("\\{".toRegex()),
    RBRACE("}".toRegex()),
    LTRIANGLE("<".toRegex()),
    RTRIANGLE(">".toRegex()),

    LET("let".toRegex()),
    EXTEND("extend".toRegex()),
    RETURN("return".toRegex()),
    MUT("mut".toRegex()),

    EQUAL("==(?!>)".toRegex()),
    DEF("=(?!>)".toRegex()),
    ARROW("=>".toRegex()),

    STRING("\".*\"".toRegex()),
    NUMBER("[0-9.+]+".toRegex()),

    COMMA(",".toRegex()),
    COLON(":".toRegex()),
    IDENTIFIER("[^\"\\s)(]+".toRegex()); //this has the potential to get *very* messy...

    companion object {
        // Creating regex to capture tokens
        val MATCHING_REGEX = values().joinToString(separator = "") {
            "|(?<${it.name}>${it.regex})"
        }.substring(1) //why is this necessary? Side note: if you remove it, prepare your anus.
            .toRegex()
    }
}
