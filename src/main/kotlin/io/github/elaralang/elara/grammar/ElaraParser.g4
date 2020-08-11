parser grammar ElaraParser;

options { tokenVocab=ElaraLexer; }

namespaceName : IDENTIFIER (SLASH IDENTIFIER)*;

namespaceDeclaration : NAMESPACE namespaceName;
importDeclaration : IMPORT namespaceName;

headers
    :
    namespaceDeclaration?
    importDeclaration*
    ;

file
    : headers body EOF
    ;

body :
    line (NEWLINE line)* NEWLINE*
    ;

line : (statement)
     | (expression)
     | NEWLINE+ //empty line
     ;

statement : variableDefinition
    ;

variableDefinition :
    LET IDENTIFIER DEF expression
    ;

expression
    : literal
    | function
    | functionCall
    | variableReference
    ;

function
    : functionSignature block
    ;

functionSignature
    : LPAREN (functionParameter (COMMA functionParameter)*)? RPAREN
      ARROW IDENTIFIER?
    ;

functionParameter
    : IDENTIFIER IDENTIFIER (DEF expression)?
    ;

block
    : LBRACE
     line*
     RBRACE
    ;

literal :
    NUMBER
    ;

variableReference
    : IDENTIFIER
    ;

functionCall
    : (IDENTIFIER LPAREN RPAREN)
    | IDENTIFIER expression+
    | IDENTIFIER LPAREN (expression (COMMA expression)*) RPAREN

    ;
