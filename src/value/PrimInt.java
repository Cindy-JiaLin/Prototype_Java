package value;

import type.TYPE;

public class PrimInt extends TypeT
{ private final int n;
  public PrimInt(TYPE T, int n)
  { if(!T.equals(TYPE.INT)) throw new RuntimeException("PrimInt must be of TYPE.INT.");
    this.n=n;
  }
  public String toString(){ return ""+this.n;}  

  public boolean equals(Object obj)
  { if(obj instanceof PrimInt){ PrimInt that=(PrimInt)obj; return this.n==that.n;}
    else { throw new RuntimeException("This obj="+obj+" is not of PrimInt");} 
  }
  
  public TYPE typeOf(){ return TYPE.INT;}         
  public int getValue(){ return this.n;}   
  public int weight(){ return 1;}
}
