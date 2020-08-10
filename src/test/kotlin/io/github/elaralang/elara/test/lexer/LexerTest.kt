package io.github.elaralang.elara.test.lexer

import io.github.elaralang.elara.lexer.ElaraLexer
import io.github.elaralang.elara.lexer.TokenType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LexerTest {

    @Test
    fun `Test Simple Lexer Output`() {
        val testInput = """
        let abc => print "Hello World!"
        """.trimIndent()

        val expectedTokenStream = listOf(
            TokenType.LET, TokenType.IDENTIFIER, TokenType.ARROW, TokenType.IDENTIFIER, TokenType.STRING, TokenType.EOF
        )

        val tokenStream = ElaraLexer().lex(testInput).map { it.type }

        assertEquals(expectedTokenStream, tokenStream)

    }

}
