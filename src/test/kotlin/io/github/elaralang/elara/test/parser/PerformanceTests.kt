package io.github.elaralang.elara.test.parser

import io.github.elaralang.elara.lexer.ElaraLexer
import io.github.elaralang.elara.parser.ElaraParser
import io.github.elaralang.elara.parser.TokenStack
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis


class PerformanceTests {
    private val lexer = ElaraLexer()

    @Test
    fun `Test Parser Performance Small`() {
        checkParseTimeBound(
            """
            let a = 3
        """.trimIndent(),
            15
        )
    }

    @Test
    fun `Test Parser Performance Medium`() {
        checkParseTimeBound(
            """
            let a = functionCall(test,123,a())
            let p = :(String txt) => this print txt
            p("Testing Medium input")
            struct Human {
                String name
            }
        """.trimIndent(),
            20
        )
    }

    @Test
    fun `Test Parser Performance Large`() {
        checkParseTimeBound(
            """
            let a = 5
            let b = 45
            let addTest = :(Int a, Int b) => a + b
            this print addTest(a, b)
            let subtractTest = :(Int a, Int b) => a - b
            this print subtractTest(a, b)
            let multiplyTest = :(Int a, Int b) => a * b
            this print multiplyTest(a, b)
            let divideTest = :(Int a, Int b) => a * b
            this print divideTest(a,b)
        """.trimIndent(), 25
        )
    }

    @Test
    fun `Test Parser Performance For Arithmetic`() {
        checkParseTimeBound(
            """
            let a = a + b * c + (a + b + c) * c
        """.trimIndent(), 10
        )
    }

    @Test
    fun `Test Parser Performance For Boolean Expressions`() {
        checkParseTimeBound(
            """
            let a = a && b || (c && d)
        """.trimIndent(), 10
        )
    }


    private fun checkParseTimeBound(snippet: String, upperBound: Int) {
        val tokens = ElaraLexer().lex(snippet)
        val timeTaken = measureTimeMillis { ElaraParser().parse(TokenStack(tokens)) }
        println("Completed parsing input in $timeTaken ms")
        assert(timeTaken < upperBound)
    }
}
