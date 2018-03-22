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

  public TYPE typeOf(){ return this.T;}         
  public double getValue(){ return this.real;}   
  public int weight(){ return 1;}
}
