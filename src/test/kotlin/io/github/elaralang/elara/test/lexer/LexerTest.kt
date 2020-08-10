package io.github.elaralang.elara.test.lexer

import io.github.elaralang.elara.lexer.ElaraLexer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LexerTest {

    @Test
    fun `test lexer output`() {
        val testInput = """
        let abc => print "Hello World!"
    """.trimIndent()

        println(ElaraLexer().lex(testInput))

    }

}
