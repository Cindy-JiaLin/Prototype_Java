# List of unit values
java -cp lib dcprototype.Main -diff -sim -type testTYPE/LIST/LIST_UNIT.TYPE -source testVALUE/List/list_unit_1.VALUE -target testVALUE/List/list_unit_2.VALUE
# List of boolean values
java -cp lib dcprototype.Main -diff -sim -type testTYPE/LIST/LIST_BOOL.TYPE -source testVALUE/List/list_bool_1.VALUE -target testVALUE/List/list_bool_2.VALUE
# List of nat values (natural numbers)
java -cp lib dcprototype.Main -diff -sim -type testTYPE/LIST/LIST_NAT.TYPE -source testVALUE/List/list_nat_1.VALUE -target testVALUE/List/list_nat_2.VALUE
# List of characters
java -cp lib dcprototype.Main -diff -sim -type testTYPE/LIST/LIST_CHAR.TYPE -source testVALUE/List/list_char_1.VALUE -target testVALUE/List/list_char_2.VALUE
# List of Strings
java -cp lib dcprototype.Main -diff -sim -type testTYPE/LIST/LIST_STRING.TYPE -source testVALUE/List/list_string_1.VALUE -target testVALUE/List/list_string_2.VALUE 

java -cp lib diff.ListDiff -diff -sim -type testTYPE/LIST/LIST_STRING.TYPE -source testVALUE/List/list_string_1.VALUE -target testVALUE/List/list_string_2.VALUE -html testHTMLres/test_ListDiff_12.html

