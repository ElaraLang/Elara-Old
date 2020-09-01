# Elara Language Specification
This uses ANTLR Grammar Syntax to describe all elements of the Elara Language.

## Names and conventions
**Types** have names in PascalCase, and should be short and simple, eg `Int`, `String`, `Person`

**Identifiers** are the names used for variables and parameters. 
Identifiers can be any unicode character, with the exceptions of `,`, `.`, `:`, `#`, `[]`, `()`, `{}` `"`, and whitespace as these symbols are all reserved for other tokens.
For longer identifiers, kebab-case should be used, eg `print-values`, `for-each`, `add-1` 

## Types 
**Type** refers to an element in the type system. Currently, this is either a **Struct**, or a **Function Type**

**Struct** refers to the element defined with the `struct` syntax. Structs are data-only types composed of multiple `properties`

**Function Types** are the types used for first class functions. They follow the format 
```
LPAREN (type (COMMA type)*)? RPAREN ARROW type;
```
for example `(Int) => String`

## Simple Syntax

Variable Declaration:

`let [name] = [value]`
```antlrv4
LET identifer EQUAL expression;
```

Variable Declaration with Explicit Type:

`let [name]: [Type] = value`
```antlrv4
LET identifer COLON type EQUAL expression;
```

Function Literals:
`([Type] [name], [Type2] [name2], etc) => ReturnType? {}`
```antlrv4
functionLiteral:
    LPAREN (param (COMMA param)*)? RPAREN ARROW type? BLOCK
    ;

param: 
    type identifier
    ;
```

Single Expression Functions:

`let [name] => [expression]`
```antlrv4
LET identifier ARROW expression;
```

Function Calling:

Simple function calling is the same as most languages:
`function-name()`

For functions with arguments, parentheses can commas can be omitted:

`function-name(arg1, arg2)`
`function-name arg1 arg2`

Receiver functions can also be invoked as if they were methods:

```
receiver-function param1 param2
param1 receiver-function param2
receiver-function(param1, param2)
param1.receiver-function(param2)
```

These are all functionally identical


### Structs
Struct declaration is simple:
```
struct [StructName] {
    Type property-name
} 
```

```antlrv4
structDeclaration: 
    STRUCT identifier LCPAREN structBody RCPAREN
    ;

structBody:
    structPropertyDeclaration*
    ;

structPropertyDeclaration:
    identifier identifier (EQUAL expression)?
    ;
```

Struct extending can be done anywhere in any file:
```
extend [StructName] {
    [let] | [struct]
}
```

```antlrv4
structExtension:
    EXTEND identifier LCPAREN extensionBody RCPAREN
    ;
extensionBody:
    (variableDeclaration | structDeclaration)+
    ;
```

### Generics

Generics will *probably* only be applicable to functions, with the following syntax:
```
<T>
let func-name = () => {}
```

This creates a single, unbound type parameter named `T`

Bound parameters are simple:
`<T: Int>`

Parameters can also be bound by contract, eg:
```
<T { add(T) => Unit }>
```
will only accept types that define a function named `add` with the signature `T => Unit`

We can of course combine the two:
```
<T: Number { add(T) => Unit }> 
``` 

```antlrv4
typeParameters:
    LANGLE_BRACKET typeParameter+ RANGLE_BRACKET
    ;

typeParameter
    : unboundTypeParameter
    | boundTypeParameter
    ;

unboundTypeParameter:
    identifier
    ;

boundTypeParameter
    : upperBoundTypeParameter
    | contractBoundTypeParameter
    | fullyBoundTypeParameter
    ;

upperBoundTypeParameter:
    identifier COLON identifer
    ;
 
contractBoundTypeParameter: 
    identifer LCPAREN contractSpec+ RCPAREN
    ;

contractSpec: 
    identifier LPAREN (identifier)* RPAREN ARROW identifier
    ;

fullyBoundTypeParameter:
   upperBoundTypeParameter LCPAREN contractSpec+ RCPAREN
    ;
```

## Runtime

### Simple Types
* Int - represents a single integer, implementation will probably be 64-bit
* Float - a floating-point number, probably 64-bit again
* Number - any number
* Char - a single Unicode character
* String - a string of characters
* Any - any type

