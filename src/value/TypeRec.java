package value;

import type.TYPE;
public class TypeRec extends TypeT
{ private final TYPE T;
  private final TypeT typeBody;
  public TypeRec(TYPE T, TypeT typeBody)
  { // In the constructor of TypeRec, the TYPE of typeBody has already been UNION of PRODUCT TYPE.
    // This case has been considered in the equals method in TYPE class.
    if(!T.isREC()||!T.unfold(T).equals(typeBody.typeOf()))
    { throw new RuntimeException("This recursive type does not match its TYPE=\n"+T);}
    this.T=T; this.typeBody=typeBody;
  }
  public String toString(){ return ""+typeBody;} 
  public boolean equals(Object obj)
  { if(obj instanceof TypeRec)
    { TypeRec that=(TypeRec)obj;
      return this.T.equals(that.T)&&this.typeBody.equals(that.typeBody);
    }
    else { throw new RuntimeException("This obj="+obj+" is not instance of TypeRecursive.");}
  }          
  
  public TypeT getBody(){ return this.typeBody;}
  public TYPE typeOf() { return this.T;}         
  public int weight() { return this.typeBody.weight();}
}
