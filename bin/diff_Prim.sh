# PrimUnit
java -cp lib dcprototype.Main -diff -sim Unit unit unit
java -cp lib dcprototype.Main -diff -sim -type testTYPE/PRIM/PRIMUNIT.TYPE -source testVALUE/Prim/unit.VALUE -target testVALUE/Prim/unit.VALUE
# PrimBool
java -cp lib dcprototype.Main -diff -sim Bool true true
java -cp lib dcprototype.Main -diff -sim Bool true false 
java -cp lib dcprototype.Main -diff -sim Bool false true
java -cp lib dcprototype.Main -diff -sim Bool false false
java -cp lib dcprototype.Main -diff -sim -type testTYPE/PRIM/PRIMBOOL.TYPE -source testVALUE/Prim/bool_true.VALUE -target testVALUE/Prim/bool_false.VALUE
# PrimInt
java -cp lib dcprototype.Main -diff -sim Int -19 -19
java -cp lib dcprototype.Main -diff -sim -type testTYPE/PRIM/PRIMINT.TYPE -source testVALUE/Prim/int1.VALUE -target testVALUE/Prim/int2.VALUE
# PrimNat
java -cp lib dcprototype.Main -diff -sim Nat 19 19
java -cp lib dcprototype.Main -diff -sim -type testTYPE/PRIM/PRIMNAT.TYPE -source testVALUE/Prim/nat1.VALUE -target testVALUE/Prim/nat2.VALUE
# PrimReal
java -cp lib dcprototype.Main -diff -sim Real 300 300.0
java -cp lib dcprototype.Main -diff -sim -type testTYPE/PRIM/PRIMREAL_02.TYPE -source testVALUE/Prim/real_02_1.VALUE -target testVALUE/Prim/real_02_2.VALUE
java -cp lib dcprototype.Main -diff -sim -type testTYPE/PRIM/PRIMREAL_05.TYPE -source testVALUE/Prim/real_05_1.VALUE -target testVALUE/Prim/real_05_2.VALUE
java -cp lib dcprototype.Main -diff -sim -type testTYPE/PRIM/PRIMREAL_05.TYPE -source testVALUE/Prim/real_05_1.VALUE -target testVALUE/Prim/real_05_3.VALUE
java -cp lib dcprototype.Main -diff -sim -type testTYPE/PRIM/PRIMREAL_10.TYPE -source testVALUE/Prim/real_10_1.VALUE -target testVALUE/Prim/real_10_2.VALUE
java -cp lib dcprototype.Main -diff -sim -type testTYPE/PRIM/PRIMREAL_10.TYPE -source testVALUE/Prim/real_10_1.VALUE -target testVALUE/Prim/real_10_3.VALUE
# PrimChar
java -cp lib dcprototype.Main -diff -sim Char \'s\' \'s\'
java -cp lib dcprototype.Main -diff -sim Char \'c\' \'?\'
java -cp lib dcprototype.Main -diff -sim -type testTYPE/PRIM/PRIMCHAR.TYPE -source testVALUE/Prim/char1.VALUE -target testVALUE/Prim/char2.VALUE
