package io.github.elaralang.elara.lexer

class ElaraLexer {

    fun lex(value: String): List<Token> {

        // Creating regex to capture tokens
        val tokenPatternsBuffer = TokenType.values().joinToString(separator = "") {
            "|(?<${it.name}>${it.regex})"
        }.substring(1) //why is this necessary?

        val tokenPatterns = tokenPatternsBuffer.toRegex()
        val results = tokenPatterns.findAll(value)

        // Loop through matcher till all tokens are found
        val tokens = results.mapNotNull { result ->
            for (tokenType in TokenType.values()) {
                val matchGroup = result.groups[tokenType.name]
                if (matchGroup != null) {
                    return@mapNotNull Token(tokenType, matchGroup.value)
                }
            }
            null
        }.toList()

        return tokens

    }

}
