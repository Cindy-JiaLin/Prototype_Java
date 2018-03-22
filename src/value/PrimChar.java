package value;

import type.TYPE;

public class PrimChar extends TypeT
{ private final char c;
  public PrimChar(TYPE T, char c)
  { if(!T.equals(TYPE.CHAR)) throw new RuntimeException("PrimChar must be of TYPE.CHAR.");
    this.c=c;
  }
  public String toString(){ return ""+this.c;}  

  public boolean equals(Object obj)
  { if(obj instanceof PrimChar){ PrimChar that=(PrimChar)obj; return this.c==that.c;}
    else { throw new RuntimeException("This obj="+obj+" is not of PrimChar");} 
  }
  
  public TYPE typeOf(){ return TYPE.CHAR;}         
  public char getValue(){ return this.c;}   
  public int weight(){ return 1;}
}
