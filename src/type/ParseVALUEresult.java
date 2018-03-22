package type;

import value.PrimUnit;
import value.PrimBool;
import value.PrimChar;
import value.PrimString;
import value.PrimNat;
//import value.PrimReal;
import value.TypeList;
import value.TypeSet;
//import value.TypeMapping;
//import value.TypeMultiset;
import value.TypeT;
//import value.TypeProduct;
//import TypeT.TypeUnion;
//import utility.LabelandTypeT;
//import utility.ListOfLabelandTYPEs;
//import utility.ListOfLabelandTypeTs;

public class ParseVALUEresult 
{ private final TypeT res;
  private final String rest, error;
  
  private ParseVALUEresult(TypeT res, String rest, String error){ this.res=res; this.rest=rest; this.error=error;}
  // Either res == null or error == null
  public static ParseVALUEresult ok(TypeT res, String rest){ return new ParseVALUEresult(res, rest, null);}
  public static ParseVALUEresult error(String rest, String error){ return new ParseVALUEresult(null, rest, error);}        
  
  public TypeT getResult(){ return this.res;}  
  public String getRest(){ return this.rest;}  
  public String getError(){ return this.error;}        
       
  @Override
  public String toString()
  { StringBuilder buf=new StringBuilder();
    if(this.res!=null)
    { buf.append(this.res);
      if(this.rest.length()!=0){ buf.append(", Rest="); buf.append(this.rest);}  
    }
    else{ buf.append("Rest="); buf.append(this.rest); buf.append(", ERROR="); buf.append(this.error);}    
    return buf.toString();
  }        
  
  private static String cutoff(String str, String prefix)
  { str=str.trim();
    if(str.startsWith(prefix)){ return str.substring(prefix.length()).trim();}
    else{ return str;}  
  }   
  private static boolean startsWithInt(String str)
  { try
    { Integer.parseInt(str.substring(0,1));return true;
    } catch (NumberFormatException ex){return false;}
  }        
  private static int getInt(String str)
  { int n = 0;
    for (int i=0; i < str.length(); i++) 
    { char c = str.charAt(i);
      if (c < '0' || c > '9') break;
      n = n * 10 + c - '0';
    }
    return n;
  }
 
  private static double getDouble(String str)
  { StringBuilder buf = new StringBuilder();
    char c;
    for (int i = 0; i < str.length() ; i++) 
    { c = str.charAt(i);
      if (Character.isDigit(c) || c == '.') { buf.append(c);}
      else break;
    }
    //System.out.println(buf.toString());
    return Double.parseDouble(buf.toString());
  }        
  public static ParseVALUEresult parseVALUE(TYPE T, String str)
  { str=str.trim();
    if(T.isUNIT())
    { if(str.startsWith("unit")) return ok(new PrimUnit(T), cutoff(str, "unit"));
      else{ return error(str, "Expected a UNIT TYPE value.");}
    }    
    else if(T.isBOOL())
    { if(str.startsWith("true")){ return ok(new PrimBool(T,(boolean)true), cutoff(str, "true"));}
      else if(str.startsWith("false")){ return ok(new PrimBool(T,(boolean)false), cutoff(str, "false"));}
      else{ return error(str, "Expected a BOOL TYPE value.");}
    } 
    else if(T.isCHAR())
    { String c=str.substring(0,1);
      str=cutoff(str, c);
      return ok(new PrimChar(T,c.charAt(0)), str);
    } 
    // since a string contains special characters, such as spaces
    // value of a string needs a starting point and an ending point
    // but how to deal with sentence contains " mark ????
    else if(T.isSTRING())
    {   if(!str.startsWith("\"")){ return error(str, "Expected a STRING TYPE value.");}
      str=cutoff(str,"\"");
        if(str.startsWith("\"")){ str=cutoff(str,"\""); return ok(new PrimString(T, ""),str);}
      int dotIndex=str.indexOf("\"");// Find the position of the " symbol at the end of the string value
        if(dotIndex==-1)throw new RuntimeException("Has no \" in this str.");
      String content=str.substring(0, dotIndex).trim();
      str=cutoff(str,content);
        if(!str.startsWith("\"")) { return error(str, "Expected a \" at the end of string value");}
      str=cutoff(str,"\"");
        return ok(new PrimString(T, content), str);
    } 
    else if(T.isNAT())
    { if(startsWithInt(str)){ return ok(new PrimNat(T,Integer.parseInt(String.valueOf(getInt(str)))),cutoff(str, String.valueOf(getInt(str))));}
      else{ return error(str, "Expected a NAT TYPE value.");}
    }
    /*  
    else if(T.isREAL())
    { return ok(new PrimReal(T,getDouble(str)),cutoff(str,String.valueOf(getDouble(str))));  
    }*/  
    /*
    else if(T.isPRODUCT())
    {   if(!str.startsWith("(")){ return error(str, "Expected a '(' at the beginning of a PRODUCT TYPE value.");}
      str=cutoff(str,"(");
      int dotIndex=str.indexOf(".");
        if(dotIndex==-1)throw new RuntimeException("Has no '.' in this str.");
      String label=str.substring(0, dotIndex).trim();
        if(!T.getMembers().matchFstLabel(label)){ return error(str, "This label="+label+" does not match the first label in this TYPE T="+T);}
      str=cutoff(str,label);
        if(!str.startsWith(".")){ return error(str, "Expected a '.' between the label and the value.");}
      str=cutoff(str,".");
      ParseVALUEresult head=parseVALUE(T.getMembers().head().getTYPE(),str);
        if(head.getError()!=null){ return head;}  
      str=head.getRest().trim();
      ListOfLabelandTypeTs listofLabelandTypeTs=new ListOfLabelandTypeTs(new LabelandTypeT(label, head.getResult()),new ListOfLabelandTypeTs());
      ListOfLabelandTYPEs temp=T.getMembers().rest();
        if(temp.isEmptyListOfLabelandTYPEs()&&!str.startsWith(")")){ return error(str, "The number of members in TYPE T less than the number of members in the value.");}
        if(!temp.isEmptyListOfLabelandTYPEs()&&str.startsWith(")")){ return error(str, "The number of members in TYPE T more than the number of members in the value.");}
      while(!str.startsWith(")"))
      {   if(!str.startsWith(",")){ return error(str, "Expected a ',' between elements in PRODUCT TYPE value.");}
        str=cutoff(str,",");
        int dot=str.indexOf(".");
          if(dot==-1)throw new RuntimeException("Has no '.' in this str.");
        String newLabel=str.substring(0, dot).trim();
          if(!temp.matchFstLabel(newLabel)){ return error(str, "This label="+newLabel+" does not match the first label in this TYPE T="+T);}
        str=cutoff(str,newLabel);
          if(!str.startsWith(".")){ return error(str, "Expected a '.' between the label and the value.");}
        str=cutoff(str,".");
        ParseVALUEresult newEl=parseVALUE(temp.head().getTYPE(),str);
        str=newEl.getRest().trim();
        listofLabelandTypeTs=listofLabelandTypeTs.append(new LabelandTypeT(newLabel, newEl.getResult()));
        temp=temp.rest();
          if(temp.isEmptyListOfLabelandTYPEs()&&!str.startsWith(")")){ return error(str, "The number of members in TYPE T less than the number of members in the value.");}
          if(!temp.isEmptyListOfLabelandTYPEs()&&str.startsWith(")")){ return error(str, "The number of members in TYPE T more than the number of members in the value.");}
      }
        if(!str.startsWith(")")) { return error(str, "Expected a ')' at the end of typedProduct");}
      str=cutoff(str,")");
      return ok(new TypeProduct(T,listofLabelandTypeTs),str);            
    } 
    else if(T.isUNION())
    { int dotIndex=str.indexOf(".");
        if(dotIndex==-1)throw new RuntimeException("Has no '.' in this str.");
      String label=str.substring(0, dotIndex).trim();//this trim() used here to invoid there is some space before the dot '.'
        if(!T.getMembers().hasLabel(label)){ return error(str, "There is no such label="+label+" in this TYPE T="+T);}
      str=cutoff(str,label);
        if(!str.startsWith(".")){ return error(str, "Expected a '.' between the label and the value.");}
      str=cutoff(str,".");
      TYPE t=T.getMembers().getItsTYPE(label);
      ParseVALUEresult value=parseVALUE(t,str);
        if(value.getError()!=null) { return value;}
      str=value.getRest().trim(); 
      return ok(new TypeUnion(T, label, value.getResult()),str);
    } 
    else if(T.isREC()){ return parseVALUE(TYPE.unfold(T), str);} // REC TYPE
    */
    else if(T.isLIST())
    {   if(!str.startsWith("[")){ return error(str, "Expected a LIST TYPE value.");}
      str=cutoff(str,"[");
        if(str.startsWith("]")){ str=cutoff(str,"]"); return ok(new TypeList(T.getBaseTYPE()),str);}
      ParseVALUEresult head=parseVALUE(T.getBaseTYPE(),str);
        if(head.getError()!=null){ return head;}  
      str=head.getRest().trim();
      TypeList list = new TypeList(T.getBaseTYPE()).append(head.getResult());
      while(!str.startsWith("]"))
      { if(!str.startsWith(","))
        { return error(str, "Expected a ',' between elements in TypeList.");}
        str=cutoff(str,",");
        ParseVALUEresult newEl=parseVALUE(T.getBaseTYPE(),str);
        str=newEl.getRest().trim();
        list=list.append(newEl.getResult());
      } 
      str=cutoff(str,"]");
      return ok(list,str);
    }   
    else if(T.isSET())
    { if(!str.startsWith("{")){ return error(str, "Expected a SET TYPE value.");}
      str=cutoff(str,"{");
        if(str.startsWith("}")){ str=cutoff(str,"}"); return ok(new TypeSet(T.getBaseTYPE()),str);}
      ParseVALUEresult head=parseVALUE(T.getBaseTYPE(),str);
        if(head.getError()!=null){ return head;}  
      str=head.getRest().trim(); 
      TypeSet set=new TypeSet(T.getBaseTYPE()).append(head.getResult());
      while(!str.startsWith("}"))
      {   if(!str.startsWith(",")){ return error(str, "Expected a ',' between elements in TypeSet.");}
        str=cutoff(str,",");
        ParseVALUEresult newEl=parseVALUE(T.getBaseTYPE(),str); 
        str=newEl.getRest().trim(); //System.out.println(str);
        set=set.append(newEl.getResult()); //System.out.println("Inside loop set="+set);
      } 
      str=cutoff(str,"}");
      //System.out.println("Outside loop set="+set);
      return ok(set,str);
    } 
    /* 
    else if(T.isMSET())
    { if(!str.startsWith("{{")){ return error(str, "Expected a MSET TYPE value.");}
      str=cutoff(str,"{{");
        if(str.startsWith("}}")){ str=cutoff(str,"}}"); return ok(TypeMultiset.EMPTY_MSET(T.getBaseTYPE()),str);}
      ParseVALUEresult head=parseVALUE(T.getBaseTYPE(),str);
        if(head.getError()!=null){ return head;}  
      str=head.getRest().trim();
      TypeMultiset mset=TypeMultiset.EMPTY_MSET(T.getBaseTYPE()).put(head.getResult());
      while(!str.startsWith("}}"))
      {   if(!str.startsWith(",")){ return error(str, "Expected a ',' between elements in TypeMultiSet.");}
        str=cutoff(str,",");
        ParseVALUEresult newEl=parseVALUE(T.getBaseTYPE(),str);
        str=newEl.getRest().trim();
        mset=mset.append(newEl.getResult());
      } 
      str=cutoff(str,"}");
      return ok(mset,str);
    } 
    else if(T.isMAPPING())
    { if(!str.startsWith("[")){ return error(str, "Expected a '[' at the beginning of a MAPPING TYPE value.");}
      str=cutoff(str,"[");
      ParseVALUEresult head=parseVALUE(TYPE.PRODUCT(T.getMembers()),str);
        if(head.getError()!=null){ return head;}  
      str=head.getRest().trim();
      TypeMapping mapping=TypeMapping.EMPTY_MAPPING(T).extend(head.getResult());
      while(!str.startsWith("]"))
      {   if(!str.startsWith(",")){ return error(str, "Expected a ',' between elements in MAPPING TYPE value.");}
        str=cutoff(str,",");
        ParseVALUEresult newEl=parseVALUE(TYPE.PRODUCT(T.getMembers()),str);
        mapping=mapping.extend(newEl.getResult());
        str=newEl.getRest().trim();
      }
        if(!str.startsWith("]")) { return error(str, "Expected a ']' at the end of typedMapping");}
      str=cutoff(str,")");
      return ok(mapping,str);   
    }*/      
    else { return error(str, "There is no other TYPE anymore.");}    
  }         
}
