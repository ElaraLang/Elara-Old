package io.github.elaralang.elara.lexer

enum class TokenType(val regex: String) {

    NEWLINE("\n|[\r\n]"),
    EOF("$(?![\r\n])"),
    COMMENT("//.+"),
    LPAREN("\\("),
    RPAREN("\\)"),
    LBRACE("\\{"),
    RBRACE("}"),
    //LTRIANGLE("<".),
    //RTRIANGLE(">"),

    LET("let"),
    EXTEND("extend"),
    RETURN("return"),
    WHILE("while"),
    MUT("mut"),
    STRUCT("struct"),
    NAMESPACE("namespace"),
    IMPORT("import"),
    IF("if"),
    ELSE("else"),

    ADD("\\+"),
    SUBTRACT("\\-"),
    MULTIPLY("\\*"),
    DIVIDE("/"),
    REMAINDER("%"),
    AND("&&"),
    OR("\\|\\|"),
    XOR("\\^"),
    EQUALS("==(?!>)"),
    NOTEQUALS("!=(?!>)"),
    GREATEREQUAL("\\>="),
    GREATER("\\>"),
    LESSER("\\<"),
    LESSEREQUAL("\\<="),
    NOT("!"),
    DEF("=(?![>=])"),
    ARROW("=>"),

    DOT("\\."),

    BOOLEAN("(true)|(false)"),
    STRING("\".*\""),
    NUMBER("[+-]?[0-9]+(\\.[0-9]+)?"),

    COMMA(","),
    COLON(":"),
    SLASH("/"),

    IDENTIFIER("[^,.:#{}\\[\\]\"\\s)(]+"),//this has the potential to get *very* messy...
    OPERATOR("qe"); //REMOVE

    companion object {
        // Creating regex to capture tokens
        val MATCHING_REGEX by lazy {
            values().joinToString(separator = "") {
                "|(?<${it.name}>${it.regex})"
            }.substring(1) //why is this necessary? Side note: if you remove it, prepare your anus.
                .toRegex()
        }
    }
}
