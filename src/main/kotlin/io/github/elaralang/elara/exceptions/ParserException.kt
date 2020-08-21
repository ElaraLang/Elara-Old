package io.github.elaralang.elara.exceptions

import java.lang.Exception



class ParserException(msg: String): Exception("Parser failed... -> $msg")

fun parserException(msg: String): Nothing {
    throw ParserException(msg)
}