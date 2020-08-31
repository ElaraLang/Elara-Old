package io.github.elaralang.elara

import io.github.elaralang.elara.evaluator.ElaraEvaluator
import io.github.elaralang.elara.lexer.ElaraLexer
import io.github.elaralang.elara.parser.ElaraParser
import java.io.File

private val lexer = ElaraLexer()
private val parser = ElaraParser()
private val evaluator = ElaraEvaluator()

fun main(args: Array<String>) {

    if (args.isNotEmpty()) {
        val file = File(args.first())
        if (file.exists().not()) {
            System.err.println("No such file ${file.absolutePath}")
            return
        }
        val code = file.readText()
        execute(code)
        return
    }

    val arithmeticCode = """
            let a = 3
            a + 2 - 3
        """.trimIndent()
    execute(arithmeticCode)

    val booleanCode = """
            let a = false
            a && true
        """.trimIndent()

    execute(booleanCode)
}

fun execute(code: String) {
    val tokens = lexer.lex(code)
    val ast = parser.parse(tokens)

    val last = evaluator.evaluate(ast)

    println(code)
    println("Result: $last")
    println()
}
