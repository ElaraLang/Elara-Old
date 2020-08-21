package io.github.elaralang.elara.exceptions

import kotlin.RuntimeException

class ElaraRuntimeException(msg: String): java.lang.RuntimeException(msg)

fun elaraRuntimeException(msg: String): Nothing {
    // TODO:: Make exceptions more informative
    println("Runtime exception occurred. $msg")
    throw ElaraRuntimeException(msg)
}