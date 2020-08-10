//For the sake of simplicity, we'll have this, even though it's unused in actual code
lexer grammar ElaraLexer;

NEWLINE :  ('\r' '\n' | '\n' | '\r') -> skip;
WHITESPACE: [ \t]+ -> skip;

COMMENT: '//'  ~[\r\n]* -> skip;


LPAREN : '(';
RPAREN : ')';

LBRACE : '{';
RBRACE : '}';

LTRIANGLE : '<';
RTRIANGLE : '>';

LET : 'let';
EXTEND : 'extend';
RETURN : 'return';
MUT : 'mut';
STRUCT : 'struct';
NAMESPACE : 'namespace';
IMPORT : 'import';

DEF : '=';
ARROW : '=>';
EQUAL : '==';

//Literals
STRING : '"' ( '\\"' | . )*? '"';
//naive but it works...
NUMBER : [0-9]+;

COMMA : ',';
COLON : ':';
SLASH : '/';

fragment PLAIN_TEXT : [a-zA-Z]+;
NAMESPACE_IDENTIFIER : PLAIN_TEXT;
IDENTIFIER : PLAIN_TEXT | ~('(' | ')')+;
