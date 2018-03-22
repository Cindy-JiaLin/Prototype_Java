package value;

import java.util.List;
import java.util.ArrayList;

import type.TYPE;

public class TypeList extends TypeT
{ private final TYPE baseTYPE;
  private final List<TypeT> lst;
  public TypeList(TYPE baseTYPE)
  { this.baseTYPE=baseTYPE; this.lst = new ArrayList();}
  public TypeList(TYPE baseTYPE, List<TypeT> lst)
  { this.baseTYPE = baseTYPE; this.lst = lst;}
  
  public String toString()
  { if(this.lst.isEmpty()) return "[]";
    else
    { StringBuffer buf = new StringBuffer();
      buf.append("[");
      for(int i=0; i<this.lst.size(); i++)
      { buf.append(this.lst.get(i));
        if(i<this.lst.size()-1) buf.append(",");
        if(this.lst.get(i).weight()>1) buf.append("\n");
      }
      buf.append("]");
      return buf.toString();
    }
  }
  public boolean equals(Object obj)
  { if(obj instanceof TypeList)
    { TypeList that=(TypeList)obj;
      return this.baseTYPE.equals(that.baseTYPE)&&
             this.lst.equals(that.lst);
    }
    else throw new RuntimeException("This obj="+obj+" is not an instance of TypeList."); 
  }
  
  public TYPE getBaseTYPE(){ return this.baseTYPE;}
  public List<TypeT> getValue(){ return this.lst;}
  public int size(){ return this.lst.size();} 
  public TypeT get(int i){ return this.lst.get(i);}
  public boolean isEmptyList(){ return this.lst.isEmpty();}

  public TYPE typeOf(){ return TYPE.LIST(this.baseTYPE);} 
  public int weight()
  { int w = 0;
    if(this.lst.isEmpty()) return 0;
    else 
    { for(int i=0; i<this.lst.size(); i++)
         w = w+this.lst.get(i).weight();
    }
    return w;
  }
  
  // append an element at the end of this list
  public TypeList append(TypeT a)
  { List<TypeT> temp = this.lst;
    if(a.typeOf().equals(this.baseTYPE))
    { temp.add(a);
      return new TypeList(this.baseTYPE, temp);
    } 
    else throw new RuntimeException("TYPE of this element "+a+" is different from the expected type"+this.baseTYPE);
  } 
}
