package io.github.elaralang.elara.exceptions

import java.lang.RuntimeException

class InvalidTypeException(msg: String) : RuntimeException(msg)

fun invalidType(expected: String, found: String, position: String): Nothing {
    throw InvalidTypeException("Expected type $expected at $position, but found $found")
}