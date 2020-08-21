package io.github.elaralang.elara.exceptions

import io.github.elaralang.elara.lexer.Token
import io.github.elaralang.elara.lexer.TokenType
import io.github.elaralang.elara.parserrework.logError

class ParseError : RuntimeException()

fun unwindWithError(token: Token, message: String): ParseError {
    logError(token,  message)
    return ParseError()
}