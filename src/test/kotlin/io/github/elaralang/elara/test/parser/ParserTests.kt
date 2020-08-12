package io.github.elaralang.elara.test.parser

import io.github.elaralang.elara.lexer.ElaraLexer
import io.github.elaralang.elara.parser.AssignmentNode
import io.github.elaralang.elara.parser.ElaraParser
import io.github.elaralang.elara.parser.NumberNode
import io.github.elaralang.elara.parser.RootNode
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * @author AlexL
 */
class ParserTests {
    private val lexer = ElaraLexer()

    @Test
    fun `Test Correct Parsing of Basic Statement`() {
        val text = """
            let a = 3
        """.trimIndent()
        val tokens = lexer.lex(text)
        val ast = ElaraParser(tokens).parse()

        assertEquals(RootNode().apply {
            addChild(AssignmentNode("a", NumberNode(3)))
        }, ast)
    }
}
