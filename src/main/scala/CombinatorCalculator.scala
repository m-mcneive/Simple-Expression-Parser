package edu.luc.cs.laufer.cs371.expressions
import scala.collection.mutable.{Map as MMap}

object CombinatorCalculator {

  def processExpr(input: String): Unit = {
    println("You entered: " + input)
    //TODO change CombinatorParser.expr to CombinatorParser.top_level
    val result = CombinatorParser.parseAll(CombinatorParser.statement, input)
    if result.isEmpty then {
      println("This expression could not be parsed")
    } else {
      import behaviors.*
      // behaviors.resetMemory()
      val expr = result.get
      println("Memory: " + behaviors.memory)
      println("The parsed expression is: ")
      println(toFormattedString(expr))
      println("The unparsed expression is: ")
      println(prettyPrinter(expr))
      println("It has size " + size(expr) + " and height " + height(expr))
      println("It evaluates to " + evaluate(behaviors.memory)(expr))
      println("Memory: " + behaviors.memory)
    }
  }

  def main(args: Array[String]): Unit = {
    if args.length > 0 then {
      processExpr(args mkString " ")
    } else {
      print("Enter infix expression: ")
      val s = StringBuilder()
      scala.io.Source.stdin.getLines() foreach { line =>
        if line != "" then { // Keep concatenating lines until a blank line is entered
          s.append(line)
        } else {
          processExpr(s.toString)
          s.delete(0, s.length)
          print("Enter infix expression: ")
        }
      }
    }
  }
}
