package edu.luc.cs.laufer.cs371.expressions

import org.scalatest.funsuite.AnyFunSuite
import TestFixtures.*
import edu.luc.cs.laufer.cs371.expressions.ast.Expr

object MainCombinatorParser {
  def main(args: Array[String]): Unit = {
    val parsedExpr = CombinatorParser.parseAll(CombinatorParser.statement, complex1string)
    println(parsedExpr.get)
    println(complex1)
    println(parsedExpr.get == complex1)
    println(behaviors.evaluate(parsedExpr.get))
  }
}

class TestCombinatorParser extends AnyFunSuite {
  val parsedExpr = CombinatorParser.parseAll(CombinatorParser.statement, complex1string)
  val parsedExpr2 = CombinatorParser.parseAll(CombinatorParser.statement, complex1string2)
  test("parser works 1") { assert(parsedExpr.get == complex1) }
  test("parser works 2") { assert(parsedExpr2.get == complex1) }

  //Test for basic loop
  val parsedLoop1 = CombinatorParser.parseAll(CombinatorParser.statement, loop1String)
  test("parser works: loop 1") { assert(parsedLoop1.get == loop1) }

  //Test for basic conditional
  val parsedCond1 = CombinatorParser.parseAll(CombinatorParser.statement, cond1String)
  test("parser works: conditional 1") { assert(parsedCond1.get == cond1) }

  //Test for basic assignment
  val parsedAssignment1 = CombinatorParser.parseAll(CombinatorParser.statement, assign1String)
  test("parser works: assignment 1") { assert(parsedAssignment1.get == assign1) }

  //Advanced test, nested loops/conditionals with assignment
  val parsedAll1 = CombinatorParser.parseAll(CombinatorParser.statement, all1String)
  test("parser works: nested loops/conditionals with assignment 1") { assert(parsedAll1.get == all1) }
  
  // Test for simple struct
  val parsedStruct = CombinatorParser.parseAll(CombinatorParser.statement, struct1String)
  test("parser works: simple struct") { assert(parsedStruct.get == struct1) }

  // Test for nested struct
  val parsedNestedStruct = CombinatorParser.parseAll(CombinatorParser.statement, nestedStructString)
  test("parser works: nested struct") { assert(parsedNestedStruct.get == nestedStruct) }

  // Test for assigning to struct
  val parsedAssignmentStruct = CombinatorParser.parseAll(CombinatorParser.statement, assignStructString)
  test("parser works: assignment struct") { assert(parsedAssignmentStruct.get == assignStruct) }

  // Test for selecting value from struct
  val parsedSelect = CombinatorParser.parseAll(CombinatorParser.statement, selectString)
  test("parser works: select from struct") { assert(parsedSelect.get == select) }

}
