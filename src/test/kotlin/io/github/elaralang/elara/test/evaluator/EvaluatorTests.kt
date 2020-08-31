package io.github.elaralang.elara.test.evaluator

import io.github.elaralang.elara.evaluator.ElaraEvaluator
import io.github.elaralang.elara.lexer.ElaraLexer
import io.github.elaralang.elara.parser.ElaraParser
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class EvaluatorTests {
    private val lexer = ElaraLexer()
    private val parser = ElaraParser()
    private val evaluator = ElaraEvaluator()

    @Test
    fun `Test Basic Evaluation`() {
        val code = """
            let a = 3
            a + 2 - 3
        """.trimIndent()

        val tokens = lexer.lex(code)
        val ast = parser.parse(tokens)

        val last = evaluator.evaluate(ast)

        println(code)
        println("Result: $last")
        assertEquals(2.0, last)
    }

    @Test
    fun `Test Basic Evaluation 2 `() {
        val code = """
            let a = false
            a && true
        """.trimIndent()

        val tokens = lexer.lex(code)
        val ast = parser.parse(tokens)

        val last = evaluator.evaluate(ast)
        println(code)
        println("Result: $last")
        assertEquals(false, last)
    }
}
