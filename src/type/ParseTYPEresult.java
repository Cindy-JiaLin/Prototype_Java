package type;

import java.util.List;
import java.util.ArrayList;

public class ParseTYPEresult 
{ private final TYPE res;
  private final String rest, error;
  private ParseTYPEresult(TYPE res, String rest, String error)
  { this.res=res; this.rest=rest; this.error=error;}
  // Either res == null Or error == null
  public static ParseTYPEresult ok(TYPE res, String rest)
  { return new ParseTYPEresult(res, rest, null);}
  public static ParseTYPEresult error(String rest, String error)
  { return new ParseTYPEresult(null, rest, error);}        
  
  public TYPE getResult() { return this.res;}  
  public String getRest() { return this.rest;}  
  public String getError() { return this.error;}        
       
  public String toString()
  { StringBuilder buf=new StringBuilder();
    if(this.res!=null)
    { buf.append(this.res);
      if(this.rest.length()!=0){ buf.append(", Rest="); buf.append(this.rest);}  
    }
    else { buf.append("Rest="); buf.append(this.rest); buf.append(", ERROR="); buf.append(this.error);}    
    return buf.toString();
  }   
      
  // Cutoff the substring "prefix" from the string "str"
  private static String cutoff(String str, String prefix)
  { str=str.trim();
    if(str.startsWith(prefix)){ return str.substring(prefix.length()).trim();}
    else{ return str;}  
  } 
  // used for parsing accuracy of REAL TYPE
  private static boolean startsWithDouble(String str)
  { try
    { Double.parseDouble(str);return true;
    } catch (NumberFormatException ex){return false;}
  } 

  // for PRODUCT TYPE when any label is not available. 
  private static String NOLABEL = "nolabel";

  public static ParseTYPEresult parseTYPE(List<String> varNames, String str)
  { str=str.trim();
    for(int i=0; i<varNames.size(); i++)
    { String vname = varNames.get(i);
      if(str.startsWith(vname))
        return ok(TYPE.VAR(vname),cutoff(str, vname));
    }
    if(str.startsWith("("))// TYPE in the parentheses has priority 
    { str=cutoff(str,"(");
      ParseTYPEresult t=parseTYPE(varNames, str);
        if(t.getError()!=null) return t;
      str=t.getRest().trim();
        if(!str.startsWith(")")) return error(str, "Expected a ')' at the end");
      str=cutoff(str,")");
      return ok(t.getResult(),str); 
    } 
    if(str.startsWith("Unit"))
    { return ok(TYPE.UNIT, cutoff(str, "Unit"));}    
    else if(str.startsWith("Bool"))
    { return ok(TYPE.BOOL, cutoff(str, "Bool"));} 
    else if(str.startsWith("Char"))
    { return ok(TYPE.CHAR, cutoff(str, "Char"));}
    else if(str.startsWith("String"))
    { return ok(TYPE.STRING, cutoff(str, "String"));} 
    else if(str.startsWith("Nat"))
    { return ok(TYPE.NAT, cutoff(str, "Nat"));} 
    else if(str.startsWith("Int"))
    { return ok(TYPE.INT, cutoff(str, "Int"));}
    /* REAL */
    else if(str.startsWith("Real"))
    { str=cutoff(str, "Real");
        if(!str.startsWith("(")) 
        return ok(TYPE.REAL, cutoff(str, "Real"));// REAL TYPE without acc
      str=cutoff(str,"(");
      int index=str.indexOf(")");
        if(index==-1)throw new RuntimeException("Error: Has no end ')' in this str.");
      String acc=str.substring(0, index).trim();
        if(!startsWithDouble(acc)) return error(str, "Expected a double value as the accuracy.");
      str=cutoff(str, acc);
      str=cutoff(str,")");
      return ok(TYPE.REAL(Double.parseDouble(acc)),str); 
    }   
    /* PRODUCT
    // In PRODUCT TYPE, labels are optional, hence I give a NOLABEL to record
    // the position of any TYPE without label.
    */ 
    else if(str.startsWith("{"))
    { str=cutoff(str, "{");
      List<String> lols = new ArrayList<String>();
      List<TYPE> lots = new ArrayList<TYPE>();
      String label1;
      int starIndex1=str.indexOf("*");
      if(starIndex1==-1)// The size of this PRODUCT TYPE is 1 
      { int curveIndex1 = str.indexOf("}");
        if(curveIndex1==-1) 
          throw new RuntimeException("Expected a correct form of PRODUCT TYPE");
        String strBeforeCurve = str.substring(0, curveIndex1);
        int dotIndex1=strBeforeCurve.indexOf(".");
        if(dotIndex1==-1)// this is a TYPE without label
        { ParseTYPEresult t1 = parseTYPE(varNames, str);
            if(t1.getError()!=null) return t1;
          str=t1.getRest().trim();
          lols.add(NOLABEL);
          lots.add(t1.getResult());
        }
        else// this a label followed by a TYPE
        { label1 = str.substring(0, dotIndex1);
          str=cutoff(str, label1);
          str=cutoff(str, ".");
          ParseTYPEresult t1 = parseTYPE(varNames, str);
            if(t1.getError()!=null) return t1;
          str=t1.getRest().trim();
          lols.add(label1);
          lots.add(t1.getResult());
        }
      }
      else// the size of this PROCUCT >1
      { String strBeforeStar = str.substring(0, starIndex1);
        int dotIndex1 = strBeforeStar.indexOf(".");
        if(dotIndex1==-1)// the first TYPE has no label
        { ParseTYPEresult t1=parseTYPE(varNames, str);
            if(t1.getError()!=null) return t1;
          str=t1.getRest().trim();
          lols.add(NOLABEL);
          lots.add(t1.getResult());
        }
        else// has label
        { label1 = str.substring(0, dotIndex1);
          str=cutoff(str, label1);
          str=cutoff(str, ".");
          ParseTYPEresult t1 = parseTYPE(varNames, str);
            if(t1.getError()!=null) return t1;
          str=t1.getRest().trim();
          lols.add(label1);
          lots.add(t1.getResult());
        }
      }
      for(int i=1; !str.startsWith("}"); i++)
      { str=cutoff(str, "*");
        String label;
        int starIndex=str.indexOf("*");
        if(starIndex==-1)
        { int curveIndex = str.indexOf("}");
          if(curveIndex==-1) 
            throw new RuntimeException("Expected a correct form of PRODUCT TYPE");
          String strBeforeCurve = str.substring(0, curveIndex);
          int dotIndex=strBeforeCurve.indexOf(".");
          if(dotIndex==-1)// this is a TYPE without label
          { ParseTYPEresult t = parseTYPE(varNames, str);
              if(t.getError()!=null) return t;
            str=t.getRest().trim();
            lols.add(NOLABEL);
            lots.add(t.getResult());
          }
          else// this a label followed by a TYPE
          { label = str.substring(0, dotIndex);
            str=cutoff(str, label);
            str=cutoff(str, ".");
            ParseTYPEresult t = parseTYPE(varNames, str);
              if(t.getError()!=null) return t;
            str=t.getRest().trim();
            lols.add(label);
            lots.add(t.getResult());
          }
        }
        else
        { String strBeforeStar = str.substring(0, starIndex);
          int dotIndex = strBeforeStar.indexOf(".");
          if(dotIndex==-1)// the first TYPE has no label
          { ParseTYPEresult t=parseTYPE(varNames, str);
              if(t.getError()!=null) return t;
            str=t.getRest().trim();
            lols.add(NOLABEL);
            lots.add(t.getResult());
          }
          else// has label
          { label = str.substring(0, dotIndex);
            str=cutoff(str, label);
            str=cutoff(str, ".");
            ParseTYPEresult t = parseTYPE(varNames, str);
              if(t.getError()!=null) return t;
            str=t.getRest().trim();
            lols.add(label);
            lots.add(t.getResult());
          }
        }   
      }
      str=cutoff(str, "}");
      return ok(TYPE.PRODUCT(lols,lots),str);
    }
    /* UNION 
    // In UNION TYPE, Any label without a TYPE is the short form of label.UNIT
    */    
    else if(str.startsWith("["))
    { str=cutoff(str,"[");
      List<String> lols = new ArrayList<String>();
      List<TYPE> lots = new ArrayList<TYPE>();
      String label1;
      int barIndex1 = str.indexOf("|");
      if(barIndex1==-1)// this UNIOIN has only one constructor
      { int squareIndex1 = str.indexOf("]");
        if(squareIndex1==-1) 
          throw new RuntimeException("Expected a correct form of UNION TYPE");
        String strBeforeSquare = str.substring(0, squareIndex1);
        int dotIndex1=strBeforeSquare.indexOf(".");
        if(dotIndex1==-1)// this is a label without TYPE, which indicates it a UNIT TYPE
        { label1 = str.substring(0,squareIndex1);
          str=cutoff(str, label1);
          lols.add(label1);
          lots.add(TYPE.UNIT);
        }
        else// this a label followed by a TYPE
        { label1 = str.substring(0, dotIndex1);
          str=cutoff(str, label1);
          str=cutoff(str, ".");
          ParseTYPEresult t1 = parseTYPE(varNames, str);
            if(t1.getError()!=null) return t1;
          str=t1.getRest().trim();
          lols.add(label1);
          lots.add(t1.getResult());
        }
      }
      else// this UNION has more constructors
      { String strBeforeBar = str.substring(0, barIndex1);
        int dotIndex1 = strBeforeBar.indexOf(".");
        if(dotIndex1==-1)// the first constructor has no TYPE
        { label1 = str.substring(0,barIndex1);
          str=cutoff(str, label1);
          lols.add(label1);
          lots.add(TYPE.UNIT);
        }
        else
        { label1 = str.substring(0, dotIndex1);
          str=cutoff(str, label1);
          str=cutoff(str, ".");
          ParseTYPEresult t1 = parseTYPE(varNames, str);
            if(t1.getError()!=null) return t1;
          str=t1.getRest().trim();
          lols.add(label1);
          lots.add(t1.getResult());
        }
      }
      for(int i=1; !str.startsWith("]"); i++)
      { str=cutoff(str, "|");
        String label;
        int barIndex = str.indexOf("|");
        if(barIndex==-1)
        { int squareIndex = str.indexOf("]");
          if(squareIndex==-1) 
            throw new RuntimeException("Expected a correct form of UNION TYPE");
          String strBeforeSquare = str.substring(0, squareIndex);
          int dotIndex=strBeforeSquare.indexOf(".");
          if(dotIndex==-1)
          { label = str.substring(0,squareIndex);
            str=cutoff(str, label);
            lols.add(label);
            lots.add(TYPE.UNIT);
          }
          else
          { label = str.substring(0, dotIndex);
            str=cutoff(str, label);
            str=cutoff(str, ".");
            ParseTYPEresult t = parseTYPE(varNames, str);
              if(t.getError()!=null) return t;
            str=t.getRest().trim();
            lols.add(label);
            lots.add(t.getResult());
          }
        }
        else// this UNION has more constructors
        { String strBeforeBar = str.substring(0, barIndex);
          int dotIndex = strBeforeBar.indexOf(".");
          if(dotIndex==-1)// the first constructor has no TYPE
          { label = str.substring(0,barIndex);
            str=cutoff(str, label);
            lols.add(label);
            lots.add(TYPE.UNIT);
          }
          else
          { label = str.substring(0, dotIndex);
            str=cutoff(str, label);
            str=cutoff(str, ".");
            ParseTYPEresult t = parseTYPE(varNames, str);
              if(t.getError()!=null) return t;
            str=t.getRest().trim();
            lols.add(label);
            lots.add(t.getResult());
          }
        }
      }
      str=cutoff(str,"]");
      return ok(TYPE.UNION(lols, lots), str); 
    }
    /* REC */
    else if(str.startsWith("rec"))
    { str=cutoff(str,"rec");
        if(!str.startsWith("(")) return error(str, "Expected a '(' after rec label.");
      str=cutoff(str,"(");
      int colonIndex = str.indexOf(":");
        if(colonIndex==-1) 
          throw new RuntimeException("Expected a ':' after the TYPE variable.");
      String newVarName = str.substring(0, colonIndex).trim();
        if(varNames.contains(newVarName)) 
          return error(str, newVarName+"has already been declared.");
      varNames.add(newVarName);// varNames does not contain newVarName
      str=cutoff(str,newVarName);
      str=cutoff(str,":");
      //System.out.println("current str="+str);
      ParseTYPEresult unionBody=parseTYPE(varNames,str);
        if(unionBody.getError()!=null) return unionBody;
      str=unionBody.getRest().trim();
        if(!str.startsWith(")")) return error(str, "Expected a ')' at the end of REC TYPE");
      str=cutoff(str,")");
        if(!unionBody.getResult().isUNION()){ throw new RuntimeException("Expect a UNION TYPE body in this REC TYPE.");}
      return ok(TYPE.REC(newVarName, unionBody.getResult()),str);  
    }  
    else if(str.startsWith("List"))
    { str=cutoff(str,"List");
      ParseTYPEresult baseTYPE=parseTYPE(varNames, str);
        if(baseTYPE.getError()!=null) return baseTYPE;
      str=baseTYPE.getRest().trim();
      return ok(TYPE.LIST(baseTYPE.getResult()),str);      
    } 
    else if(str.startsWith("Set"))
    { str=cutoff(str,"Set");
      ParseTYPEresult baseTYPE=parseTYPE(varNames, str);
        if(baseTYPE.getError()!=null) return baseTYPE;
      str=baseTYPE.getRest().trim();
      return ok(TYPE.SET(baseTYPE.getResult()),str);     
    }
    else if(str.startsWith("MSet"))
    { str=cutoff(str,"MSet");
      ParseTYPEresult baseTYPE=parseTYPE(varNames, str);
        if(baseTYPE.getError()!=null) return baseTYPE;
      str=baseTYPE.getRest().trim();
      return ok(TYPE.MSET(baseTYPE.getResult()),str);     
    }
    /* MAPPING */
    else if(str.startsWith("Map")) 
    { str=cutoff(str, "Map");
      ParseTYPEresult t1 = parseTYPE(varNames, str);
        if(t1.getError()!=null) return t1;
      str=t1.getRest().trim();
        if(!str.startsWith("=>")) 
          throw new RuntimeException("Expected a => between domain TYPE and codomain TYPE");
      str=cutoff(str, "=>");
      ParseTYPEresult t2 = parseTYPE(varNames, str); 
        if(t2.getError()!=null) return t2;
      str=t2.getRest().trim();
      return ok(TYPE.MAPPING(t1.getResult(), t2.getResult()), str);     
    }
    else throw new RuntimeException("There is no other TYPEs currently"); 
  } 
}
