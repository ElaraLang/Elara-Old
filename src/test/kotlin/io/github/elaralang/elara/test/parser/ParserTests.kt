package io.github.elaralang.elara.test.parser

import io.github.elaralang.elara.lexer.ElaraLexer
import io.github.elaralang.elara.parser.*
import io.github.elaralang.elara.parser.ElaraParser
import org.junit.jupiter.api.Test
import javax.naming.Context
import kotlin.system.measureNanoTime
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
        println(ast)
        assertEquals(RootNode().apply {
            addChild(
                    ExpressionNode().apply {
                        addChild(DeclarationNode("a", false, number(3)))
                    }
            )
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
            addChild(
                    ExpressionNode().apply {
                        addChild(
                                DeclarationNode("a", true, number(3))
                        )
                    }
            )
            addChild(
                    ExpressionNode().apply {
                        addChild(
                                AssignmentNode("a", number(4))
                        )
                    }
            )
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
                    ExpressionNode().apply {
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
                    }
            )
        }, ast)
    }
    @Test
    fun `Test Correct Parsing of Function Call Without Parentheses`() {
        val text = """
            this someFunction param1 123 param3
        """.trimIndent()
        val tokens = lexer.lex(text)
        val ast = ElaraParser(tokens).parse()

        assertEquals(RootNode().apply {
            addChild(
                    ExpressionNode().apply {
                        addChild(
                            ContextNode("this").apply {
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
                            }
                        )
                    }
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
                    ExpressionNode().apply {
                        addChild(
                            FunctionNode(
                                    TypedParameterNode().apply {
                                        addChild(TypedIdentifierNode("a", null, "Int"))
                                        addChild(TypedIdentifierNode("b", number(10), null))
                                    },
                                    ScopeNode().apply {
                                        addChild(
                                                ExpressionNode().apply {
                                                    addChild(DeclarationNode("c", false, number(5)))
                                                }
                                        )
                                    }
                            )
                        )
                    }
            )
        }, ast)
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
        val ast = ElaraParser(tokens).parse()
        print(ast)
        assertEquals(RootNode().apply {
            addChild(
                    ExpressionNode().apply {
                        addChild(
                            StructNode(
                                    "Human",
                                    TypedParameterNode().apply {
                                        addChild(
                                                TypedIdentifierNode(
                                                        "age",
                                                        number(18),
                                                        "Int"
                                                )
                                        )
                                        addChild(
                                                TypedIdentifierNode(
                                                        "height",
                                                        number(177)
                                                )
                                        )
                                        addChild(
                                                TypedIdentifierNode(
                                                        "speed",
                                                        null,
                                                        "Int"
                                                )
                                        )
                                    }
                            )
                        )
                    }
            )
        }, ast)
    }


    @Test
    fun `Test Correct Parsing of Struct field getter`() {
        val text = """
            someStructInstance.someField
        """.trimIndent()
        val tokens = lexer.lex(text)
        val ast = ElaraParser(tokens).parse()
        print(ast)
        assertEquals(RootNode().apply {
            addChild(
                    ExpressionNode().apply {
                        addChild(
                            ContextNode(
                                    "someStructInstance"
                            ).apply { addChild(IdentifierNode("someField")) }
                        )
                    }
            )
        }, ast)
    }

    @Test
    fun `Test Correct Parsing of Struct field setter`() {
        val text = """
            someStructInstance.someField = 5
        """.trimIndent()
        val tokens = lexer.lex(text)
        val ast = ElaraParser(tokens).parse()
        print(ast)
        assertEquals(RootNode().apply {
            addChild(
                    ExpressionNode().apply {
                        addChild(
                            ContextNode(
                                    "someStructInstance"
                            ).apply {
                                addChild(AssignmentNode("someField", NumberNode(5)))
                            }
                        )
                    }
            )
        }, ast)
    }
    @Test
    fun `Test Correct Parsing of Struct function Call`() {
        val text = """
            someStructInstance.someFunction(15)
        """.trimIndent()
        val tokens = lexer.lex(text)
        val ast = ElaraParser(tokens).parse()
        print(ast)
        assertEquals(RootNode().apply {
            addChild(
                    ExpressionNode().apply {
                        addChild(
                            ContextNode(
                                    "someStructInstance"
                            ).apply {
                                addChild(
                                        FunctionCallNode(
                                                "someFunction",
                                                ParameterNode().apply {
                                                    addChild(
                                                            NumberNode(15)
                                                    )
                                                }
                                        )
                                )
                            }
                        )
                    }
            )
        }, ast)
    }

    @Test
    fun `Test Correct Parsing of If expressions`() {
        val text = """
            if test() => {
                this print a
            }
        """.trimIndent()
        val tokens = lexer.lex(text)
        val ast = ElaraParser(tokens).parse()
        print(ast)
        assertEquals(RootNode().apply {
            addChild(
                    ExpressionNode().apply {
                        addChild(
                                ConditionalNode(
                                        ExpressionNode().apply {
                                            addChild(
                                            FunctionCallNode("test", ParameterNode())
                                            )
                                        },
                                        ExpressionNode().apply {
                                            addChild(
                                                    ScopeNode().apply {
                                                        addChild(
                                                                ExpressionNode().apply {
                                                                    addChild(
                                                                        ContextNode("this").apply {
                                                                            addChild(
                                                                                    FunctionCallNode("print", ParameterNode().apply { addChild(IdentifierNode("a")) })
                                                                            )
                                                                        }
                                                                    )
                                                                }
                                                        )
                                                    }
                                            )
                                        },
                                        null
                                )
                        )
                    }
            )
        }, ast)
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
        val ast = ElaraParser(tokens).parse()
        print(ast)
        assertEquals(RootNode().apply {
            addChild(
                    ExpressionNode().apply {
                        addChild(
                                ConditionalNode(
                                        ExpressionNode().apply {
                                            addChild(
                                                    FunctionCallNode("test", ParameterNode())
                                            )
                                        },
                                        ExpressionNode().apply {
                                            addChild(
                                                    ScopeNode().apply {
                                                        addChild(
                                                                ExpressionNode().apply {
                                                                    addChild(
                                                                            ContextNode("this").apply {
                                                                                addChild(
                                                                                        FunctionCallNode("print", ParameterNode().apply { addChild(IdentifierNode("a")) })
                                                                                )
                                                                            }
                                                                    )
                                                                }
                                                        )
                                                    }
                                            )
                                        },
                                        ScopeNode().apply {
                                            addChild(
                                                    ExpressionNode().apply {
                                                        addChild(
                                                                ContextNode("this").apply {
                                                                    addChild(
                                                                            FunctionCallNode("print", ParameterNode().apply { addChild(IdentifierNode("b")) })
                                                                    )
                                                                }
                                                        )
                                                    }
                                            )
                                        }
                                )
                        )
                    }
            )
        }, ast)
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
        val ast = ElaraParser(tokens).parse()
        print(ast)
        assertEquals(RootNode().apply {
            addChild(
                    ExpressionNode().apply {
                        addChild(
                                ConditionalNode(
                                        ExpressionNode().apply {
                                            addChild(
                                                    FunctionCallNode("test", ParameterNode())
                                            )
                                        },
                                        ExpressionNode().apply {
                                            addChild(
                                                    ScopeNode().apply {
                                                        addChild(
                                                                ExpressionNode().apply {
                                                                    addChild(
                                                                            ContextNode("this").apply {
                                                                                addChild(
                                                                                        FunctionCallNode("print", ParameterNode().apply { addChild(IdentifierNode("a")) })
                                                                                )
                                                                            }
                                                                    )
                                                                }
                                                        )
                                                    }
                                            )
                                        },
                                        ConditionalNode(
                                                ExpressionNode().apply {
                                                    addChild(
                                                            FunctionCallNode("otherTest", ParameterNode())
                                                    )
                                                },
                                                ExpressionNode().apply {
                                                    addChild(
                                                            ScopeNode().apply {
                                                                addChild(
                                                                        ExpressionNode().apply {
                                                                            addChild(
                                                                                    ContextNode("this").apply {
                                                                                        addChild(
                                                                                                FunctionCallNode("print", ParameterNode().apply { addChild(IdentifierNode("a")) })
                                                                                        )
                                                                                    }
                                                                            )
                                                                        }
                                                                )
                                                            }
                                                    )
                                                },
                                                null
                                        )
                                )
                        )
                    }
            )
        }, ast)
    }

    @Test
    fun `Test Correct Parsing of Extension scope`() {
        val text = """
            extend Something {
                let sayHi = :() => this print "hi"
            }
        """.trimIndent()
        val tokens = lexer.lex(text)
        val ast = ElaraParser(tokens).parse()
        print(ast)
        assertEquals(RootNode().apply {
            addChild(
                    ExpressionNode().apply {
                        addChild(
                                ExtensionNode("Something").apply {
                                    addChild(
                                            DeclarationNode(
                                                    "sayHi",
                                                    false,
                                                    ExpressionNode().apply {
                                                        addChild(
                                                                ContextNode("this").apply {
                                                                    addChild(
                                                                            FunctionCallNode(
                                                                                    "print",
                                                                                    ParameterNode().apply {
                                                                                        addChild(StringNode("hi"))
                                                                                    }
                                                                            )
                                                                    )
                                                                }
                                                        )
                                                    }
                                            )
                                    )
                                }
                        )
                    }
            )
        }, ast)
    }


    private fun number(num: Long): ExpressionNode {
        return ExpressionNode().apply { addChild(NumberNode(num)) }
    }
}
