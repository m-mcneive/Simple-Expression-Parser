package edu.luc.cs.laufer.cs371.expressions

import org.scalatest.funsuite.AnyFunSuite
import behaviors.*
import Value.*
import ast.*
import TestFixtures.*

import scala.collection.immutable.HashMap
import scala.collection.mutable
import scala.collection.mutable.Map as MMap
import scala.util.{Failure, Success, Try}

object Main {
  def main(args: Array[String]): Unit = {
    println("p = " + complex1)
    println("evaluate(p) = " + evaluate(complex1))
    println("size(p) = " + size(complex1))
    println("height(p) = " + height(complex1))
    println(toFormattedString(complex1))
    println("q = " + complex2)
    println("evaluate(q) = " + evaluate(complex2))
    println("size(q) = " + size(complex2))
    println("height(q) = " + height(complex2))
    println(toFormattedString(complex2))
  }
}

class Test extends AnyFunSuite {
  test("evaluate(p)") { assert(
    evaluate(complex1) match {
      case Success(Num(-1)) => true
      case _ => false
    }
  )}
  test("size(p)") { assert(size(complex1) == 9) }
  test("height(p)") { assert(height(complex1) == 4) }

  test("evaluate(q)") { assert(
    evaluate(complex2) match {
      case Success(_) => true
      case _ => false
    }
  )}
  test("size(q)") { assert(size(complex2) == 10) }
  test("height(q)") { assert(height(complex2) == 5) }

  //Test size and height with new fixtures
  test("size: loop") { assert(size(loop1) == 9) }
  test("height: loop") { assert(height(loop1) == 5) }
  test("size: if/else") { assert(size(cond1) == 14) }
  test("height: if/else") { assert(height(cond1) == 5) }
  test("size: if without else") { assert(size(cond2) == 11) }
  test("height: if without else") { assert(height(cond2) == 5) }
  test("size: assignment") { assert(size(assign1) == 4) }
  test("height: assignment") { assert(height(assign1) == 3) }
  test("size: nested loops/conditionals with assignment") { assert(size(all1) == 27) }
  test("height: nested loops/conditionals with assignment") { assert(height(all1) == 9) }
  test("size: simple struct") { assert(size(struct1) == 7) }
  test("height: simple struct") { assert(height(struct1) == 3) }
  test("size: nested struct") { assert(size(nestedStruct) == 9) }
  test("height: nested struct") { assert(height(nestedStruct) == 4) }
  test("size: select from struct") { assert(size(select) == 12) }
  test("height: select from struct") { assert(height(select) == 4) }

  //Test evaluate
  test("evaluate select from struct") {assert (
    evaluate(select) match {
      case Success(Num(3)) => true
      case _ => false
    }
  )}

  test("evaluate 1") { assert(
    evaluate(e1) match {
      case Success(Num(2)) => true
      case _ => false
    }
  )}
  test("evaluate 2") { assert(
    evaluate(e2) match {
      case Success(Num(6)) => true
      case _ => false
    }
  )}
  test("evaluate 3") { assert(
    evaluate(e3) match {
      case Success(Num(16)) => true
      case _ => false
    }
  )}

  test("evaluate 4 (failure)") { assert(
    evaluate(e4) match {
      case Success(_) => false
      case Failure(_) => true
    }
  )}

  test("evaluate 5 (failure)") { assert(
    evaluate(e5) match {
      case Success(_) => false
      case Failure(_) => true
    }
  )}
}
