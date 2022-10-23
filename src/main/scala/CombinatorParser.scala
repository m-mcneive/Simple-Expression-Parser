package edu.luc.cs.laufer.cs371.expressions

import scala.util.parsing.combinator.JavaTokenParsers
import ast.Expr, Expr.*

object CombinatorParser extends JavaTokenParsers {

  /**
   * Enable missing typesafe equality between `None` and `Option`.
   * TODO remove once the library provides this.
   */
  given CanEqual[None.type, Option[_]] = CanEqual.derived

  // Struct:
  // use repsep()
  // Struct evaluates to a map of the evaluated expressions in the struct
  // Seq.init (gives seq without last element - use to traverse back up tree structure)

  //override val ident: Parser[String] = """[a-zA-Z] [a-zA-Z0-9]*"""

  /** expr ::= term { { "+" | "-" } term }* */
  def expr: Parser[Expr] =
    term ~ rep(("+" | "-") ~ term) ^^ {
      case l ~ ts => ts.foldLeft( l ) {
        case (r, "+" ~ t) => Plus(r, t)
        case (r, "-" ~ t) => Minus(r, t)
        case _ => l
      }
    }

  /** term ::= factor { { "*" | "/" | "%" } factor }* */
  def term: Parser[Expr] =
    factor ~ rep(("*" | "/" | "%") ~ factor) ^^ {
      case l ~ ts => ts.foldLeft( l ) {
        case (r, "*" ~ t) => Times(r, t)
        case (r, "/" ~ t) => Div(r, t)
        case (r, "%" ~ t) => Mod(r, t)
        case _ => l
      }
    }

  //factor ::= ident | wholeNumber | "+" factor | "-" factor | "(" expr ")"
  def factor: Parser[Expr] = (
    wholeNumber ^^ { case s => Constant(s.toInt) }
    | "+" ~> factor ^^ { case e => e }
    | "-" ~> factor ^^ { case e => UMinus(e) }
    | "(" ~ expr ~ ")" ^^ { case _ ~ e ~ _ => e }
    | ident ~ rep("." ~ ident) ^^ {
      case s =>
        if s._2.length > 0 then {
          Select((Variable(s._1) :: s._2.map(a => Variable(a._2))).asInstanceOf[Seq[Variable]]:_*)
        } else {
          Variable(s._1)
        }
    }
    | struct ^^ {case s => s}
  )
    //block ::= "{" statement* "}"
  def block: Parser[Expr] = 
    "{" ~ rep(statement) ~ "}" ^^ {
      case _ ~ es ~ _ => Block(es:_*)
    }
    //loop ::= "while" "(" expression ")" block
  def loop: Parser[Expr] =
    "while" ~ "(" ~ expr ~ ")" ~ block ^^ {
      case _ ~ _ ~ e ~ _ ~ b => Loop(e, b)
    }

  def conditional: Parser[Expr] = 
    "if" ~ "(" ~ expr ~ ")" ~ block ~! opt("else" ~ block) ^^ {
      case _~_~c~_~i~ None => Conditional (c,i,Block()) 
      case _~_~c~_~i~ Some(_ ~ e) => Conditional (c,i,e)
    }

  def assignment: Parser[Expr] = 
    ident ~ rep("." ~ ident) ~ "=" ~ expr ~ opt(";") ^^ {
      case i ~ is ~ _ ~ e ~ _ =>
        // Checking if there was anything returned from the rep
        if is.length > 0 then {
          // Map the list of patterns to a list of variables that describes the path to traverse in memory
          val vars = (Variable(i) :: is.map(a => Variable(a._2))).asInstanceOf[Seq[Variable]]
          Assignment(Select(vars:_*), e)
        } else {
          Assignment(Select(Variable(i)),e)
        }
    }

  // def struct: Parser[Expr] =
  //   "{" ~ repsep(field, ",") ~ "}" ^^ {case _ ~ fs ~ _ => Struct(fs)}
  
  def statement: Parser[Expr] =
    assignment | loop | conditional | (expr ~ opt(";") ^^ {case e ~ _ => e}) | block | struct

  def struct: Parser[Expr] =
    "{" ~ repsep(ident ~ ":" ~ expr, ",") ~ "}" ^^ {
      case _~s~_ =>
        // Splitting up the list of patterns into a list of names and expressions
        // Definitely a better way to do this, but it works
        val names = s.map(a => a match {
          case name ~ _ ~ value => name
        })
        val values = s.map(a => a match {
          case name ~ _ ~ value => value
        })
        // Creating a map with the above variable names and expressions
        val entries = names.zip(values).map(a => (Variable(a._1) -> a._2))
        Struct(entries.toMap[Variable, Expr])
    }
}
