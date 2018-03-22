java -cp lib dcprototype.Main -diff -sim -type testTYPE/LIST_STRING.TYPE -source testVALUE/List/list_string_1.VALUE -target testVALUE/List/list_string_2.VALUE 

java -cp lib diff.ListDiff -diff -sim -type testTYPE/LIST_STRING.TYPE -source testVALUE/List/list_string_1.VALUE -target testVALUE/List/list_string_2.VALUE -html testHTMLres/test_ListDiff_12.html

java -cp lib diff.ListStringDiff -diff -sim -source testVALUE/List/list_string_1_source.VALUE -target testVALUE/List/list_string_2_target.VALUE -html testHTMLres/test_ListStringDiff_12.html

java -cp lib diff.ListStringDiff -diff -sim -source testVALUE/lists_strings/source14.java -target testVALUE/lists_strings/target14.java -html testHTMLres/test_ListStringDiff_src14.html
