java -cp lib dcprototype.Main -diff -sim STRING \"survey\" \"surgery\"

java -cp lib diff.PrimStringDiff -diff -sim survey surgery


java -cp lib dcprototype.Main -diff -sim -type testTYPE/PRIMSTRING.TYPE -source testVALUE/Prim/string1.VALUE -target testVALUE/Prim/string2.VALUE

java -cp lib dcprototype.Main -diff -sim STRING \"pneumonoultramicroscopicsilicovolcanoconiosis\" \"pneumonoultramycroscopicsiliconvolcanoconioses\"

java -cp lib diff.PrimStringDiff -diff -sim pneumonoultramicroscopicsilicovolcanoconiosis pneumonoultramycroscopicsiliconvolcanoconioses
