package io.github.elaralang.elara.parserrework


import io.github.elaralang.elara.lexer.ElaraLexer
import io.github.elaralang.elara.lexer.Token



class ElaraParser {

    fun parse(tokenList: List<Token>): List<Statement> {
        return ParserResult(TokenStack(tokenList)).parse()
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