package io.github.elaralang.elara.exceptions

import java.lang.Exception

class InvalidSyntaxException(msg: String): Exception("Syntax provided was invalid -> $msg")

fun invalidSyntax(msg: String): Nothing {
    throw InvalidSyntaxException(msg)
}