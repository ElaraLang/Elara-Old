#Elara Type System

Elara's Type System is predomininantly **Contract Based**. That is, type B can be used in place of type A if B has the contract of A. 
We define "having a contract" as either:
  * Having a superset of properties (name sensitive), in case of structs
  * Having a definition in which all of the possibilities share a contract, in the case of compound types.
  
For example:
```
struct A {
  Int foo
}

struct B {
  Int foo
  String bar
}

struct C {
  Int foobar
  String baz
}
```

Here we can use type B in place of type A, because it declares a property named "foo" with the type Int. 
However, we can not use type C, as its only Int property has a different name ("foobar" instead of "foo")


This can therefore be used as an appropriate analogy to inheritance:
```
struct Person {
  String name
  mut Int age
  Sex sex
}

extend Person {
  struct Employee {
    Job job
  }
}
```

As explained in the [Docs](./README.MD), creating a struct in an `extend` block will copy all of the properties into the declared struct.
Because of the contract based type system, an Employee can be used in place of a Person



## Compound Types

Elara supports some higher typing features, similar to languages like Haskell, in **Compound Types**
These are types composed of other types, with either an AND or OR relation.

For example:
```
type Result<T> => Some<T> | None
```

This defines a type `Result` which will accept either Some or None
Elara also adds implicit boolean conversion for compound types with 2 bounds. In the above example, Some is truthy and None is falsey

This can lead to very clean code such as ```
let result = do-some-operation()
if result => {
  result.value.blah()
} else {
  print "Something went wrong"
}
```

Compound types can also be a **Union**:
```
type NamedEntity => Entity & Named
```
This will accept any type which is assignable to both Entity and Named


Of course, these can be chained

```
type BigCompound => (A & B | (C & D)) | E
```

