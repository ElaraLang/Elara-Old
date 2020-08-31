package io.github.elaralang.elara.test.parser

import io.github.elaralang.elara.lexer.ElaraLexer
import io.github.elaralang.elara.parser.*
import org.intellij.lang.annotations.Identifier
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
        val ast = ElaraParser().parse(TokenStack(tokens))
        println(ast)
        val expectation = mutableListOf<Statement>(
            VariableDeclarationStatement(false, "a", Literal(3))
        )
        println(expectation)
        assertEquals(
            expectation.toString(), ast.toString()
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
        println(ast)
        val expectation = mutableListOf<Statement>(
            VariableDeclarationStatement(true, "a", Literal(3)),
            ExpressionStatement(Assignment("a", Literal(4)))
        )
        println(expectation)
        assertEquals(
            expectation.toString(), ast.toString()
        )
    }

    @Test
    fun `Test Correct Parsing of Function Call`() {
        val text = """
            someFunction(param1, 123, param3)
        """.trimIndent()
        val tokens = lexer.lex(text)
        val ast = ElaraParser().parse(TokenStack(tokens))
        println(ast)
        val expectation = mutableListOf<Statement>(
            ExpressionStatement(
                FunctionInvocation(
                    Variable("someFunction"),
                    listOf<Expression> (
                        Variable("param1"),
                        Literal(123),
                        Variable("param3")
                    )
                )
            )
        )
        println(expectation)
        assertEquals(
            expectation.toString(), ast.toString()
        )

    }


    @Test
    fun `Test Correct Parsing of Function Call with Named Parameters`() {
        val text = """
            someFunction(a = param1, b = 123, c = param3)
        """.trimIndent()
        val tokens = lexer.lex(text)
        val ast = ElaraParser().parse(TokenStack(tokens))
        println(ast)
        val expectation = mutableListOf<Statement>(
            ExpressionStatement(
                FunctionInvocation(
                    Variable("someFunction"),
                    listOf<Expression> (
                        Assignment("a", Variable("param1")),
                        Assignment("b", Literal(123)),
                        Assignment("c", Variable("param3"))
                    )
                )
            )
        )
        println(expectation)
        assertEquals(
            expectation.toString(), ast.toString()
        )

    }


    @Test
    fun `Test Correct Parsing of Function Call Without Parentheses`() {
        val text = """
            this someFunction param1 123 param3
        """.trimIndent()
        val tokens = lexer.lex(text)
        val ast = ElaraParser().parse(TokenStack(tokens))
        println(ast)
        val expectation = mutableListOf<Statement>(
            ExpressionStatement(
                FunctionInvocation(
                    ContextExpression(Variable("this"), "someFunction"),
                    listOf<Expression> (
                        Variable("param1"),
                        Literal(123),
                        Variable("param3")
                    )
                )
            )
        )
        println(expectation)
        assertEquals(
            expectation.toString(), ast.toString()
        )
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
        println(ast)
        val expectation = mutableListOf<Statement>(
            ExpressionStatement(
                FunctionDefinition(
                    "ElaraUnit",
                    listOf<FunctionArgument>(
                        FunctionArgument("Int", "a", null),
                        FunctionArgument(null, "b", Literal(10))
                    ),
                    BlockStatement(
                        listOf(
                            VariableDeclarationStatement(
                                false,
                                "c",
                                Literal(5)
                            )
                        )
                    )
                )
            )
        )
        println(expectation)
        assertEquals(
            expectation.toString(), ast.toString()
        )
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
        println(ast)
        val expectation = mutableListOf<Statement>(
            StructDefinitionStatement(
                "Human",
                listOf<StructMember>(
                    StructMember("Int", "age", Literal(18)),
                    StructMember(null, "height", Literal(177)),
                    StructMember("Int", "speed", null)
                )
            )
        )
        println(expectation)
        assertEquals(
            expectation.toString(), ast.toString()
        )

    }


    @Test
    fun `Test Correct Parsing of Struct field getter`() {
        val text = """
            someStructInstance.someField
        """.trimIndent()
        val tokens = lexer.lex(text)
        val ast = ElaraParser().parse(TokenStack(tokens))
        println(ast)
        val expectation = mutableListOf<Statement>(
            ExpressionStatement(
                ContextExpression(
                    Variable("someStructInstance"),
                    "someField"
                )
            )
        )
        println(expectation)
        assertEquals(
            expectation.toString(), ast.toString()
        )
    }

    @Test
    fun `Test Correct Parsing of Struct field setter`() {
        val text = """
            someStructInstance.someField = 5
        """.trimIndent()
        val tokens = lexer.lex(text)
        val ast = ElaraParser().parse(TokenStack(tokens))
        println(ast)
        val expectation = mutableListOf<Statement>(
            ExpressionStatement(
                ContextualAssignment(
                    Variable("someStructInstance"),
                    "someField",
                    Literal(5)
                )
            )
        )
        println(expectation)
        assertEquals(
            expectation.toString(), ast.toString()
        )
    }

    @Test
    fun `Test Correct Parsing of Struct function Call`() {
        val text = """
            someStructInstance.someFunction(15)
        """.trimIndent()
        val tokens = lexer.lex(text)
        val ast = ElaraParser().parse(TokenStack(tokens))
        println(ast)
        val expectation = mutableListOf<Statement>(
            ExpressionStatement(
                FunctionInvocation(
                    ContextExpression(
                        Variable("someStructInstance"),
                        "someFunction"
                    ),
                    listOf<Expression>(
                        Literal(15)
                    )
                )
            )
        )
        println(expectation)
        assertEquals(
            expectation.toString(), ast.toString()
        )
    }

    @Test
    fun `Test Correct Parsing of If expressions`() {
        val text = """
            if test() => this print "a"
            else => this print "b"
        """.trimIndent()
        val tokens = lexer.lex(text)
        val ast = ElaraParser().parse(TokenStack(tokens))
        println(ast)
        val expectation = mutableListOf<Statement>(
            IfElseStatement(
                FunctionInvocation(
                    Variable("test"),
                    listOf()
                ),
                ExpressionStatement(
                    FunctionInvocation(
                        ContextExpression(
                            Variable("this"),
                            "print"
                        ),
                        listOf<Expression>(
                            Literal<String>("a")
                        )
                    )
                ),
                ExpressionStatement(
                    FunctionInvocation(
                        ContextExpression(
                            Variable("this"),
                            "print"
                        ),
                        listOf<Expression>(
                            Literal<String>("b")
                        )
                    )
                )
            )
        )
        println(expectation)
        assertEquals(
            expectation.toString(), ast.toString()
        )
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
        println(ast)
        val expectation = mutableListOf<Statement>(
            IfElseStatement(
                FunctionInvocation(
                    Variable("test"),
                    listOf()
                ),
                BlockStatement(
                    listOf(
                        ExpressionStatement(
                            FunctionInvocation(
                                ContextExpression(
                                    Variable("this"),
                                    "print"
                                ),
                                listOf<Expression>(
                                    Variable("a")
                                )
                            )
                        )
                    )
                ),
                BlockStatement(
                    listOf(
                        ExpressionStatement(
                            FunctionInvocation(
                                ContextExpression(
                                    Variable("this"),
                                    "print"
                                ),
                                listOf<Expression>(
                                    Variable("b")
                                )
                            )
                        )
                    )
                )
            )
        )
        println(expectation)
        assertEquals(
            expectation.toString(), ast.toString()
        )
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
        val ast = ElaraParser().parse(TokenStack(tokens))
        println(ast)
        val expectation = mutableListOf<Statement>(
            IfElseStatement(
                FunctionInvocation(
                    Variable("test"),
                    listOf()
                ),
                BlockStatement(
                    listOf(
                        ExpressionStatement(
                            FunctionInvocation(
                                ContextExpression(
                                    Variable("this"),
                                    "print"
                                ),
                                listOf<Expression>(
                                    Variable("a")
                                )
                            )
                        )
                    )
                ),
                IfElseStatement(
                    FunctionInvocation(
                        Variable("otherTest"),
                        listOf()
                    ),
                    BlockStatement(
                        listOf(
                            ExpressionStatement(
                                FunctionInvocation(
                                    ContextExpression(
                                        Variable("this"),
                                        "print"
                                    ),
                                    listOf<Expression>(
                                        Variable("b")
                                    )
                                )
                            )
                        )
                    ),
                    null
                )
            )
        )
        println(expectation)
        assertEquals(
            expectation.toString(), ast.toString()
        )
    }

    @Test
    fun `Test Correct Parsing of Extension scope`() {
        val text = """
            extend Something {
                let sayHi = :() => this print "hi"
            }
        """.trimIndent()
        val tokens = lexer.lex(text)
        val ast = ElaraParser().parse(TokenStack(tokens))
        println(ast)
        val expectation = mutableListOf<Statement>(
            ExtendStatement(
                "Something",
                BlockStatement(
                    listOf(
                        VariableDeclarationStatement(
                            false,
                            "sayHi",
                            FunctionDefinition(
                                "ElaraUnit",
                                listOf(),
                                ExpressionStatement(
                                    FunctionInvocation(
                                        ContextExpression(
                                            Variable("this"),
                                            "print"
                                        ),
                                        listOf(
                                            Literal("hi")
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
        println(expectation)
        assertEquals(
            expectation.toString(), ast.toString()
        )

    }

   
}
