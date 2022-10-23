package edu.luc.cs.laufer.cs371.expressions.ast

/** An initial algebra of arithmetic expressions. */
enum Expr derives CanEqual:
  case Constant(value: Int)
  case UMinus(expr: Expr)
  case Plus(left: Expr, right: Expr)
  case Minus(left: Expr, right: Expr)
  case Times(left: Expr, right: Expr)
  case Div(left: Expr, right: Expr)
  case Mod(left: Expr, right: Expr)

  case Block(expr: Expr*) //Main body, any number of expressions
  case Loop(cond: Expr, block: Expr) //First is loop condition, second is loop body
  case Conditional(cond: Expr, i: Expr, e: Expr) //condition for loop, then if/else
  case Assignment(name: Select, value: Expr) //name is Expr but always a variable, value is a constant
  case Variable(name: String)  //not sure
  case Struct(s: Map[Variable, Expr])
  case Select(name: Variable*)

