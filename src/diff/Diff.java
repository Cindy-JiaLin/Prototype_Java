package diff; 

import sim.Sim;
import type.TYPE;
import value.*;


public abstract class Diff 
{ public abstract boolean isFinal();
  public abstract boolean refine();
  public abstract Sim getSim();
 
  public abstract Sim getUnknown();
  // a factory method
  public static Diff newDiff(TYPE T, TypeT source, TypeT target)
  { if (!source.typeOf().equals(T)) throw new RuntimeException("Type Mismatch: "+source+" not of expected type: "+T);
    else if (!target.typeOf().equals(T)) throw new RuntimeException("Type Mismatch: "+target+" not of expected type: "+T);
    else if (T.isPRIMITIVE()||T.isREAL()) return new PrimDiff(source, target);
    else if (T.isSTRING()) return new PrimStringDiff((PrimString)source, (PrimString)target);
    else if (T.isPRODUCT()) return new ProductDiff((TypeProduct)source, (TypeProduct)target);
    else if (T.isUNION()) return new UnionDiff((TypeUnion)source, (TypeUnion)target);
    else if (T.isLIST()) return new ListDiff((TypeList)source, (TypeList)target);
    else if (T.isSET()) return new SetDiff((TypeSet)source, (TypeSet)target);
    else throw new RuntimeException("Diff not yet implemented for type: "+T);
  }
}
