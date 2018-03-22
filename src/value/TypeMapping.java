package value;

import type.TYPE;

public class TypeMapping extends TypeT
{ private final TYPE T1, T2;
  private final TypeT a, b;
  private final TypeMapping rest; 
  // empty constructor
  public TypeMapping(TYPE T1, TYPE T2)
  { this.T1=T1; this.T2=T2; this.a=null; this.b=null; this.rest=null;}
  // non-empty constructor
  public TypeMapping(TYPE T1, TYPE T2, TypeT a, TypeT b, TypeMapping rest)
  { if(!a.typeOf().equals(T1)||!b.typeOf().equals(T2)||
       !rest.typeOf().getDOM().equals(T1)||!rest.typeOf().getCOD().equals(T2))
    throw new RuntimeException("Elements TYPEs do not match the mapping TYPE");
    this.T1=T1; this.T2=T2; this.a=a; this.b=b; this.rest=rest;
  }  
  /*
  private static String NOLABEL="nolabel"; 
  private static TYPE BaseTYPE(TYPE t1, TYPE t2)
  { List<String> lols=new ArrayList<>();
    lols.add(NOLABEL); lols.add(NOLABEL);
    List<TypeT> lots=new ArrayList<>();
    lots.add(t1); lots.add(t2);
    return TYPE.PRODUCT(lols,lots);
  }*/

  public boolean isEmptyMapping(){ return this.a==null&&this.b==null&&this.rest==null;}

  public String toString()
  { StringBuffer buf=new StringBuffer();
    buf.append("[");
    if(!isEmptyMapping())dump(buf);
    buf.append("]"); 
    return buf.toString();
  }
  private void dump(StringBuffer buf)
  { buf.append(this.a); buf.append("|->"); buf.append(this.b);
    if(!rest.isEmptyMapping()){ buf.append(","); this.rest.dump(buf);}  
  }   
  public boolean equals(Object obj)
  { if(obj instanceof TypeMapping)
    { TypeMapping that=(TypeMapping)obj;
      if(this.isEmptyMapping()&&that.isEmptyMapping()) 
        return this.T1.equals(that.T1)&&this.T2.equals(that.T2);
      else if(!this.isEmptyMapping()&&!that.isEmptyMapping())
        return this.T1.equals(that.T1)&&this.T2.equals(that.T2)&&
               this.a.equals(that.a)&&this.b.equals(that.b)&&this.rest.equals(that.rest);
      else return false;
    }
    else throw new RuntimeException("This obj="+obj+
                                    " is not an instance of TypeMapping");
  }

  public TYPE getDomTYPE(){ return this.T1;}
  public TYPE getCodTYPE(){ return this.T2;}
  public TypeT getDomFst(){ return this.a;}
  public TypeT getCodFst(){ return this.b;}
  public TypeMapping getRest(){ return this.rest;}

  public boolean contains(TypeT x, TypeT y)
  { if(isEmptyMapping()) return false;
    else
    { if(this.a.equals(x)&&this.b.equals(b)) return true;
      else return this.rest.contains(x,y);
    }
  }
  public TYPE typeOf(){ return TYPE.MAPPING(this.T1, this.T2);}
  public int weight()
  { if(!isEmptyMapping()) return this.a.weight()+this.b.weight()+this.rest.weight();
    else return 0;
  }
  // ins x, y to the beginning of the mapping
  // mapping will be convert to set of pairs
  // the order of these pairs are not important.
  public TypeMapping ins(TypeT x, TypeT y)
  { if(x.typeOf().equals(this.T1) || y.typeOf().equals(this.T2))
    { if(this.isEmptyMapping()) 
        return new TypeMapping(this.T1, this.T2, x, y, new TypeMapping(this.T1, this.T2));
      else
      { if(this.a.equals(x)) return this;
        else return new TypeMapping(this.T1, this.T2, x, y, this);
      }
    }
    else throw new RuntimeException("TYPE of these elements are not matching TypeMapping");
  }
}
