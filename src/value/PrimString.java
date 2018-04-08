package value;

import type.TYPE;

public class PrimString extends TypeT
{ private final String str;
  public PrimString(TYPE T, String str)
  { if(!T.equals(TYPE.STRING)) throw new RuntimeException("TypeString must be of TYPE.STRING.");
    this.str=str;
  }
  public String toString(){return "\""+this.str+"\"";}    
  public boolean equals(Object obj)
  { if(obj instanceof PrimString)
    { PrimString that=(PrimString)obj; return this.str.equals(that.str);}
    else { throw new RuntimeException("This obj="+obj+" is not of TypeString");} 
  } 
 
  public char charAt(int i){ return this.str.charAt(i);}
  public String substring(int i){ return this.str.substring(i);}
 
  public TYPE typeOf(){ return TYPE.STRING;}         
  public String getValue(){ return this.str;}  
  public int weight(){ return this.str.length();}
}
  
  
