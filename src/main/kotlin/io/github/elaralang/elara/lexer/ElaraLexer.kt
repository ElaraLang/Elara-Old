package io.github.elaralang.elara.lexer

class ElaraLexer {

    fun lex(value: String): List<Token> {

        val tokenPatterns = TokenType.MATCHING_REGEX

        val results = tokenPatterns.findAll(value)

        // Loop through results till all tokens are found
        return results.mapNotNull { result ->
            for (tokenType in TokenType.values()) {
                val matchGroup = result.groups[tokenType.name]
                if (matchGroup != null) {
                    return@mapNotNull Token(tokenType, matchGroup.value)
                }
            }
            null
        }.toList()

    }

}
