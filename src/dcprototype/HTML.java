package dcprototype;

public final class HTML 
{ public static String BODY(String body)// body is a partial solution
  { return "<html>\n"+
      "<style style=\"text/css\">\n"+
      "  html{ font-family:sans-serif}\n"+
      " .line{ text-align: right; font-family: monospace;}\n"+
      " .code{ text-align:  left; font-family: monospace;}\n"+
      " .summary{ text-align: center; font-family: monospace;}\n"+
      " .del{ background-color: #ffcccc;}\n"+
      " .ins{ background-color: #ccffcc;}\n"+
      " .chg{ background-color: #ccccff;}\n"+
      " .cpy{ background-color: #eeeeee;}\n"+
      "</style>\n"+
      "<body>\n"+body+"<body>\n"+
      "</html>\n";
  }
  public static final String DEL="del";
  public static final String INS="ins";
  public static final String CHG="chg";
  public static final String CPY="cpy";

  public static String TABLE(String rows)// rows are traces
  { return "<table>\n"+"<table style='border-collapse;'>\n"+
           rows
           +"</table>\n";
  }        
  private final static String SPACE="&nbsp;";
  
  // tr defines a row in a table
  // td defines a cell in a table
  public static String TR(String row){ return "<tr>"+row+"</tr>\n";}
  
  public static String TD(int cell){ return "<td class=\"line\">"+SPACE+cell+SPACE+"</td>";}
  public static String TD(String cell){ return "<td class=\"code\">"+cell+"</td>";}

  public static String TD(String style, int cell){ return "<td class=\"line "+style+"\">"+SPACE+cell+SPACE+"</td>";}
  public static String TD(String style, String cell){ return "<td class=\"code "+style+"\">"+cell+"</td>";}

  public static String TD2(String style, String cell){ return "<td colspan='2' class=\"summary "+style+"\">"+cell+"</td>";}
 
  // Reserved characters in HTML must be replaced with character entities.
  // Characters that are not present on your keyboard can also be replaced by entities.
  /* Convert some predefined characters to HTML entities
  & (ampersand) becomes &amp;
  " (double quote) becomes &quot;
  ' (single quote) becomes &#039;
  < (less than) becomes &lt;
  > (greater than) becomes &gt;
  */ 
  public static String encode(char c)
  { if(c=='<') return "&lt;";
    else if(c=='>') return "&gt;";
    else if(c=='&') return "&amp;";
    else if(c==' ') return "&nbsp;";
    else if(c=='~') return "&asymp;";
    else return ""+c;
  }
  
  public static String encode(String s)
  { StringBuffer buf = new StringBuffer();
    for(int i=0;i<s.length(); i++)
      buf.append(encode(s.charAt(i)));
    return buf.toString();
  }  
  
  public static String DEL(char c){ return "<span style=\"background-color:#ffcccc;\">"+encode(c)+"</span>";}
  public static String INS(char c){ return "<span style=\"background-color:#ccffcc;\">"+encode(c)+"</span>";}
  public static String CHG(char c){ return "<span style=\"background-color:#ccccff;\">"+encode(c)+"</span>";}
  public static String CPY(char c){ return "<span stype=\"background-color:#eeeeee;\">"+encode(c)+"</span>";}

  public static String DEL(String s){ return "<span style=\"background-color:#ffcccc;\">"+encode(s)+"</span>";}
  public static String INS(String s){ return "<span style=\"background-color:#ccffcc;\">"+encode(s)+"</span>";}
  public static String CHG(String s){ return "<span style=\"background-color:#ccccff;\">"+encode(s)+"</span>";}
  public static String CPY(String s){ return "<span stype=\"background-color:#eeeeee;\">"+encode(s)+"</span>";}


 }
