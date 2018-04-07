# Empty Sets of Characters
java -cp lib dcprototype.Main -diff -sim -type testTYPE/SET/SET_CHAR.TYPE \{\} \{\}
# source set is empty and target set is non-empty
java -cp lib dcprototype.Main -diff -sim -type testTYPE/SET/SET_CHAR.TYPE \{\} \{\'a\'\}
# source set is empty and target set is non-empty (target set contains more elements rather than one)
java -cp lib dcprototype.Main -diff -sim -type testTYPE/SET/SET_CHAR.TYPE \{\} \{\'a\',\'c\',\'d\'\}
# source set is non-empty and target set is empty
java -cp lib dcprototype.Main -diff -sim -type testTYPE/SET/SET_CHAR.TYPE \{\'a\',\'b\',\'d\',\'e\'\} \{\}
# Set of Characters
java -cp lib dcprototype.Main -diff -sim -type testTYPE/SET/SET_CHAR.TYPE -source testVALUE/Set/set_char_1.VALUE -target testVALUE/Set/set_char_2.VALUE 

