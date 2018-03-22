package value;

import type.TYPE;

public class PrimNat extends TypeT
{ private final int n;
  public PrimNat(TYPE T, int n)
  { if(!T.equals(TYPE.NAT)) throw new RuntimeException("PrimNat must be of TYPE.NAT.");
    this.n=n;
  }
  public String toString(){ return ""+this.n;}  

  public boolean equals(Object obj)
  { if(obj instanceof PrimNat){ PrimNat that=(PrimNat)obj; return this.n==that.n;}
    else { throw new RuntimeException("This obj="+obj+" is not of PrimNat");} 
  }
  
  public TYPE typeOf(){ return TYPE.NAT;}         
  public int getValue(){ return this.n;}   
  public int weight(){ return 1;}
}
