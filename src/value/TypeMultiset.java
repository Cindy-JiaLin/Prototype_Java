package value;

import java.util.List;
import java.util.ArrayList;

import type.TYPE;

// Use List<TypeT> to represent a Multiset
public class TypeMultiset extends TypeT
{ private final TYPE baseTYPE;
  private final List<TypeT> multiset;
  public TypeMultiset(TYPE baseTYPE)
  { this.baseTYPE=baseTYPE; this.multiset = new ArrayList();}
  public TypeMultiset(TYPE baseTYPE, List<TypeT> multiset)
  { this.baseTYPE = baseTYPE; this.multiset = multiset;}
  
  public String toString()
  { if(this.multiset.isEmpty()) return "<>";
    else
    { StringBuffer buf = new StringBuffer();
      buf.append("<");
      for(int i=0; i<this.multiset.size(); i++)
      { buf.append(this.multiset.get(i));
        if(i<this.multiset.size()-1) buf.append(",");
      }
      buf.append(">");
      return buf.toString();
    }
  }
  public boolean equals(Object obj)
  { if(obj instanceof TypeMultiset)
    { TypeMultiset that=(TypeMultiset)obj;
      return this.baseTYPE.equals(that.baseTYPE)&&
             this.multiset.equals(that.multiset);
    }
    else throw new RuntimeException("This obj="+obj+" is not an instance of TypeMultiset.");
  }
  
  public TYPE getBaseTYPE(){ return this.baseTYPE;}
  public List<TypeT> getValue(){ return this.multiset;}
  public int size(){ return this.multiset.size();} 
  public TypeT get(int i){ return this.multiset.get(i);}
  public boolean isEmptyMultiset(){ return this.multiset.isEmpty();}

  public TYPE typeOf(){ return TYPE.MSET(this.baseTYPE);} 
  public int weight()
  { int w = 0;
    if(this.multiset.isEmpty()) return 0;
    else 
    { for(int i=0; i<this.multiset.size(); i++)
         w = w+this.multiset.get(i).weight();
    }
    return w;
  }
  
  // put an element at the end of this multiset
  public TypeMultiset put(TypeT a)
  { List<TypeT> temp = this.multiset;
    if(a.typeOf().equals(this.baseTYPE))
    { temp.add(a);
      return new TypeMultiset(this.baseTYPE, temp);
    } 
    else throw new RuntimeException("TYPE of this element "+a+
                                    " is different from the expected type"+this.baseTYPE);
  } 
}
