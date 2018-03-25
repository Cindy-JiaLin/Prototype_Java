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
  public int weight(){ return 1;}
  
  public double getAcc(){ return this.T.getAcc();}         
  public double getValue(){ return this.real;}   
  public boolean hasSameAcc(PrimReal that){ return this.typeOf().equals(that.typeOf());}

  public boolean isSimilar(PrimReal that){ return Math.abs(this.real-that.real)<this.getAcc();}
}
