package io.github.elaralang.elara.test.parser

import io.github.elaralang.elara.lexer.ElaraLexer
import io.github.elaralang.elara.parser.ElaraParser
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
    fun `Test Lexer Performance Medium`() {
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
    fun `Test Lexer Performance Large`() {
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


    private fun checkParseTimeBound(snippet: String, upperBound: Int) {
        val lexOutput = ElaraLexer().lex(snippet)
        val timeTaken = measureTimeMillis { ElaraParser(lexOutput).parse() }
        println("Completed parsing input in $timeTaken ms")
        assert(timeTaken < upperBound)
    }
}
