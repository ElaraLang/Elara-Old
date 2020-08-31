package io.github.elaralang.elara.parser


import io.github.elaralang.elara.lexer.ElaraLexer
import io.github.elaralang.elara.lexer.Token

class ElaraParser {

    fun parse(tokenList: List<Token>): List<Statement> {
        return ParserResult(TokenStack(tokenList)).parse()
    }

    fun parse(tokenStack: TokenStack): List<Statement> {
        return ParserResult(tokenStack).parse()
    }

}


fun main() {

    val lexResult = ElaraLexer().lex("""
        let mut a = Int:() => test
    """.trimIndent())
    val out = ElaraParser().parse(lexResult)
    //ElaraEvaluator().evaluate(out)
    println(out)
}
