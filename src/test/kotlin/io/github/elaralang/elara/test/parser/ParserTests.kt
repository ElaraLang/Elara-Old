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

    @Test
    fun `Test Correct Parsing of Basic Assignment Statement`() {
        val text = """
            let a = 3
        """.trimIndent()
        val tokens = lexer.lex(text)
        val ast = ElaraParser(tokens).parse()

        assertEquals(RootNode().apply {
            addChild(DeclarationNode("a", false, NumberNode(3)))
        }, ast)
    }

    @Test
    fun `Test Correct Parsing of Basic Reassignment Statement`() {
        val text = """
            let mut a = 3
            a = 4
        """.trimIndent()
        val tokens = lexer.lex(text)
        val ast = ElaraParser(tokens).parse()

        assertEquals(RootNode().apply {
            addChild(DeclarationNode("a", true, NumberNode(3)))
            addChild(AssignmentNode("a", NumberNode(4)))
        }, ast)
    }

    @Test
    fun `Test Correct Parsing of Function Call`() {
        val text = """
            someFunction(param1, 123, param3)
        """.trimIndent()
        val tokens = lexer.lex(text)
        val ast = ElaraParser(tokens).parse()
        print(ast)
        assertEquals(RootNode().apply {
            addChild(
                    FunctionCallNode(
                            "someFunction",
                            ParameterNode().apply {
                                addChild(IdentifierNode("param1"))
                                addChild(NumberNode(123))
                                addChild(IdentifierNode("param3"))
                            }
                    )
            )
        }, ast)
    }
    @Test
    fun `Test Correct Parsing of Function Call Without Parentheses`() {
        val text = """
            someFunction param1 123 param3
        """.trimIndent()
        val tokens = lexer.lex(text)
        val ast = ElaraParser(tokens).parse()

        assertEquals(RootNode().apply {
            addChild(
                FunctionCallNode(
                    "someFunction",
                    ParameterNode().apply {
                        addChild(IdentifierNode("param1"))
                        addChild(NumberNode(123))
                        addChild(IdentifierNode("param3"))
                    }
                )
            )
        }, ast)
    }
    @Test
    fun `Test Correct Parsing of Function Definition`() {
        val text = """
            :(Int a, b = 10) => {
                let c = 5
            }
        """.trimIndent()
        val tokens = lexer.lex(text)
        val ast = ElaraParser(tokens).parse()
        print(ast)
        assertEquals(RootNode().apply {
            addChild(
                    FunctionNode(
                            TypedParameterNode().apply {
                                addChild(TypedIdentifierNode("a", null, "Int"))
                                addChild(TypedIdentifierNode("b", NumberNode(10), null))
                            },
                            ScopeNode().apply {
                                addChild(DeclarationNode("c", false, NumberNode(5)))
                            }
                    )
            )
        }, ast)
    }

    @Test
    fun `Test Correct Parsing of Struct`() {
        val text = """
            struct Human {
                Int age = 18
                height = 177
                Int speed = 5
            }
        """.trimIndent()
        val tokens = lexer.lex(text)
        val ast = ElaraParser(tokens).parse()
        print(ast)
        assertEquals(RootNode().apply {
            addChild(
                    StructNode(
                            "Human",
                            TypedParameterNode().apply {
                                addChild(
                                        TypedIdentifierNode(
                                                "age",
                                                NumberNode(18),
                                                "Int"
                                        )
                                )
                                addChild(
                                        TypedIdentifierNode(
                                                "height",
                                                NumberNode(177)
                                        )
                                )
                                addChild(
                                        TypedIdentifierNode(
                                                "speed",
                                                NumberNode(5),
                                                "Int"
                                        )
                                )
                            }
                    )
            )
        }, ast)
    }
}
