package value;

import type.TYPE;

public class PrimUnit extends TypeT
{ private static final PrimUnit unit=new PrimUnit(TYPE.UNIT);
  public PrimUnit(TYPE T)
  { if(!T.equals(TYPE.UNIT)) throw new RuntimeException("PrimUnit must be of TYPE.UNIT.");}
  public String toString(){ return "unit";}  

  public boolean equals(Object obj){ return obj instanceof PrimUnit;}

  public TYPE typeOf(){ return TYPE.UNIT;}         
  public PrimUnit getValue(){ return this.unit;}   
  public int weight(){ return 1;}
}
