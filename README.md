# A simple expression evaluator in Scala. 

This is for a class on programming languages I took in college. The goal of this project was to create a parser that could evaluate simple 
mathematic espressions. After inputting an expression, hit ```Enter``` twice to run. See examples below for proper syntax.

Includes two types of parsers:

- [Scala parser combinators](http://www.scala-lang.org/api/current/scala-parser-combinators/#scala.util.parsing.combinator.Parsers)
- [parboiled2 parsing expression grammars](https://github.com/sirthias/parboiled2)

To run the tests:

      sbt test

To run either of the calculator examples:

      sbt run

To run either of the test examples:

      sbt test:run

### Example inputs
```{ x = 3; x; }```
Output:
```
You entered: { x = 3; x; }
Memory: HashMap()
The parsed expression is: 
Block(
..Assign(
....Select(
......x), 
....3)
..x)
The unparsed expression is: 
{
  x = 3
x
}
It has size 6 and height 4
It evaluates to Success(Num(3))
Memory: HashMap(x -> Num(3))
```
This code is saving 3 to the variable x then printing it out. The variable is not saved and can be used further:

``` { if(x % 2){r = x + x;} else {r = x * x;} r; } ```
Output:
```
You entered: { if(x % 2){r = x + x;} else {r = x * x;} r; }
Memory: HashMap(x -> Num(3))
The parsed expression is: 
Block(
..Cond(
....Mod(
......x, 
......2), 
....Block(
......Assign(
........Select(
..........r), 
........Plus(
..........x, 
..........x))), 
....Block(
......Assign(
........Select(
..........r), 
........Times(
..........x, 
..........x))))
..r)
The unparsed expression is: 
{
  if (  x % 2)  {
    r =     x + x
  } else   {
    r =     x * x
  }
r
}
It has size 20 and height 6
It evaluates to Success(Num(6))
Memory: HashMap(r -> Num(6), x -> Num(3))
```
In this case, it is important that ```1 = true``` so when doing x % 2, the output causes the if portion to execute.
