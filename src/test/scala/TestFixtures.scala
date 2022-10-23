package edu.luc.cs.laufer.cs371.expressions

import edu.luc.cs.laufer.cs371.expressions.ast.Expr

object TestFixtures {

  import ast.Expr.*

  val e1 = //{x = 2; x;}
    Block(
      Assignment(
        Select(Variable("x")),
        Constant(2)
      ),
      Variable("x")
    )

  //{x = 2; y = 3; r = 0; while(y){r = r + x; y = y-1;} r;}
  val e2 = 
    Block(
      Assignment(
        Select(Variable("x")),
        Constant(2)),
      Assignment(
        Select(Variable("y")),
        Constant(3)),
      Assignment(
        Select(Variable("r")),
        Constant(0)),
      Loop(
        Variable("y"), 
        Block(
          Assignment(
            Select(Variable("r")),
            Plus(
              Variable("r"), 
              Variable("x"))),
          Assignment(
            Select(Variable("y")),
            Minus(
              Variable("y"), 
              Constant(1))))),
        Variable("r"))

  //{x = 4; if(x % 2){r = x + x;} else {r = x * x;} r;}
  val e3 = 
    Block(
      Assignment(
        Select(Variable("x")),
        Constant(4)
      ),
      Conditional(
        Mod(
          Variable("x"),
          Constant(2)
        ),
        Assignment(
          Select(Variable("r")),
          Plus(
            Variable("x"),
            Variable("x")
          )
        ),
        Assignment(
          Select(Variable("r")),
          Times(
            Variable("x"),
            Variable("x")
          )
        )
      ),
      Variable("r")
    )

  //{y = 0; if(y){x = 1;} x;}
  val e4 = 
    Block(
      Assignment(
        Select(Variable("y")),
        Constant(0)
      ),
      Conditional(
        Variable("y"),
        Block(
          Assignment(
            Select(Variable("x")),
            Constant(1)
          )
        ),
        Block()
      ),
      Variable("x")
    )
  
  //{y;}
  val e5 = 
  Block(
    Variable("y")
  )




  val complex1 =
    Div(
      Minus(
        Plus(
          Constant(1),
          Constant(2)
        ),
        Times(
          Constant(3),
          Constant(4)
        )
      ),
      Constant(5)
    )

  val complex1string = "((1 + 2) - (3 * 4)) / 5;"

  val complex1string2 = "  ((1 + 2) - (3 * 4)) / 5;  "

  val complex2 =
    Mod(
      Minus(
        Plus(
          Constant(1),
          Constant(2)
        ),
        Times(
          UMinus(
            Constant(3)
          ),
          Constant(4)
        )
      ),
      Constant(5)
    )


  val loop1String = "while(1 + -1){num = 100;}"
  val loop1 = 
    Loop(
      Plus(
        Constant(1),
        Constant(-1) // Changed to avoid infinite loop
      ), 
      Block(
        Assignment(
          Select(Variable("num")),
          Constant(100)
        )
      )
    )
    

  val cond1String = "if(2 % 1){num = 1;} else {num = 2;}"
  val cond1 = 
    Conditional(
        Mod(
            Constant(2),
            Constant(1)
        ),
        Block(
          Assignment(
            Select(Variable("num")),
            Constant(1)
          )
        ),
        Block(
          Assignment(
            Select(Variable("num")),
            Constant(2)
          )
        )
    )

  val cond2String = "if(2 % 1){num = 1;}"
  val cond2 = 
    Conditional(
        Mod(
            Constant(2),
            Constant(1)
        ),
        Block(
          Assignment(
            Select(Variable("num")),
            Constant(1)
          )
        ),
        Block(
        )
    )

  val assign1String = "value = 10;"
  val assign1 = 
    Assignment(
      Select(Variable("value")),
      Constant(10)
    )

  val struct1String = "{a : 2 + 5, b : 23}"
  val struct1 =
    Struct(
      Map[Variable, Expr](
        (Variable("a"),
          Plus(
            Constant(2),
            Constant(5)
          )),
        (Variable("b"),
          Constant(23))
      )
    )

  val nestedStructString = "{a : 3, b : {c : 3 * 2}}"
  val nestedStruct =
    Struct(
      Map[Variable, Expr](
        (Variable("a"),
          Constant(3)),
        (Variable("b"),
          Struct(
            Map[Variable, Expr](
              (Variable("c"),
                Times(
                  Constant(3),
                  Constant(2)
                ))
            )
          ))
      )
    )

  val assignStructString = "x = {a : 4}"
  val assignStruct =
    Assignment(
      Select(Variable("x")),
      Struct(
        Map[Variable, Expr](
          (Variable("a"),
          Constant(4))
        )
      )
    )

  val selectString = "{x = {a : 2, b : 3}; x.b;}"
  val select =
    Block(
      Assignment(
        Select(Variable("x")),
        Struct(
          Map[Variable, Expr](
            (Variable("a"),
              Constant(2)),
            (Variable("b"),
              Constant(3))
          )
        )
      ),
      Select(Variable("x"), Variable("b"))
    )


  val all1String = "{if(1+2){while(0){val = 2; 1+2/3*4;}} else {while(1){1 + 1;}}}"
  val all1 = 
    Block(
      Conditional(
        Plus(
          Constant(1),
          Constant(2)
        ),
        Block(
          Loop(
            Constant(0),
            Block(
              Assignment(
                Select(Variable("val")),
                Constant(2)
              ), 
              Plus(
                Constant(1), 
                Times(
                  Div(
                    Constant(2), 
                    Constant(3)
                  ), 
                Constant(4))
              ),
            )
          )
        ),
        Block(
          Loop(
            Constant(1),
            Block(
              Plus(
                Constant(1),
                Constant(1)
              )
            )
          )          
        )
      )
    )

}
