package value;

import java.util.List;

import type.TYPE;

public class TypeProduct extends TypeT
{ private final TYPE T;
  private final List<String> labels;
  private final List<TypeT> values;
  public TypeProduct(TYPE T, List<String> labels, List<TypeT> values) 
  { if(!T.isPRODUCT())
      throw new RuntimeException("This TYPE T="+T+" is not a product type.");
    if(!T.hasSameLabels(labels))
      throw new RuntimeException("This TYPE T="+T+" does not contain the same labels="+labels);
    if(!T.hasSameTYPEs(values))
    { throw new RuntimeException("This TYPE T="+T+" does not match the value TYPEs="+values);}
    if(labels.size()==0 || values.size()==0)
    { throw new RuntimeException("There must be at least one typed value in this TypeProduct value.");}
    if(labels.size()!=values.size()) 
    { throw new RuntimeException("The size of labels and values are not matching.");}
    this.T=T; this.labels=labels; this.values=values;
  }
  public TYPE typeOf(){ return this.T;}  
  public List<String> getLabels(){ return this.labels;}
  public List<TypeT> getValues(){ return this.values;}        
  public int size(){ return this.values.size();}
 
  private static String NOLABEL="nolabel";
  public String toString()
  { StringBuffer buf = new StringBuffer();
    buf.append("(");
    for(int i=0; i<this.values.size(); i++)
    { if(!this.labels.get(i).equals(NOLABEL))
      { buf.append(this.labels.get(i)); buf.append(".");}
      buf.append(this.values.get(i));
      if(i<this.values.size()-1) buf.append(",");
    }
    buf.append(")");
    return buf.toString();
  }
  public boolean equals(Object obj)
  { if(obj instanceof TypeProduct)
    { TypeProduct that=(TypeProduct)obj;
      return this.T.equals(that.T)&&
             this.labels.equals(that.labels)&&
             this.values.equals(that.values);
    }
    else { throw new RuntimeException("This obj="+obj+" is not of TypeProduct");}
  }
  public int weight()
  { int weight=0;
    for(int i=0; i<this.values.size(); i++)
      weight+=this.values.get(i).weight();
    return weight;
  }              
}
