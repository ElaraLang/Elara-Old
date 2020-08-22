package io.github.elaralang.elara.test.parser

import io.github.elaralang.elara.lexer.ElaraLexer
import io.github.elaralang.elara.parser.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * @author AlexL
 */
class ParserTests {
    private val lexer = ElaraLexer()
    val a = mapOf("root" to "1.0.0", "lib" to "1.0.0")

    @Test
    fun `Test Correct Parsing of Basic Assignment Statement`() {

        val text = """
            let a = 3
        """.trimIndent()
        val tokens = lexer.lex(text)
        val ast = ElaraParser().parse(TokenStack(tokens))

        assertEquals(
            listOf(
                VariableDeclarationStatement(false, "a", Literal(3))
            ), ast
        )
    }

    @Test
    fun `Test Correct Parsing of Basic Reassignment Statement`() {
        val text = """
            let mut a = 3
            a = 4
        """.trimIndent()
        val tokens = lexer.lex(text)
        val ast = ElaraParser().parse(TokenStack(tokens))

        assertEquals(
            listOf(
                VariableDeclarationStatement(true, "a", Literal(3)),
                ExpressionStatement(Assignment("a", Literal(4)))
            ), ast
        )
    }

    @Test
    fun `Test Correct Parsing of Function Call`() {
        val text = """
            someFunction(param1, 123, param3)
        """.trimIndent()
        val tokens = lexer.lex(text)
        val ast = ElaraParser().parse(TokenStack(tokens))

        assertEquals(
            listOf(
                ExpressionStatement(
                    FunctionInvocation(
                        ElaraUnit, listOf(
                            Variable("param1"), Literal(123), Variable("param3")
                        )
                    )
                )
            ), ast
        )
    }


    @Test
    fun `Test Correct Parsing of Function Call with Named Parameters`() {
        val text = """
            someFunction(a = param1, b = 123, c = param3)
        """.trimIndent()
        val tokens = lexer.lex(text)
        val ast = ElaraParser().parse(TokenStack(tokens))
        print(ast)
    }


    @Test
    fun `Test Correct Parsing of Function Call Without Parentheses`() {
        val text = """
            this someFunction param1 123 param3
        """.trimIndent()
        val tokens = lexer.lex(text)
        val ast = ElaraParser().parse(TokenStack(tokens))
    }

    @Test
    fun `Test Correct Parsing of Function Definition`() {
        val text = """
            :(Int a, b = 10) => {
                let c = 5
            }
        """.trimIndent()
        val tokens = lexer.lex(text)
        val ast = ElaraParser().parse(TokenStack(tokens))
    }

    @Test
    fun `Test Correct Parsing of Struct`() {
        val text = """
            struct Human {
                Int age = 18
                height = 177
                Int speed
            }
        """.trimIndent()
        val tokens = lexer.lex(text)
        val ast = ElaraParser().parse(TokenStack(tokens))
        print(ast)

    }


    @Test
    fun `Test Correct Parsing of Struct field getter`() {
        val text = """
            someStructInstance.someField
        """.trimIndent()
        val tokens = lexer.lex(text)
        val ast = ElaraParser().parse(TokenStack(tokens))
        print(ast)

        val a = 5 + 6.toLong()
    }

    @Test
    fun `Test Correct Parsing of Struct field setter`() {
        val text = """
            someStructInstance.someField = 5
        """.trimIndent()
        val tokens = lexer.lex(text)
        val ast = ElaraParser().parse(TokenStack(tokens))
        print(ast)
    }

    @Test
    fun `Test Correct Parsing of Struct function Call`() {
        val text = """
            someStructInstance.someFunction(15)
        """.trimIndent()
        val tokens = lexer.lex(text)
        val ast = ElaraParser().parse(TokenStack(tokens))
        print(ast)
    }

    @Test
    fun `Test Correct Parsing of If expressions`() {
        val text = """
            if test() => ? print "a"
            else => ? print "b"
        """.trimIndent()
        val tokens = lexer.lex(text)
        val ast = ElaraParser().parse(TokenStack(tokens))
        print(ast)
    }

    @Test
    fun `Test Correct Parsing of If-else expressions`() {
        val text = """
            if test() => {
                this print a
            } else => {
                this print b
            }
        """.trimIndent()
        val tokens = lexer.lex(text)
        val ast = ElaraParser().parse(TokenStack(tokens))
        print(ast)
    }

    @Test
    fun `Test Correct Parsing of If-else ladder expressions`() {
        val text = """
            if test() => {
                this print a
            } else if otherTest() => {
                this print b
            }
        """.trimIndent()
        val tokens = lexer.lex(text)
//        val ast = ElaraParser(tokens).parse()
//        print(ast)
    }

    @Test
    fun `Test Correct Parsing of Extension scope`() {
        val text = """
            extend Something {
                let sayHi = :() => this print "hi"
            }
        """.trimIndent()
        val tokens = lexer.lex(text)
//        val ast = ElaraParser(tokens).parse()
//        print(ast)

    }


    @Test
    fun `Test Correct Parsing of Arithmetic Operations`() {
        val text = """
            let expr = (a + b + c) + b * c + a + (b * c)
        """.trimIndent()
        val tokens = lexer.lex(text)
//        val ast = ElaraParser(tokens).parse()
//        print(ast)

    }

    @Test
    fun `Test Correct Parsing of Boolean Expressions`() {
        val text = """
            a && (b == c) || (a ^ b ^ c)
        """.trimIndent()
        val tokens = lexer.lex(text)
//        val ast = ElaraParser(tokens).parse()
//        print(ast)

    }

    @Test
    fun `Test Correct Parsing of Function Call without parentheses`() {
        val text = """
            someFunction param1 123
        """.trimIndent()
        val tokens = lexer.lex(text)
//        val ast = ElaraParser(tokens).parse()

    }
}
