package io.github.elaralang.elara.test.parser

import io.github.elaralang.elara.lexer.ElaraLexer
import io.github.elaralang.elara.parser.ElaraParser
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis


class PerformanceTests {
    private val lexer = ElaraLexer()

    @Test
    fun `Test Parser Performance Small`() {
        val text = """
            let a = 3
        """.trimIndent()
        val lexOutput = ElaraLexer().lex(text)
        val timeTaken = measureTimeMillis {ElaraParser(lexOutput).parse() }
        println("Completed parsing in $timeTaken ms")
        assert(timeTaken < 15)
    }

    @Test
    fun `Test Lexer Performance Medium`() {
        val text = """
            let a = functionCall(test,123,a())
            let p = :(String txt) => this print txt
            p("Testing Medium input")
            struct Human {
                String name
            }
        """.trimIndent()
        val lexOutput = ElaraLexer().lex(text)
        val timeTaken = measureTimeMillis {ElaraParser(lexOutput).parse() }
        println("Completed parsing Medium input in $timeTaken ms")
        assert(timeTaken < 20)
    }

    @Test
    fun `Test Lexer Performance Large`() {
        val text = """
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
        """.trimIndent()
        val lexOutput = ElaraLexer().lex(text)
        val timeTaken = measureTimeMillis {ElaraParser(lexOutput).parse() }
        println("Completed parsing Large input in $timeTaken ms")
        assert(timeTaken < 25)
    }
}