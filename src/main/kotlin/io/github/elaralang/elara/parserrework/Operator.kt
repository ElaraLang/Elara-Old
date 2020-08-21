package io.github.elaralang.elara.parserrework

import io.github.elaralang.elara.exceptions.parserException
import io.github.elaralang.elara.lexer.Token
import java.lang.Exception


enum class Operator {
    ADD, SUBTRACT, MULTIPLY, DIVIDE, REMAINDER,
    EQUALS, NOTEQUALS,
    GREATER, GREATEREQUAL, LESSER, LESSEREQUAL,
    NOT,
    AND, OR, XOR;

    companion object {
        fun parseFrom(token: Token): Operator {
            try {
                return enumValueOf(token.type.toString())
            } catch (e: Exception) {
                parserException("Invalid operator $token passed to Operator.parseFrom.")
            }
        }
    }
}