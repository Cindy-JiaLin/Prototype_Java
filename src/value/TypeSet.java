package value;

import java.util.List;
import java.util.ArrayList;

import type.TYPE;

// Use List<TypeT> to represent a Set
// The add method must be carefully overrode 
public class TypeSet extends TypeT
{ private final TYPE baseTYPE;
  private final List<TypeT> set;
  public TypeSet(TYPE baseTYPE)
  { this.baseTYPE=baseTYPE; this.set = new ArrayList();}
  public TypeSet(TYPE baseTYPE, List<TypeT> set)
  { this.baseTYPE = baseTYPE; this.set = set;}
 
  public String toString()
  { if(this.set.isEmpty()) return "{}";
    else
    { StringBuffer buf = new StringBuffer();
      buf.append("{");
      for(int i=0; i<this.set.size(); i++)
      { buf.append(this.set.get(i));
        if(i<this.set.size()-1) buf.append(",");
        if(this.set.get(i).weight()>1) buf.append("\n");
      }
      buf.append("}");
      return buf.toString();
    }
  }
  public boolean equals(Object obj)
  { if(obj instanceof TypeSet)
    { TypeSet that=(TypeSet)obj;
      return this.baseTYPE.equals(that.baseTYPE)&&
             this.set.equals(that.set);
    }
    else throw new RuntimeException("This obj="+obj+" is not an instance of TypeSet.");
  }
  
  public TYPE getBaseTYPE(){ return this.baseTYPE;}

  public List<TypeT> getValue(){ return this.set;}
  public int size(){ return this.set.size();} 
  public TypeT get(int i){ return this.set.get(i);}

  public boolean isEmptySet(){ return this.set.isEmpty();}

  public TYPE typeOf(){ return TYPE.SET(this.baseTYPE);} 
  public int weight()
  { int w = 0;
    if(this.set.isEmpty()) return 0;
    else 
    { for(int i=0; i<this.set.size(); i++)
         w = w+this.set.get(i).weight();
    }
    return w;
  }
  
  // append an element at the end of this set
  public TypeSet add(TypeT a)
  { List<TypeT> temp = this.set;
    if(a.typeOf().equals(this.baseTYPE))
    { if(!temp.contains(a)) temp.add(a);
      return new TypeSet(this.baseTYPE, temp);
    } 
    else throw new RuntimeException("TYPE of this element "+a+
                                    " is different from the expected type"+this.baseTYPE);
  }
}
