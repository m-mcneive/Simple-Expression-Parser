package edu.luc.cs.laufer.cs371.expressions

import scala.collection.mutable.Map as MMap
import edu.luc.cs.laufer.cs371.expressions.CombinatorParser.given_CanEqual_None_type_Option

import scala.util.*
import ast.Expr
import Expr.*
import cats.implicits.*

import scala.collection.mutable
import scala.util.{Failure, Success, Try}

object behaviors {

  enum Value:
    case Num(value: Int) extends Value
    case Ins(value: Instance) extends Value
  import Value.*

  type Store = Instance

  type Result = Try[Value]
  type Instance = MMap[String, Value]

  var memory: Store = MMap.empty[String, Value]
  var testMemory: Store = MMap.empty[String, Value]


  def getMemory(expr: Expr): Store  = {  //For testing
    evaluate(expr)
    return memory
  }

  def resetMemory() = {
    memory = MMap.empty[String, Value]
  }

  def evaluate(e: Expr): Result = {
    resetMemory()
    evaluate(memory)(e)
  }

  def evaluate(store: Store)(e: Expr): Result = e match {
    case Constant(c) => Success(Num(c))

    case UMinus(r)   => Try(evaluate(store)(r).get)

    case Plus(l, r)  => for
      Num(l) <- evaluate(store)(l)
      Num(r) <- evaluate(store)(r)
    yield Num(l + r )

    case Minus(l, r)  => for
      Num(l) <- evaluate(store)(l)
      Num(r) <- evaluate(store)(r)
    yield Num(l - r )

    case Times(l, r)  => for
      Num(l) <- evaluate(store)(l)
      Num(r) <- evaluate(store)(r)
    yield Num(l * r )

    case Div(l, r)  => for
      Num(l) <- evaluate(store)(l)
      Num(r) <- evaluate(store)(r)
    yield Num(l / r )

    case Mod(l, r)  => for
      Num(l) <- evaluate(store)(l)
      Num(r) <- evaluate(store)(r)
    yield Num(l % r)

    case Variable(name) => Try(store(name))

    case Assignment(left: Select, right) =>
      var current = MMap[String, Value]()
      current = store
      val names = left.name
      for a <- names.init do { // Navigating through memory to rightmost variable in select
        current = current(a.name).asInstanceOf[Value.Ins].value
      }
      current.update(names.last.name, evaluate(store)(right).get) // Updating the map (struct) that was navigated to
      Success(Num(0))

    case Block(expressions @_*) => 
      expressions.traverse(s => evaluate(store)(s)).map(l => Try(l.last).getOrElse(Num(0)))

    case Select(names @_*) =>
      var current = MMap[String, Value]()
      current = store
      for name <- names.init do { // Navigating through memory to rightmost variable in select
        current = current(name.name).asInstanceOf[Value.Ins].value
      }
      Try(current(names.last.name)) // Getting value of last element

    case Conditional(cond, thenbranch, elsebranch) => 
      evaluate(store)(cond) match {
        case Success(Num(0)) => evaluate(store)(elsebranch)
        case Success(_) => evaluate(store)(thenbranch)
        case f @ Failure(_)     => f
      }
    
    case Loop(cond, body) =>
      while (true) do {
        evaluate(store)(cond) match {
          case Success(Num(0)) => return Success(Num(0))
          case Success(_) => evaluate(store)(body)
          case f @ Failure(_) => return f
        }
      }
      Success(Num(0))

    case Struct(m) =>
      // Creating empty mutable map to make the result mutable
      val res : Instance = MMap[String, Value]()
      // Mapping tuples of variables and expressions to a new map with the variables' names and the evaluated expressions
      res ++= m.map(f => (f._1.name, evaluate(store)(f._2).get))
      Try(Value.Ins(res))
  }


  def size(e: Expr): Int = e match {
    case Constant(c)            => 1
    case UMinus(r)              => 1 + size(r)
    case Plus(l, r)             => 1 + size(l) + size(r)
    case Minus(l, r)            => 1 + size(l) + size(r)
    case Times(l, r)            => 1 + size(l) + size(r)
    case Div(l, r)              => 1 + size(l) + size(r)
    case Mod(l, r)              => 1 + size(l) + size(r)
    case Block()                => 2
    case Block(es@_*)           => 1 + (es.map(size)).sum
    case Loop(l, r)             => 1 + size(l) + size(r)
    case Conditional(l, r, n)   => 1 + size(l) + size(r) + size(n)
    case Assignment(i,e)        => 1 + size(i) + size(e)
    case Variable(v)            => 1
    case Struct(es)          => 1 + es.map(x => size(x(0)) + size(x(1))).sum
    case Select(is@_*)             => 1 + (is.map(size)).sum
                                      
                                    
  }

  def height(e: Expr): Int = e match {
    case Constant(c)          => 1
    case UMinus(r)            => 1 + height(r)
    case Plus(l, r)           => 1 + math.max(height(l), height(r))
    case Minus(l, r)          => 1 + math.max(height(l), height(r))
    case Times(l, r)          => 1 + math.max(height(l), height(r))
    case Div(l, r)            => 1 + math.max(height(l), height(r))
    case Mod(l, r)            => 1 + math.max(height(l), height(r))
    case Block()              => 1
    case Block(es@_*)         => 1 + (es.map(height)).max
    case Loop(l, r)           => 1 + math.max(height(l), height(r))
    case Assignment(i,e)      => 1 + math.max(height(i), height(e))
    case Conditional(l, r, n) => 1 + math.max(height(l), math.max(height(r), height(n)))
    case Variable(v)          => 1
    case Struct(es)            => 1 + (es.map(x => height(x(1)))).max
    case Select(is@_*)           => 1 + (is.map(height)).max
  }

  def toFormattedString(prefix: String)(e: Expr): String = e match {
    case Constant(c)        => prefix + c.toString
    case UMinus(r)          => buildUnaryExprString(prefix, "UMinus", toFormattedString(prefix + INDENT)(r))
    case Plus(l, r)         => buildExprString(prefix, "Plus", toFormattedString(prefix + INDENT)(l), toFormattedString(prefix + INDENT)(r))
    case Minus(l, r)        => buildExprString(prefix, "Minus", toFormattedString(prefix + INDENT)(l), toFormattedString(prefix + INDENT)(r))
    case Times(l, r)        => buildExprString(prefix, "Times", toFormattedString(prefix + INDENT)(l), toFormattedString(prefix + INDENT)(r))
    case Div(l, r)          => buildExprString(prefix, "Div", toFormattedString(prefix + INDENT)(l), toFormattedString(prefix + INDENT)(r))
    case Mod(l, r)          => buildExprString(prefix, "Mod", toFormattedString(prefix + INDENT)(l), toFormattedString(prefix + INDENT)(r))

    case Block(es@_*)       => buildBlock(prefix, "Block", es)
    case Loop(c,b)          => buildExprString(prefix, "Loop", toFormattedString(prefix + INDENT)(c), toFormattedString(prefix + INDENT)(b))
    case Conditional(c,i,e) => buildCondString(prefix, "Cond", toFormattedString(prefix + INDENT)(c), toFormattedString(prefix + INDENT)(i), toFormattedString(prefix + INDENT)(e))
    case Assignment(i,e)    => buildExprString(prefix, "Assign", toFormattedString(prefix + INDENT)(i), toFormattedString(prefix + INDENT)(e))
    case Variable(c)        => prefix + c.toString

    case Struct(fs)      => buildStruct(prefix, "Struct", fs)
    case Select(es@_*)      => buildBlock(prefix, "Select", es)

  }

  def toFormattedString(e: Expr): String = toFormattedString("")(e)

  def buildSelect(prefix: String, nodeString: String, fs: List[String]): String = {
    val result = new StringBuilder(prefix)
    result.append(nodeString)
    result.append("(")
    for exprString <- fs do {
      result.append(EOL)
      result.append(toFormattedString(prefix + INDENT)(Variable(exprString)) + ",")
    }
    result.setLength(result.length() - 1);
    if fs.size == 0 then result.append(EOL)
    result.append(")")
    result.toString
  }


  def buildStruct(prefix: String, nodeString: String, fs: List[(String, Expr)]): String = {
    val result = new StringBuilder(prefix)
    result.append(nodeString)
    result.append("(")
    for exprString <- fs do {
      result.append(EOL)
      result.append(toFormattedString(prefix + INDENT)(Variable(exprString(0))))
      result.append(EOL)
      result.append(toFormattedString(prefix + INDENT)(exprString(1)) + ",")
    }
    result.setLength(result.length() - 1);
    if fs.size == 0 then result.append(EOL)
    result.append(")")
    result.toString
  }

  def buildAssign(prefix: String, nodeString: String, is: List[Expr], e: Expr): String = {
    val result = new StringBuilder(prefix)
    var x = is
    var p = prefix.toString
  
    while x.length > 0 do {
      result.append(p + nodeString + "(" + EOL)
      p = p + INDENT
      result.append(toFormattedString(p)(x(0)) + "," + EOL)
      x = x.drop(1)
    }
    result.append(toFormattedString(p)(e) + (')'.toString * is.length))
    
    result.toString
  }

  def buildBlock(prefix: String, nodeString: String, es: Seq[Expr]) = {
    val result = new StringBuilder(prefix)
    result.append(nodeString)
    result.append("(")
    
    for exprString <- es do {
      result.append(EOL)
      result.append(toFormattedString(prefix + INDENT)(exprString))
      }
    if es.size == 0 then result.append(EOL)
    result.append(")")
    result.toString
  }

  def buildStruct(prefix: String, nodeString: String, fs: Map[Variable, Expr]): String = {
    val result = new StringBuilder(prefix)
    result.append(nodeString)
    result.append("(")
    for exprString <- fs do {
      result.append(EOL)
      result.append(toFormattedString(prefix + INDENT)(exprString._1))
      result.append(EOL)
      result.append(toFormattedString(prefix + INDENT)(exprString._2) + ",")
    }
    result.setLength(result.length() - 1);
    if fs.size == 0 then result.append(EOL)
    result.append(")")
    result.toString
  }

  def buildExprString(prefix: String, nodeString: String, leftString: String, rightString: String) = {
    val result = new StringBuilder(prefix)
    result.append(nodeString)
    result.append("(")
    result.append(EOL)
    result.append(leftString)
    result.append(", ")
    result.append(EOL)
    result.append(rightString)
    result.append(")")
    result.toString
  }

  def buildCondString(prefix: String, nodeString: String, leftString: String, midString: String, rightString: String) = {
    val result = new StringBuilder(prefix)
    result.append(nodeString)
    result.append("(")
    result.append(EOL)
    result.append(leftString)
    result.append(", ")
    result.append(EOL)
    result.append(midString)
    result.append(", ")
    result.append(EOL)
    result.append(rightString)
    result.append(")")
    result.toString
  }

  def buildUnaryExprString(prefix: String, nodeString: String, exprString: String) = {
    val result = new StringBuilder(prefix)
    result.append(nodeString)
    result.append("(")
    result.append(EOL)
    result.append(exprString)
    result.append(")")
    result.toString
  }



  val EOL = scala.util.Properties.lineSeparator
  val INDENT = ".."



//-----------------------------Pretty Printer--------------------------------//

  def prettyPrinter(prefix: String)(e: Expr): String = e match {
    case Constant(c)        => c.toString
    case UMinus(c)          => c.toString
    case Plus(l,r)          => buildPrettyExpr(prefix, " + ", prettyPrinter("")(l), prettyPrinter("")(r))
    case Minus(l,r)         => buildPrettyExpr(prefix, " - ", prettyPrinter("")(l), prettyPrinter("")(r))
    case Times(l,r)         => buildPrettyExpr(prefix, " * ", prettyPrinter("")(l), prettyPrinter("")(r))
    case Div(l,r)           => buildPrettyExpr(prefix, " / ", prettyPrinter("")(l), prettyPrinter("")(r))
    case Mod(l,r)           => buildPrettyExpr(prefix, " % ", prettyPrinter("")(l), prettyPrinter("")(r))

    case Block(es@_*)       => buildPrettyBlock(prefix, "Block", es)
    case Loop(c,b)          => buildPrettyLoop(prefix, "Loop", prettyPrinter(prefix)(c), prettyPrinter(prefix )(b))
    case Assignment(l,r)    => buildPrettyAssign(prefix, " = ", l, prettyPrinter(prefix)(r))
    case Variable(c)        => c.toString
    case Conditional(c,i,e) => buildPrettyCond(prefix, "Cond", prettyPrinter(prefix)(c), prettyPrinter(prefix )(i), prettyPrinter(prefix)(e))
    case Struct(fs)         => buildPrettyStruct(prefix, "Struct", fs)
    case Select(es@_*)      => buildPrettySelect(es)

  }

  def prettyPrinter(e: Expr): String = prettyPrinter("")(e)

  def buildPrettySelect(is: Seq[Variable]): String = {
    val result = new StringBuilder("")
    for i <- is do {
      result.append(prettyPrinter("")(i) + ".")
    }
    result.setLength(result.length() - 1);
    result.toString
  }

  def buildPrettyAssign(prefix: String, nodeString: String, leftString: Select, rightString: String) = {
    val result = new StringBuilder(prefix)
    result.append(prettyPrinter(prefix)(leftString))
    result.append(" = " + rightString)
    result.toString

  }

  def buildPrettyBlock(prefix: String, nodeString: String, es: Seq[Expr]) = {
    val result = new StringBuilder(prefix)
    result.append("{")
    result.append(EOL)
    for exprString <- es do {
      result.append(prettyPrinter(prefix + PRETTYINDENT)(exprString))
      result.append(EOL)
      }
    result.append(prefix + "}")
    result.toString
  }

  def buildPrettyStruct(prefix: String, nodeString: String, es: Map[Variable, Expr]) = {
    val result = new StringBuilder("")
    result.append("{")
    for exprString <- es do {
      result.append(" " + prettyPrinter("")(exprString(0)) + ": " + prettyPrinter("")(exprString(1)) + ",")
    }
    result.setLength(result.length() - 1);
    result.append("}")
    result.toString
  }

  def buildPrettyExpr(prefix: String, nodeString: String, leftString: String, rightString: String) = {
    val result = new StringBuilder(prefix)
    result.append(leftString)
    result.append(nodeString)
    result.append(rightString)
    if (nodeString == " = ") then result.append(";")
    result.toString
  }


  def buildPrettyLoop(prefix: String, nodeString: String, leftString: String, rightString: String) = {
    val result = new StringBuilder(prefix)
    result.append("while (" + leftString + ")")
    result.append(rightString)
    result.toString
  }

  def buildPrettyCond(prefix: String, nodeString: String, leftString: String, midString: String, rightString: String) = {
    val result = new StringBuilder(prefix)
    result.append("if (" + leftString + ")")
    result.append(midString)
    result.append(" else ")
    result.append(rightString)
    result.toString
  }

  val PRETTYINDENT = "  "
}
