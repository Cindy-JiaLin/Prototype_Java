#java -cp lib dcprototype.Main -diff -sim -type testTYPE/JSON/JSONSample.TYPE -source testVALUE/JSON/JSONSample_1.VALUE -target testVALUE/JSON/JSONSample_2.VALUE
 
java -cp lib dcprototype.Main -diff -sim -type testTYPE/JSON/JSON_Example.TYPE -source testVALUE/JSON/JSON_Example_1.VALUE -target testVALUE/JSON/JSON_Example_2.VALUE

java -cp lib diff.ProductDiff -diff -sim -type -html testHTMLres/JSONres.html testTYPE/JSON/JSON_Example.TYPE -source testVALUE/JSON/JSON_Example_1.VALUE -target testVALUE/JSON/JSON_Example_2.VALUE 

