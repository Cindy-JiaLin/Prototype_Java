package value;

import type.TYPE;

//There is no value represented by null
public class TypeUnion extends TypeT 
{ private final TYPE T;
  private final String label;
  private final TypeT value;
  public TypeUnion(TYPE T, String label, TypeT value)
  { if(!T.isUNION()) 
      throw new RuntimeException("This TYPE T="+T+" is not a UNION type.");
    if(!T.getLabels().contains(label))
      throw new RuntimeException("This TYPE T="+T+" does not contain this label="+label);
    if(!T.getTYPEs().contains(value.typeOf()))
      throw new RuntimeException("This TYPE T="+T+" does not contain this TYPE value="+value);
    this.T=T; this.label=label; this.value=value;
  }
  public String toString(){ return this.label+"."+this.value;}
  public String beautify()
  { return (value.typeOf().isUNIT()) ? this.label : this.label+"."+this.value;}
  public boolean equals(Object obj)
  { if(obj instanceof TypeUnion)
    { TypeUnion that=(TypeUnion)obj;
      return this.T.equals(that.T)&&this.label.equals(that.label)&&
             this.value.equals(that.value);
    }
    else { throw new RuntimeException("This obj="+obj+" is not of TypeUnion");}
  }
  public TYPE typeOf(){ return this.T;}
  public int weight(){ return this.value.weight();}

  public String getLabel(){ return this.label;}
  public TypeT getValue(){ return this.value;}
}
