package value;

import type.TYPE;

public class PrimBool extends TypeT
{ private final boolean t;
  public PrimBool(TYPE T, boolean t)
  { if(!T.equals(TYPE.BOOL)) throw new RuntimeException("PrimBool must be of TYPE.BOOL.");
    this.t=t;
  }
  public String toString(){ return ""+this.t;}  

  public boolean equals(Object obj)
  { if(obj instanceof PrimBool){ PrimBool that=(PrimBool)obj; return this.t==that.t;}
    else { throw new RuntimeException("This obj="+obj+" is not of PrimBool");} 
  }
  
  public TYPE typeOf(){ return TYPE.BOOL;}         
  public boolean getValue(){ return this.t;}   
  public int weight(){ return 1;}
}
