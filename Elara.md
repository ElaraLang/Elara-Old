# Types

## Number Types

### Number
The parent type for Dec and Int, can be any number.

### Int
A child of Number, can be any integer.

### Dec
A child of Number, can be any decimal.


# Variable Syntax

### Immutable variables

```
let someNumber = 2
```

### Mutable variables

```
let mut someMutableNumber = 2
```

# Function Syntax

### without args - multiple lines

```
let printHello = {
  print "Hello"
}
```

### without args - one line

```
let printHello => print "Hello"
```

### with args

```
let print = (String msg) => {
  dosomethingwith msg
}
```

### Type parameter syntax
```
<T: add(T), take(T)>
let print-info = (T a, T b) => {
  print a add b
  print a take b
}
```

### Default paramters

```
let idk = (String a = "Something", String b) => {
    blah
}
```

### extension functions + reciever functions

```
let doSomethingWith = (SomeStruct value) => {
  print value.data
}
```
Can be called as both 
```
doSomethingWith someStruct
```
and
```
someStruct doSomethingWith()
```

# Structs

```
struct Person {
      int age
      String name
      int height = 110
}


```

```
let somePerson = Person(18, "test", 160)
```

# Casting

### Number/Dec -> Int
Rounded

# NameSpace

```
namespace something/anotherthing
```

# Importing

```
import something/anotherthing
```

# Examples

## Hello World!
```
let helloWorld => print "Hello World!"
helloWorld()
```



