package value;

import type.TYPE;

public class PrimReal extends TypeT
{ private final TYPE T;
  private final double real;
  public PrimReal(TYPE T, double real)
  { if(!T.isREAL()) throw new RuntimeException("PrimReal must be of TYPE.REAL.");
    this.T=T; this.real=real;
  }
  public String toString(){ return ""+this.real;}  

  public boolean equals(Object obj)
  { if(obj instanceof PrimReal)
    { PrimReal that=(PrimReal)obj; 
      return this.T.equals(that.T) && this.real==that.real;
    }
    else { throw new RuntimeException("This obj="+obj+" is not of PrimReal");} 
  }
  
  public TYPE typeOf(){ return this.T;}         
  public double getValue(){ return this.real;}   
  public int weight(){ return 1;}
}
