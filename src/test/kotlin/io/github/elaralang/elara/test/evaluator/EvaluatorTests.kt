package io.github.elaralang.elara.test.evaluator

import io.github.elaralang.elara.evaluator.ElaraEvaluator
import io.github.elaralang.elara.lexer.ElaraLexer
import io.github.elaralang.elara.parser.ElaraParser
import org.junit.jupiter.api.Test

class EvaluatorTests {
    val lexer = ElaraLexer()
    val parser = ElaraParser()
    val evaluator = ElaraEvaluator()

    @Test
    fun `Test Basic Evaluation`() {
        val code = """
            let a = 3
            a + 2
        """.trimIndent()

        val tokens = lexer.lex(code)
        val ast = parser.parse(tokens)

        evaluator.evaluate(ast)
        evaluator.env.
    }
}
