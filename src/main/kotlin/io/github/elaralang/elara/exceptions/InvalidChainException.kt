package io.github.elaralang.elara.exceptions

import java.lang.Exception

class InvalidChainException(msg: String): Exception("Parser misidentified a chain... -> $msg")

fun invalidChain(msg: String): Nothing {
    throw InvalidChainException(msg)
}