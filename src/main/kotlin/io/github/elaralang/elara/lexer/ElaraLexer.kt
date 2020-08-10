package io.github.elaralang.elara.lexer

import java.lang.StringBuilder

class ElaraLexer {

    fun lex(value: String): List<Token> {

        val tokens = mutableListOf<Token>()

        // Creating regex to capture tokens
        val tokenPatternsBuffer = TokenType.values().joinToString(separator = "") {
            "|(?<${it.name}>${it.regex})"
        }


        val tokenPatterns = tokenPatternsBuffer.substring(1).toRegex().toPattern()
        val matcher = tokenPatterns.matcher(value)

        // Loop through matcher till all tokens are found
        while (matcher.find()) {
            for (tokenType in TokenType.values()) {
                if (matcher.group(tokenType.name) != null) {
                    tokens.add(Token(tokenType, matcher.group(tokenType.name)))
                }
            }
        }
        return tokens

    }

}
