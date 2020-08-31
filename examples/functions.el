let add-2-ints = (Int a, Int b) => {
    a + b
} //implicit return

let four = add-2-ints 1 3 //parens are optional
let five = add-2-ints(2, 3)

let single-expression-function = (String message) => println message


let is-overage = (Int age) => {
    if age >= 18 => print "you are overage, yay"
    else => print "too young loser"
}

//both styles of function calling are possible
21 is-overage

is-overage 21

let fact = (Int n) => {
    if n == 1 => return 1
    else => return n * fact(n - 1)
}

print fact 5
//prints 5
