package io.github.elaralang.elara.test.lexer

import io.github.elaralang.elara.lexer.ElaraLexer
import io.github.elaralang.elara.parser.DeclarationNode
import io.github.elaralang.elara.parser.ElaraParser
import io.github.elaralang.elara.parser.ExpressionNode
import io.github.elaralang.elara.parser.RootNode
import org.junit.jupiter.api.Test
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis
import kotlin.test.assertEquals

class PerformanceTests {
    private val lexer = ElaraLexer()

    @Test
    fun `Test Lexer Performance Small`() {
        val text = """
            let a = 3
        """.trimIndent()
        val timeTaken = measureTimeMillis { lexer.lex(text) }
        println("Completed lexing in $timeTaken ms")
        assert(timeTaken < 60)
    }

    @Test
    fun `Test Lexer Performance Medium`() {
        val text = """
            let a = functionCall(test,123,a())
            let p = :(String txt) => print txt
            p("Testing Medium input")
            struct Human {
                String name
            }
        """.trimIndent()
        val timeTaken = measureTimeMillis { lexer.lex(text) }
        println("Completed lexing Medium input in $timeTaken ms")
        assert(timeTaken < 65)
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
            print multiplyTest(a, b)
            let divideTest = :(Int a, Int b) => a * b
            this print divideTest(a,b)
        """.trimIndent()
        val timeTaken = measureTimeMillis { lexer.lex(text) }
        println("Completed lexing Large input in $timeTaken ms")
        assert(timeTaken < 75)
    }
}