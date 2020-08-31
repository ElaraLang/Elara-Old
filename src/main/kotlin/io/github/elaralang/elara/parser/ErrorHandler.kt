package io.github.elaralang.elara.parser

import io.github.elaralang.elara.lexer.Token

fun logError(token: Token, msg: String) {
    // Temporary Output
    println("Parser found error at $token >> $msg")
}
