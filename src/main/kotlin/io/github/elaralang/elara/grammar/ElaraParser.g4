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

body
    : lines
    ;

lines
     : line (ENDLINE line)* ENDLINE*
     ;

line : statement
     | expression
     | NEWLINE+ //empty line
     ;

statement
    : variableDefinition
    | singleFunctionDefinition
    ;

variableDefinition :
    LET IDENTIFIER DEF expression
    ;

singleFunctionDefinition:
    LET IDENTIFIER ARROW expression
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
     lines
     RBRACE
    ;

literal
    : NUMBER
    | STRING
    ;

variableReference
    : IDENTIFIER
    ;

functionCall
    : (IDENTIFIER LPAREN RPAREN)
    | IDENTIFIER expression+
    | IDENTIFIER LPAREN (expression (COMMA expression)*) RPAREN

    ;
