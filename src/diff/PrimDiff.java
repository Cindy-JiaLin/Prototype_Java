package diff;

import sim.Sim;
import type.TYPE;

import value.TypeT;
import value.PrimUnit;
import value.PrimBool;
import value.PrimChar;
import value.PrimNat;
import value.PrimInt;
import value.PrimReal;


import dcprototype.Console;
import dcprototype.Encoding;
import dcprototype.HTML;
import dcprototype.Main;
import dcprototype.Options;

// Diff between PrimUnit, PrimBool, PrimChar, PrimNat, PrimInt
public class PrimDiff extends Diff 
{ private final TypeT a, b;
  private PartialSolution[] candidates;

  public PrimDiff(TypeT a, TypeT b)
  { this.a=a; this.b=b;
    this.candidates = new PartialSolution[] { new PartialSolution(null)};
  }  

  public Sim getUnknown(){ return Sim.UNKNOWN(2);}

  public String toString(){ return this.candidates[0].toString();}
  public String html(){ return this.candidates[0].html();}

  public Sim getSim(){ return this.candidates[0].getSim();}
  public PartialSolution getSolution(){ return this.candidates[0];}

  public boolean isFinal(){ return this.candidates[0].getSim().isFinal();}
  public boolean refine()
  { if (Main.VERBOSE) System.out.println(this.candidates[0]);
    if (isFinal()){ return true;}    
    else{ this.candidates = insertAll(this.candidates[0].expand(), this.deleteFirst(this.candidates));
          return false;
        }
  }
  
  // inner class, i.e. it has implicit reference to PrimDiff i.e. a, b
  private class PartialSolution  
  { private final Trace trace;
    private PartialSolution(Trace trace){ this.trace = trace;}
   
    public int getSource(){ return (this.trace == null ? 0 : trace.ia);}
    public int getTarget(){ return (this.trace == null ? 0 : trace.ib);}   
 
    public String toString(){ return (trace == null ? "" : trace.toString());}
    public String html(){ return (trace == null ? "" : trace.html());}
    public Sim getSim(){ return (trace == null ? Sim.UNKNOWN(2) : trace.getSim());}

    public PartialSolution copy()
    { EditOperation op = new Copy(PrimDiff.this.a);
      Trace trace = new Trace(this.trace, op);
      return new PartialSolution(trace);
    }
    public PartialSolution replace()// particular for UNIT, BOOL, CHAR, NAT, INT
    { EditOperation op = new Replace(PrimDiff.this.a, PrimDiff.this.b);
      Trace trace = new Trace(this.trace, op);
      return new PartialSolution(trace);
    }   
    public PartialSolution change()// particular for REAL
    { EditOperation op = new Change(PrimDiff.this.a, PrimDiff.this.b);
      Trace trace = new Trace(this.trace, op);
      return new PartialSolution(trace);
    }

    private PartialSolution[] expand()
    { if(PrimDiff.this.a.typeOf().isREAL())
      { PrimReal r1 = (PrimReal)PrimDiff.this.a;
        PrimReal r2 = (PrimReal)PrimDiff.this.b;
        if(r1.isSimilar(r2)) return new PartialSolution[]{ change()};
        else return new PartialSolution[]{ replace()};
      }
      else // for other primitive values, either equal or not.
      { if(PrimDiff.this.a.equals(PrimDiff.this.b))
          return new PartialSolution[]{ copy()};
        else return new PartialSolution[] { replace()};
      }
    }        
  }

  private static PartialSolution[] deleteFirst(PartialSolution[] cands)
  { PartialSolution[] res = new PartialSolution[cands.length-1];
    System.arraycopy(cands, 1, res, 0, cands.length-1);
    return res;
  }
  // return a candidates list which is sorted descendingly by upper bound then lower bound of each candidate
  private static PartialSolution[] insertAll(PartialSolution[] newCands, PartialSolution[] cands){ return newCands;}

  // a trace is a (reverse) List of Edit Operations applied to a specific position
  private class Trace
  { private final Trace trace;
    private final int ia, ib;
    private final EditOperation op;
    private final Sim sim;
    
    public Trace(Trace trace, EditOperation op)
    { this.trace = trace;
      this.op = op;
      this.ia = op.nextA(trace == null ? 0 : trace.ia);
      this.ib = op.nextB(trace == null ? 0 : trace.ib);
      this.sim = op.calculate(trace == null ? PrimDiff.this.getUnknown() :  trace.getSim());
    }        
    public String toString(){ return (this.trace ==  null ? "" : this.trace.toString())+this.op;}
    public String html(){ return(this.trace == null ? "" : this.trace.html())+this.op.html();}
    public Sim getSim(){ return this.sim;}
  }
 
  private abstract static class EditOperation
  { abstract String html();

    abstract Sim calculate(Sim sim);
    abstract int nextA(int ia);// return the position of the character in string a after a specific edit EditOperation
    abstract int nextB(int ib);// return the position of the character in string b after a specific edit EditOperation
  }
  private final static class Copy extends EditOperation
  { private final TypeT c;
    public Copy(TypeT c){ this.c=c;}
    public String toString(){ return ""+c;}
    public String html(){ return HTML.CPY(""+c);}

    public Sim calculate(Sim sim){ return sim.inc(2);}
    public int nextA(int ia){ return ia+1;}
    public int nextB(int ib){ return ib+1;}
  }
  private final static class Replace extends EditOperation
  { private final TypeT a, b;
    public Replace(TypeT a, TypeT b){ this.a=a; this.b=b;}
    public String toString(){ return Console.del(""+a)+Console.ins(""+b);}
    public String html(){ return HTML.DEL(""+a)+HTML.INS(""+b);}

    public Sim calculate(Sim sim){ return sim.dec(1).dec(1);}
    public int nextA(int ia){ return ia+1;}
    public int nextB(int ib){ return ib+1;}
  }
  private final static class Change extends EditOperation
  { private final TypeT a, b;
    public Change(TypeT a, TypeT b){ this.a=a; this.b=b;}
    public String toString(){ return Console.chg(""+a+Encoding.APPROX+b);}
    public String html(){ return HTML.CHG("("+a+","+b+")");}

    public Sim calculate(Sim sim){ return sim.dec(1).dec(1);}
    public int nextA(int ia){ return ia+1;}
    public int nextB(int ib){ return ib+1;}
  }
}
