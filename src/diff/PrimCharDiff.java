package diff;

import value.PrimChar;
import sim.Sim;

import main.Main;

public class PrimCharDiff extends Diff 
{ private final PrimChar c1, c2;
  //private PartialSolution[] candidates;

  public PrimCharDiff(PrimChar c1, PrimChar c2)
  { this.c1=c1; this.c2=c2;
    //this.candidates = new PartialSolution[] { new PartialSolution(null)};
  }  
  public String toString(){ return (this.c1.equals(this.c2) ? "="+c1 : "-"+c1+", +"+c2)+this.getSim();}
  public Sim getSim(){ return (this.c1.equals(this.c2) ? Sim.EQUAL(2) : Sim.DIFF(2));}
  public boolean isFinal(){ return true;}
  public boolean refine(){System.out.println(this.toString()); return true;}
  /*    
  public String toString(){ return this.candidates[0].toString();}      
  //public String html(){ return ;}
  public Sim getSim(){ return this.candidates[0].getSim();}  
  public Sim getUnknown(){ return Sim.UNKNOWN(2);}

  public boolean isFinal(){ return this.candidates[0].getSim().isFinal();}
  public boolean refine()
  { if (Main.VERBOSE) System.out.println(this.candidates[0]);
    if (isFinal())
    { if(!Main.VERBOSE && Main.DIFF) 
        System.out.println(this.candidates[0]);
      if(Main.SIM) 
        System.out.println(this.candidates[0].getSim().getPercentage());
      return true;
    }    
    else
    { this.candidates = insertAll(this.candidates[0].expand(), this.deleteFirst(this.candidates));
      return false;
    }
  }
  private static PartialSolution[] deleteFirst(PartialSolution[] cands)
  { PartialSolution[] res = new PartialSolution[cands.length-1];
    System.arraycopy(cands, 1, res, 0, cands.length-1);
    return res;
  }
  // return a candidates list which is sorted descendingly by upper bound then lower bound of each candidate
  private static PartialSolution[] insertAll(PartialSolution[] newCands, PartialSolution[] cands)
  { Sim sim = newCands[0].getSim();
    PartialSolution[] res = new PartialSolution[cands.length+newCands.length];
    int i = 0;
    int j = 0;
    for(; i < cands.length && cands[i].getSim().compareTo(sim)>=0; i++)
      res[j++] = cands[i];
    for(int k =0; k < newCands.length; k++)
       res[j++] = newCands[k];
    for(; i < cands.length; i++)
      res[j++] = cands[i];
    return res;
  } 
  // inner class, i.e. it has implicit reference to PrimStringDiff i.e. a, b
  private class PartialSolution  
  { private final Trace trace;
    private PartialSolution(Trace trace){ this.trace = trace;}
    
    public String toString(){ return (trace == null ? "" : trace.toString())+getSim();}  
    //public String html(){ return (trace == null ? "" : trace.html());}
    public Sim getSim(){ return (trace == null ? Sim.UNKNOWN(2) : trace.getSim());}

    public PartialSolution copy()
    { EditOperation op = new Copy(PrimCharDiff.this.c1.getValue());
      Trace trace = new Trace(this.trace, op);
      return new PartialSolution(trace);
    }    
    public PartialSolution replace()
    { EditOperation op = new Replace(PrimCharDiff.this.c1.getValue(), PrimCharDiff.this.c2.getValue());
      Trace trace = new Trace(this.trace, op);
      return new PartialSolution(trace);
    }  
    private PartialSolution[] expand()
    { if(PrimCharDiff.this.c1.equals(PrimCharDiff.this.c2)) return new PartialSolution[]{ copy()};
      else return new PartialSolution[]{ replace()};
    }        
  }
   
  // a trace is a (reverse) List of Edit Operations applied to a specific position
  private class Trace
  { private final Trace trace;
    private final EditOperation op;
    private final Sim sim;
    
    public Trace(Trace trace, EditOperation op)
    { this.trace = trace;
      this.op = op;
      this.sim = op.calculate(trace == null ? PrimCharDiff.this.getUnknown() :  trace.getSim());
    }        
    public String toString(){ return (this.trace ==  null ? "" : this.trace.toString())+this.op;}
    //public String html(){ return(this.trace == null ? "" : this.trace.html())+this.op.html();}
    public Sim getSim(){ return this.sim;}
  }
 
  private abstract static class EditOperation
  { //abstract String html();
    abstract Sim calculate(Sim sim);
  }
  private final static class Copy extends EditOperation
  { private final char c;
    public Copy(char c){ this.c=c;}
    public String toString(){ return "="+c;}
    //public String html(){ return HTML.encode(c);}
    public Sim calculate(Sim sim){ return sim.inc(2);}
  } 
  private final static class Replace extends EditOperation
  { private final char c1, c2;
    public Replace(char c1, char c2){ this.c1=c1; this.c2=c2;}
    public String toString(){ return "-"+c1+", +"+c2;}
    public Sim calculate(Sim sim){ return sim.dec(2);}
  } 
  */
}
