package type;

//import utility.ListOfVars;
//import utility.ListOfLabelandTYPEs;
//import utility.LabelandTYPE;

public class ParseTYPEresult 
{ private final TYPE res;
  private final String rest, error;
  private ParseTYPEresult(TYPE res, String rest, String error){ this.res=res; this.rest=rest; this.error=error;}
  // Either res == null Or error == null
  public static ParseTYPEresult ok(TYPE res, String rest){ return new ParseTYPEresult(res, rest, null);}
  public static ParseTYPEresult error(String rest, String error){ return new ParseTYPEresult(null, rest, error);}        
  
  public TYPE getResult() { return this.res;}  
  public String getRest() { return this.rest;}  
  public String getError() { return this.error;}        
       
  @Override
  public String toString()
  { StringBuilder buf=new StringBuilder();
    if(this.res!=null)
    { buf.append(this.res);
      if(this.rest.length()!=0){ buf.append(", Rest="); buf.append(this.rest);}  
    }
    else { buf.append("Rest="); buf.append(this.rest); buf.append(", ERROR="); buf.append(this.error);}    
    return buf.toString();
  }   
  /*
    //Static nested class Components
    //Used to return listoflabels, listofTYPEs and a str
    //The listoflabels and listofTYPEs are the components of the PRODUCT and UNION TYPE constructor.
    private static class Components
    { private final ListOfLabelandTYPEs listofLabelandTYPEs;
      private final String str;
      private Components(ListOfLabelandTYPEs listofLabelandTYPEs, String str)
      { this.listofLabelandTYPEs=listofLabelandTYPEs; this.str=str;}
      private ListOfLabelandTYPEs getListofLabelandTYPEs(){ return this.listofLabelandTYPEs;}
      private String getStr(){ return this.str;}
 
      private static Components accumulate( ListOfVars vars, String str, String separator,String title)
      {   if(!str.startsWith(title)) throw new RuntimeException("Expected a "+title+" at the beginning of the "+str+".");
        str=cutoff(str,title);  
          if(!str.startsWith("(")) throw new RuntimeException("Expected a '(' after"+title+"."); 
        str=cutoff(str,"(");
        int dotIndex=str.indexOf(".");
          if(dotIndex==-1)throw new RuntimeException("Has no '.' in this str.");
        String headLabel=str.substring(0, dotIndex);
        str=cutoff(str,headLabel+".");
        ParseTYPEresult t1=parseTYPE(vars, str);
          if(t1.getError()!=null)  throw new RuntimeException(t1.getError());
        str=t1.getRest().trim();
        ListOfLabelandTYPEs listofLabelandTYPEs = new ListOfLabelandTYPEs(new LabelandTYPE(headLabel,t1.getResult()),new ListOfLabelandTYPEs());
        while(!str.startsWith(")"))
        {   if(!str.startsWith(separator)) throw new RuntimeException("Expected a '"+separator+"' between two TYPEs in "+title+" TYPE.");
          str=cutoff(str,separator);
          int index=str.indexOf(".");
            if(index==-1)throw new RuntimeException("Has no '.' in this str.");
          String label=str.substring(0, index);
          str=cutoff(str,label+".");
          ParseTYPEresult t=parseTYPE(vars, str);
            if(t1.getError()!=null)  throw new RuntimeException(t.getError());
          str=t.getRest().trim();
          listofLabelandTYPEs=listofLabelandTYPEs.append(new LabelandTYPE(label,t.getResult()));
        }
        str=cutoff(str,")");
        return new Components(listofLabelandTYPEs, str); 
      } 
    }//End of Static nested class Components.
  */  
  private static ParseTYPEresult getBaseTYPE(ListOfVars vars, String str, String title)
  {   if(!str.startsWith(title)) throw new RuntimeException("Expected a "+title+" at the beginning of the "+str+".");
    str=cutoff(str,title);
      if(!str.startsWith("(")) return error(str, "Expected a '(' after"+title+"label.");
    str=cutoff(str,"(");
    ParseTYPEresult t=parseTYPE(vars, str);
      if(t.getError()!=null) return t;  
    str=t.getRest().trim();
      if(!str.startsWith(")")) return error(str, "Expected a ')' at the end of"+title+"TYPE");
    str=cutoff(str,")");
    return ok(t.getResult(),str); 
  }         
  //Cutoff the substring "prefix" from the string "str"
  private static String cutoff(String str, String prefix)
  { str=str.trim();
    if(str.startsWith(prefix)){ return str.substring(prefix.length()).trim();}
    else{ return str;}  
  } 
  
  private static boolean startsWithDouble(String str)
  { try
    { Double.parseDouble(str);return true;
    } catch (NumberFormatException ex){return false;}
  } 
  public static ParseTYPEresult parseTYPE(ListOfVars vars, String str)
  { for(ListOfVars v=vars; !v.isEmptyListOfStrings(); v=v.rest())
    { if(str.startsWith(v.head())){ str=cutoff(str, v.head()); return ok(TYPE.VAR(v.head()),str);}}  
    str=str.trim();
    if(str.startsWith("UNIT"))
    { return ok(TYPE.UNIT, cutoff(str, "UNIT"));}    
    else if(str.startsWith("BOOL"))
    { return ok(TYPE.BOOL, cutoff(str, "BOOL"));} 
    else if(str.startsWith("NAT"))
    { return ok(TYPE.NAT, cutoff(str, "NAT"));} 
    else if(str.startsWith("CHAR"))
    { return ok(TYPE.CHAR, cutoff(str, "CHAR"));}
     else if(str.startsWith("STRING"))
    { return ok(TYPE.STRING, cutoff(str, "STRING"));} 
    else if(str.startsWith("REAL"))
    { str=cutoff(str, "REAL");
        if(!str.startsWith("(")) return error(str, "Expected a '(' after REAL.");
      str=cutoff(str,"(");
        int index=str.indexOf(")");//get index of ")"
        if(index==-1)throw new RuntimeException("Error: Has no end ')' in this str.");
      String acc=str.substring(0, index).trim();
        if(!startsWithDouble(acc)) return error(str, "acc="+acc+" is not a double value, Expected a double value as the accuracy.");
      str=cutoff(str, acc);
      if(!str.startsWith(")")) return error(str, "Expected a ')' at the end of REAL TYPE");
    str=cutoff(str,")");
    return ok(TYPE.REAL(Double.parseDouble(acc)),str); 
    }    
    //else if(str.startsWith("PRODUCT"))
    //{ Components comp=Components.accumulate(vars, str, "*", "PRODUCT");
      //return ok(TYPE.PRODUCT(comp.getListofLabelandTYPEs()),comp.getStr()); 
    //}    
    //else if(str.startsWith("UNION"))
    //{ Components comp=Components.accumulate(vars, str, "|", "UNION");
      //return ok(TYPE.UNION(comp.getListofLabelandTYPEs()),comp.getStr());    
    //}  
    //else if(str.startsWith("MAPPING"))
    //{ Components comp=Components.accumulate(vars, str, "=>", "MAPPING");
      //return ok(TYPE.MAPPING(comp.getListofLabelandTYPEs()),comp.getStr());     
    //} 
    else if(str.startsWith("SET"))
    { ParseTYPEresult baseTYPE=getBaseTYPE(vars, str, "SET");
      return ok(TYPE.SET(baseTYPE.getResult()),baseTYPE.getRest());     
    }
    else if(str.startsWith("MSET"))
    { ParseTYPEresult baseTYPE=getBaseTYPE(vars, str, "MSET");
      return ok(TYPE.MSET(baseTYPE.getResult()),baseTYPE.getRest());     
    }
    else if(str.startsWith("LIST"))
    { ParseTYPEresult baseTYPE=getBaseTYPE(vars, str, "LIST");
      return ok(TYPE.LIST(baseTYPE.getResult()),baseTYPE.getRest());      
    } 
    else if(str.startsWith("REC"))
    { str=cutoff(str,"REC");
        if(!str.startsWith("(")) return error(str, "Expected a '(' after REC label.");
      str=cutoff(str,"(");
      String newVar=str.substring(0,1);
        if(vars.hasStr(newVar)) return error(str, newVar+"has already been declared.");
      str=cutoff(str,newVar);
        if(!str.startsWith(":")) return error(str, "Expected a ':' after a VAR TYPE");
      str=cutoff(str,":");
      ParseTYPEresult unionBody=parseTYPE(vars.ins(newVar),str);
        if(unionBody.getError()!=null) return unionBody;  
      str=unionBody.getRest().trim();
        if(!str.startsWith(")")) return error(str, "Expected a ')' at the end of REC TYPE");
      str=cutoff(str,")");
        if(!unionBody.getResult().isUNION()){ throw new RuntimeException("Expect a UNION TYPE body in this REC TYPE.");}
      return ok(TYPE.REC(newVar, unionBody.getResult()),str);  
    }  
    else 
    { return error(str, "There is no other TYPE anymore.");}    
  }          
}
