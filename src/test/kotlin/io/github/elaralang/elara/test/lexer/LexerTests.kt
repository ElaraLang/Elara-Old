package io.github.elaralang.elara.test.lexer

import io.github.elaralang.elara.lexer.ElaraLexer
import io.github.elaralang.elara.lexer.TokenType.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LexerTests {

    @Test
    fun `Test Simple Lexer Output with Expression Function`() {
        val testInput = """
        let abc => print "Hello World!"
        """.trimIndent()

        val expectedTokenStream = listOf(
            LET, IDENTIFIER, ARROW, IDENTIFIER, STRING, EOF
        )

        val tokenStream = ElaraLexer().lex(testInput).map { it.type }

        assertEquals(expectedTokenStream, tokenStream)
    }

    @Test
    fun `Test Simple Lexer Output With Hello World`() {
        val testInput = """
        print "Hello World"
        """.trimIndent()

        val expectedTokenStream = listOf(
            IDENTIFIER, STRING, EOF
        )

        val tokenStream = ElaraLexer().lex(testInput).map { it.type }

        assertEquals(expectedTokenStream, tokenStream)
    }

    @Test
    fun `Test Simple Lexer Output With Factorial Function`() {
        val testInput = """
        let fact = (Int num) => {
            if num == 1 => return 1
            return fact(num - 1)
        }
        """.trimIndent()

        val expectedTokenStream = listOf(
            LET, IDENTIFIER, DEF, LPAREN, IDENTIFIER, IDENTIFIER, RPAREN, ARROW, LBRACE, NEWLINE,
            IF, IDENTIFIER, IDENTIFIER, NUMBER, ARROW, RETURN, NUMBER, NEWLINE,
            RETURN, IDENTIFIER, LPAREN, IDENTIFIER, IDENTIFIER, NUMBER, RPAREN, NEWLINE,
            RBRACE, EOF
        )

        val tokenStream = ElaraLexer().lex(testInput)
        val tokenTypes = tokenStream.map { it.type }


        assertEquals(expectedTokenStream, tokenTypes)
    }

    @Test
    fun `Test simple lexer output with basic division extension function`() {
        val testInput = """
            extend Number {
              let isDivisibleBy = (Int value) => this % value == 0
            }
        """.trimIndent()

        val expectedTokenStream = listOf(
            EXTEND, IDENTIFIER, LBRACE, NEWLINE,
            LET, IDENTIFIER, DEF, LPAREN, IDENTIFIER, IDENTIFIER, RPAREN, ARROW, IDENTIFIER, IDENTIFIER, IDENTIFIER, IDENTIFIER, NUMBER, NEWLINE,
            RBRACE, EOF
        )

        val tokenStream = ElaraLexer().lex(testInput)
        assertEquals(expectedTokenStream, tokenStream.map{it.type})
    }

    @Test
    fun `Test simple lexer output with a basic mutable variable`() {
        val testInput = """
            let mut someMutableNumber = 2
        """.trimIndent()

        val expectedTokenStream = listOf(
            LET, MUT, IDENTIFIER, DEF, NUMBER, EOF
        )
        assertEquals(expectedTokenStream, ElaraLexer().lex(testInput).map { it.type })
    }

}
