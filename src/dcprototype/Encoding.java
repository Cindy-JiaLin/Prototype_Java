package dcprototype; 

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class Encoding 
{ 
  public final static String UNIT=new String(Character.toChars(0x1D54C));
  public final static String BOOL=new String(Character.toChars(0x1D539));
  public final static String CHAR="\u2102";
  public final static String STRING=new String(Character.toChars(0x1D54A));
  public final static String NAT="\u2115";
  public final static String INT="\u2124";
  public final static String REAL=new String(Character.toChars(0x211D));
  
  public final static String APPROX = "\u2248";
  public final static String MAPPING = new String(Character.toChars(0x21D2));
  public final static String MAPSTO = new String(Character.toChars(0x21A6));
  public final static String MSETLEFT = "\u3008";
  public final static String MSETRIGHT = "\u3009";
 
  public static void printUnicode(String str) throws UnsupportedEncodingException
  { PrintStream out = new PrintStream(System.out, true, "UTF-8");
    out.println(str);
  }
 
  public static void main (String[] argv) throws UnsupportedEncodingException 
  { String[] unicodeMessage = new String[]{UNIT, BOOL, CHAR, STRING, NAT, INT, REAL, APPROX, MAPPING, MAPSTO, MSETLEFT, MSETRIGHT};

    PrintStream out = new PrintStream(System.out, true, "UTF-8");
    for(int i=0; i<unicodeMessage.length; i++)
      out.println(unicodeMessage[i]);
  }
}

