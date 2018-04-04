package type;

import java.util.List;
import java.util.ArrayList;

import value.*;

public class ParseVALUEresult 
{ private final TypeT res;
  private final String rest, error;
  
  private ParseVALUEresult(TypeT res, String rest, String error)
  { this.res=res; this.rest=rest; this.error=error;}
  // Either res == null or error == null
  public static ParseVALUEresult ok(TypeT res, String rest)
  { return new ParseVALUEresult(res, rest, null);}
  public static ParseVALUEresult error(String rest, String error)
  { return new ParseVALUEresult(null, rest, error);}        
  
  public TypeT getResult(){ return this.res;}  
  public String getRest(){ return this.rest;}  
  public String getError(){ return this.error;}        
       
  @Override
  public String toString()
  { StringBuilder buf=new StringBuilder();
    if(this.res!=null)
    { buf.append(this.res);
      if(this.rest.length()!=0)
      { buf.append(", Rest="); 
        buf.append(this.rest);
      }  
    }
    else
    { buf.append("Rest="); 
      buf.append(this.rest); 
      buf.append(", ERROR="); 
      buf.append(this.error);
    }    
    return buf.toString();
  }        
  
  private static String cutoff(String str, String prefix)
  { str=str.trim();
    if(str.startsWith(prefix)){ return str.substring(prefix.length()).trim();}
    else{ return str;}  
  }
  // use to parse natural number and integers  
  private static int getNat(String str)
  { StringBuilder buf = new StringBuilder();
    char c;
    for (int i = 0; i < str.length() ; i++) 
    { c = str.charAt(i);
      if(!Character.isDigit(c)) break; 
      buf.append(c);
    }
    return Integer.parseInt(buf.toString());
  }  
  // use to parse real numbers
  // return the real number string, since 20 will has format 20.0
  private static String getDouble(String str)
  { StringBuilder buf = new StringBuilder();
    char c;
    for (int i = 0; i < str.length() ; i++) 
    { c = str.charAt(i);
      // any character has single quotes, any string has double quotes
      // it is impossible make the mistake of e or E in a doule value 
      if (Character.isDigit(c) || 
          c == '.'|| c == '-' || c == '+' || c == 'e' || c == 'E') 
        buf.append(c);
      else break;
    }
    return buf.toString();
  } 

  private static String NOLABEL="nolabel";
   
  public static ParseVALUEresult parseVALUE(TYPE T, String str)
  { str=str.trim();
    if(T.isUNIT())
    { if(str.startsWith("unit")) return ok(new PrimUnit(T), cutoff(str, "unit"));
      else{ return error(str, "Expected a UNIT TYPE value.");}
    }    
    /* BOOL */
    else if(T.isBOOL())
    { if(str.startsWith("true"))
      { return ok(new PrimBool(T,(boolean)true), cutoff(str, "true"));}
      else if(str.startsWith("false"))
      { return ok(new PrimBool(T,(boolean)false), cutoff(str, "false"));}
      else{ return error(str, "Expected a BOOL TYPE value.");}
    } 
    /* CHAR */
    else if(T.isCHAR())
    {  if(!str.startsWith("'")) return error(str, "Expected a CHAR TYPE value.");
      str = cutoff(str, "'"); 
      String c=str.substring(0,1);// whatever it is, get the first character
      str=cutoff(str, c);
       if(!str.startsWith("'")) 
         return error(str, "Expected a single quote at the end of a CHAR TYPE value.");
      str=cutoff(str, "'");
      return ok(new PrimChar(T,c.charAt(0)), str);
    } 
    /* STRING */
    // since a string contains special characters, such as spaces
    // value of a string needs a starting point and an ending point
    // but how to deal with sentence contains " mark ????
    // other characters like \',\",\\,\t,\b,\r,\n,\f
    else if(T.isSTRING())
    {   if(!str.startsWith("\"")) 
          return error(str, "Expected a STRING TYPE value.");
      str=cutoff(str,"\"");
        if(str.startsWith("\""))
        { str=cutoff(str,"\""); 
          return ok(new PrimString(T, ""),str);
        }
      int dotIndex=str.indexOf("\"");
      // Find the position of the " symbol at the end of the string value
        if(dotIndex==-1)throw new RuntimeException("Has no \" in this str.");
      String content=str.substring(0, dotIndex).trim();
      str=cutoff(str,content);
        if(!str.startsWith("\""))
          return error(str, "Expected a \" at the end of string value");
      str=cutoff(str,"\"");
        return ok(new PrimString(T, content), str);
    }
    /* NAT */
    else if(T.isNAT())
    { if(str.startsWith("-")) 
         return error(str, "Expected a NAT TYPE value, no negative value.");
      int nat = getNat(str);
      str=cutoff(str,String.valueOf(nat));
      return ok(new PrimNat(T,nat), str);
    }
    /* INT */
    else if(T.isINT())
    { if(str.startsWith("-"))//negative integer
      { str = cutoff(str, "-");
        int nat = getNat(str);
        String natStr = String.valueOf(nat);
        str=cutoff(str,natStr);
        int intNum = Integer.parseInt("-"+natStr);
        return ok(new PrimInt(T, intNum), str);
      }
      else//positive
      { int nat = getNat(str);
        str=cutoff(str,String.valueOf(nat));
        return ok(new PrimInt(T, nat), str);
      }
    }
    /* REAL */
    else if(T.isREAL())
    { String realStr = getDouble(str);
      double real = Double.parseDouble(realStr);
      str=cutoff(str,realStr);
      return ok(new PrimReal(T,real),str);  
    } 
    /* PRODUCT */
    else if(T.isPRODUCT())
    { if(!str.startsWith("("))
        return error(str, "Expected a '(' at the beginning of a PRODUCT TYPE value.");
      str=cutoff(str,"(");
      List<String> lols = new ArrayList<>();
      List<TypeT> lots = new ArrayList<>();
      String label1;
      TYPE type1=T.getTYPEs().get(0);
      int commaIndex1=str.indexOf(",");
      if(commaIndex1==-1)// this product value only has one component
      { int endIndex1=str.indexOf(")");
        if(endIndex1==-1) 
          throw new RuntimeException("Please provide product values with ')' at the end");
        String strBeforeEnd=str.substring(0, endIndex1);
        int eqIndex1 = strBeforeEnd.indexOf(".");// was =
        if(eqIndex1==-1)// this value has no label
        { if(!T.getLabels().get(0).equals(NOLABEL)) 
            throw new RuntimeException("This label="+NOLABEL+
                                       "does not matching the first label in "+T);
          ParseVALUEresult value1=parseVALUE(type1, str);
            if(value1.getError()!=null) return value1;
          TypeT value1res = value1.getResult();
          if(!type1.equals(value1res.typeOf()))
            throw new RuntimeException("This value's TYPE="+value1res.typeOf()+
                                       "does not matching the first TYPE in "+T);
          str=value1.getRest().trim();
          lols.add(NOLABEL);
          lots.add(value1res);
        }
        else// this value has label
        { label1 = str.substring(0, eqIndex1);
          str=cutoff(str, label1);
          str=cutoff(str, ".");// was =
          if(!T.getLabels().get(0).equals(label1)) 
            throw new RuntimeException("This label="+label1+
                                       "does not matching the first label in "+T);
          ParseVALUEresult value1=parseVALUE(type1, str);
            if(value1.getError()!=null) return value1;
          TypeT value1res = value1.getResult();
          if(!type1.equals(value1res.typeOf()))
            throw new RuntimeException("This value's TYPE="+value1res.typeOf()+
                                       "does not matching the first TYPE in "+T);
          str=value1.getRest().trim();
          lols.add(label1);
          lots.add(value1res);
        }
      }
      else// this PRODUCT value has more than one components
      { String strBeforeComma=str.substring(0, commaIndex1);
        int eqIndex1 = strBeforeComma.indexOf(".");// was =
        if(eqIndex1==-1)// this value has no label
        { if(!T.getLabels().get(0).equals(NOLABEL)) 
            throw new RuntimeException("This label="+NOLABEL+
                                       "does not matching the first label in "+T);
          ParseVALUEresult value1=parseVALUE(type1, str);
            if(value1.getError()!=null) return value1;
          TypeT value1res = value1.getResult();
          if(!type1.equals(value1res.typeOf()))
            throw new RuntimeException("This value's TYPE="+value1res.typeOf()+
                                       "does not matching the first TYPE in "+T);
          str=value1.getRest().trim();
          lols.add(NOLABEL);
          lots.add(value1res);
        }
        else// this value has label
        { label1 = str.substring(0, eqIndex1);
          str=cutoff(str, label1);
          str=cutoff(str, ".");// was =
          if(!T.getLabels().get(0).equals(label1)) 
            throw new RuntimeException("This label="+label1+
                                       "does not matching the first label in "+T);
          ParseVALUEresult value1=parseVALUE(type1, str);
            if(value1.getError()!=null) return value1;
          TypeT value1res = value1.getResult();
          if(!type1.equals(value1res.typeOf()))
            throw new RuntimeException("This value's TYPE="+value1res.typeOf()+
                                       "does not matching the first TYPE in "+T);
          str=value1.getRest().trim();
          lols.add(label1);
          lots.add(value1res);
        }
      }
      for(int i=1; !str.startsWith(")"); i++)
      { str=cutoff(str, ",");
        String label;
        TYPE type = T.getTYPEs().get(i);
        int commaIndex=str.indexOf(",");
        if(commaIndex==-1)
        { int endIndex=str.indexOf(")");
          if(endIndex==-1) 
            throw new RuntimeException("Please provide product values with ')' at the end");
          String strBeforeEnd=str.substring(0, endIndex);
          int eqIndex = strBeforeEnd.indexOf(".");// was =
          if(eqIndex==-1)// this value has no label
          { if(!T.getLabels().get(i).equals(NOLABEL)) 
              throw new RuntimeException("This label="+NOLABEL+
                                         "does not matching the"+(i+1)+"label in "+T);
            ParseVALUEresult value=parseVALUE(type, str);
              if(value.getError()!=null) return value;
            TypeT valueRes = value.getResult();
            if(!type.equals(valueRes.typeOf()))
              throw new RuntimeException("This value's TYPE="+valueRes.typeOf()+
                                         "does not matching the "+(i+1)+" TYPE in "+T);
            str=value.getRest().trim();
            lols.add(NOLABEL);
            lots.add(valueRes);
          }
          else// this value has label
          { label = str.substring(0, eqIndex);
            str=cutoff(str, label);
            str=cutoff(str, ".");// was =
            if(!T.getLabels().get(i).equals(label)) 
              throw new RuntimeException("This label="+label+
                                         "does not matching the "+(i+1)+" label in "+T);
            ParseVALUEresult value=parseVALUE(type, str);
              if(value.getError()!=null) return value;
            TypeT valueRes = value.getResult();
            if(!type.equals(valueRes.typeOf()))
              throw new RuntimeException("This value's TYPE="+valueRes.typeOf()+
                                         "does not matching the "+(i+1)+" TYPE in "+T);
            str=value.getRest().trim();
            lols.add(label);
            lots.add(valueRes);
          }
        }
        else// has more components
        { String strBeforeComma=str.substring(0, commaIndex);
          int eqIndex = strBeforeComma.indexOf(".");// was =
          if(eqIndex==-1)// this value has no label
          { if(!T.getLabels().get(i).equals(NOLABEL)) 
              throw new RuntimeException("This label="+NOLABEL+
                                         "does not matching the "+(i+1)+" label in "+T);
            ParseVALUEresult value=parseVALUE(type, str);
              if(value.getError()!=null) return value;
            TypeT valueRes = value.getResult();
            if(!type.equals(valueRes.typeOf()))
              throw new RuntimeException("This value's TYPE="+valueRes.typeOf()+
                                         "does not matching the "+(i+1)+" TYPE in "+T);
            str=value.getRest().trim();
            lols.add(NOLABEL);
            lots.add(valueRes);
          }
          else// this value has label
          { label = str.substring(0, eqIndex);
            str=cutoff(str, label);
            str=cutoff(str, ".");// was =
            if(!T.getLabels().get(i).equals(label)) 
              throw new RuntimeException("This label="+label+
                                         "does not matching the "+(i+1)+" label in "+T);
            ParseVALUEresult value=parseVALUE(type, str);
              if(value.getError()!=null) return value;
            TypeT valueRes = value.getResult();
            if(!type.equals(valueRes.typeOf()))
              throw new RuntimeException("This value's TYPE="+valueRes.typeOf()+
                                         "does not matching the "+(i+1)+" TYPE in "+T);
            str=value.getRest().trim();
            lols.add(label);
            lots.add(valueRes);
          }
        }
      }
      str=cutoff(str, ")");
      return ok(new TypeProduct(T, lols, lots), str);
    } 
    /* UNION */
    else if(T.isUNION())
    { List<String> lols = T.getLabels();
      String label="";
      for(int i=0; i<lols.size(); i++)
      { label = lols.get(i);
        if(str.startsWith(label)) break;
      }
      if(label.length()==0) 
        throw new RuntimeException("This str="+str+
                                   " does not start with any label in this TYPE");
      str=cutoff(str, label);
      if(str.startsWith("."))
      { str=cutoff(str, ".");
        ParseVALUEresult t = parseVALUE(T.getTYPE(label), str);
          if(t.getError()!=null) return t;
        str=t.getRest().trim();
        return ok(new TypeUnion(T, label, t.getResult()), str);
      }
      return ok(new TypeUnion(T, label, new PrimUnit(TYPE.UNIT)), str);
    }
    /* REC */
    else if(T.isREC()){ return parseVALUE(TYPE.unfold(T), str);} // REC TYPE
    /* LIST */
    else if(T.isLIST())
    { if(!str.startsWith("[")) 
        return error(str, "Expected a LIST TYPE value.");
      str=cutoff(str,"[");
      if(str.startsWith("]"))
      { str=cutoff(str,"]"); 
         return ok(new TypeList(T.getBaseTYPE()),str);
      }
      ParseVALUEresult head=parseVALUE(T.getBaseTYPE(),str);
        if(head.getError()!=null) return head;  
      str=head.getRest().trim();
      TypeList list = new TypeList(T.getBaseTYPE()).append(head.getResult());
      while(!str.startsWith("]"))
      { if(!str.startsWith(","))
          return error(str, "Expected a ',' between elements in TypeList.");
        str=cutoff(str,",");
        ParseVALUEresult newEl=parseVALUE(T.getBaseTYPE(),str);
          if(newEl.getError()!=null) return newEl;
        str=newEl.getRest().trim();
        list=list.append(newEl.getResult());
      } 
      str=cutoff(str,"]");
      return ok(list,str);
    } 
    /* SET */
    else if(T.isSET())
    { if(!str.startsWith("{")) 
        return error(str, "Expected a SET TYPE value.");
      str=cutoff(str,"{");
      if(str.startsWith("}"))
      { str=cutoff(str,"}"); 
        return ok(new TypeSet(T.getBaseTYPE()),str);
      }
      ParseVALUEresult head=parseVALUE(T.getBaseTYPE(),str);
        if(head.getError()!=null) return head;  
      str=head.getRest().trim();
      TypeSet set = new TypeSet(T.getBaseTYPE()).add(head.getResult());
      while(!str.startsWith("}"))
      { if(!str.startsWith(","))
          return error(str, "Expected a ',' between elements in TypeSet.");
        str=cutoff(str,",");
        ParseVALUEresult newEl=parseVALUE(T.getBaseTYPE(),str);
          if(newEl.getError()!=null) return newEl;
        str=newEl.getRest().trim();
        set=set.add(newEl.getResult());
      } 
      str=cutoff(str,"}");
      return ok(set,str);
    } 
    else if(T.isMSET())
    { if(!str.startsWith("<")) 
        return error(str, "Expected a MSET TYPE value.");
      str=cutoff(str,"<");
      if(str.startsWith(">"))
      { str=cutoff(str,">"); 
        return ok(new TypeMultiset(T.getBaseTYPE()),str);
      }
      ParseVALUEresult head=parseVALUE(T.getBaseTYPE(),str);
        if(head.getError()!=null) return head;  
      str=head.getRest().trim();
      TypeMultiset multiset = new TypeMultiset(T.getBaseTYPE()).put(head.getResult());
      while(!str.startsWith(">"))
      { if(!str.startsWith(","))
          return error(str, "Expected a ',' between elements in TypeMultiset.");
        str=cutoff(str,",");
        ParseVALUEresult newEl=parseVALUE(T.getBaseTYPE(),str);
          if(newEl.getError()!=null) return newEl;
        str=newEl.getRest().trim();
        multiset=multiset.put(newEl.getResult());
      } 
      str=cutoff(str,">");
      return ok(multiset,str);
    } 
    else if(T.isMAPPING())
    { if(!str.startsWith("[")) return error(str, "Expect a MAPPING TYPE value.");
      str=cutoff(str,"[");
      if(str.startsWith("]"))
      { str=cutoff(str,"]");
        return ok(new TypeMapping(T.getDOM(), T.getCOD()), str);
      }
      ParseVALUEresult a1=parseVALUE(T.getDOM(),str);
        if(a1.getError()!=null) return a1;
      str=a1.getRest().trim();
      if(!str.startsWith("|->")) return error(str, "Expect a '|->' symbol between values.");
      str=cutoff(str, "|->");
      ParseVALUEresult b1=parseVALUE(T.getCOD(), str);
        if(b1.getError()!=null) return b1;
      str=b1.getRest().trim();
      TypeT va1=a1.getResult();
      TypeT vb1=b1.getResult();
      TypeMapping emptyMapping = new TypeMapping(T.getDOM(), T.getCOD());
      TypeMapping mapping = new TypeMapping(T.getDOM(), T.getCOD(), va1, vb1, emptyMapping);
      for(;!str.startsWith("]");)
      { if(!str.startsWith(",")) return error(str, "Expect a ',' between mapping pairs.");
        str=cutoff(str,",");
        ParseVALUEresult a=parseVALUE(T.getDOM(),str);
          if(a.getError()!=null) return a;
        str=a.getRest().trim();
          if(!str.startsWith("|->")) return error(str, "Expect a '|->' symbol between values.");
        str=cutoff(str, "|->");
        ParseVALUEresult b=parseVALUE(T.getCOD(), str);
          if(b.getError()!=null) return b;
        str=b.getRest().trim();
        mapping=mapping.ins(a.getResult(),b.getResult());
      }
      str=cutoff(str, "]");
      return ok(mapping, str);
    }
    else { return error(str, "There is no other TYPE anymore.");}    
  }         
}
