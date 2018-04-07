package dcprototype; 

public class Console
{
  private final static int DEL=207;
  private final static int INS=118;
  private final static int CHG=39;
  private final static int CPY=7;

  private static String code(int n) { return " "+(""+(1000+n)).substring(1)+" "; }
  private static String color(int n) { return CSI+"48;5;"+n+";1m"+code(n); }

  public static String del(String s) { return CSI+"48;5;"+DEL+";1m"+s+RESET; }
  public static String ins(String s) { return CSI+"48;5;"+INS+";1m"+s+RESET; }
  public static String chg(String s) { return CSI+"48;5;"+CHG+";1m"+s+RESET; }
  public static String cpy(String s) { return CSI+"48;5;"+CPY+";1m"+s+RESET; }

  private static final String CSI="\u001b[";
  private static final String RESET=CSI+"0m";

  public static void main(String[] argv)
  { // set window title
    // System.out.println(BG_RED+" Deleted "+BG_GREEN+" Inserted "+BG_BLUE+" Changed "+RESET);
    for (int i=0; i<16; i++)
    { for (int j=0; j<16; j++)
        System.out.print(color(16*i+j));
      System.out.println(RESET);
    }
    System.out.println();
    System.out.println(cpy(" copy")+del(" deleted ")+ins("  inserted ")+chg(" changed "));
    System.out.println();
    System.out.println("\'"+del("a")+ins("b")+"'");

  }
}

