parser grammar ElaraParser;

options { tokenVocab=ElaraLexer; }

namespaceName : NAMESPACE_IDENTIFIER (SLASH NAMESPACE_IDENTIFIER)*;

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
    line*
    ;

line : (statement NEWLINE)
     | (expression NEWLINE)
     | NEWLINE+ //empty line
     ;

statement : variableDefinition
    ;

variableDefinition :
    LET IDENTIFIER DEF expression
    ;

expression :
    literal
    ;


literal :
    NUMBER
    ;
