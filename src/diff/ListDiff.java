package diff;

import java.io.FileWriter;
import java.io.IOException;

import java.util.Arrays;
import java.util.Comparator;

import sim.Sim;
import main.HTML;
import main.Options;

import type.TYPE;

import value.TypeT;
import value.PrimChar;
import value.PrimString;
import value.TypeList;

import main.Main;

public class ListDiff extends Diff 
{ private final TypeList a, b;
  private PartialSolution[] candidates;
  
  public ListDiff(TypeList a, TypeList b)
  { this.a=a; this.b=b;
    this.candidates = new PartialSolution[] { new PartialSolution(null)};
  }        
  
  public Sim getUnknown(){ return Sim.UNKNOWN(this.a.weight()+this.b.weight());} 

  public String toString(){ return this.candidates[0].toString();}
  //public String html(){ return this.candidates[0].html();}
  public Sim getSim(){ return this.candidates[0].getSim();}  

  public boolean isFinal(){ return this.candidates[0].getSim().isFinal();}

  public boolean refine()
  { if (Main.VERBOSE) 
    { //System.out.println(this.candidates[0]);
      for(int i=0; i<this.candidates.length; i++)
        System.out.println(""+i+": "+this.candidates[i].getSim()+this.candidates[i]);
      System.out.println();
    }
    // the initial state, the trace is null, outside refine is needed
    if(this.candidates[0].trace==null)
    { this.candidates = insertAll(this.candidates[0].expand(), this.deleteFirst(this.candidates));
      Arrays.sort(this.candidates, simComparator);
      return false;
    }
    // the intermediate state, the trace is not null
    // this.candidates[0].refine() is determined by the trace.refine()
    // if the current EditOperation is Change, the trace.refine() return false
    // which means one more inside refine step needs to do
    else if(!this.candidates[0].refine())
    { Arrays.sort(this.candidates, simComparator);// sort the candidates after each inside refine step
      return false;
    }
    else if (isFinal())
    { if(!Main.VERBOSE && Main.DIFF) 
      System.out.println(this.candidates[0]);
      //if(HTMLCODE) writeHTML(this.candidates[0]);
      if(Main.SIM) 
      System.out.println(this.candidates[0].getSim().getPercentage());
      return true;
    }    
    else// when each edit operation has been completely refined, expand one more step
    { this.candidates = insertAll(this.candidates[0].expand(), this.deleteFirst(this.candidates));
      Arrays.sort(this.candidates, simComparator);
      return false;
    }
  }  
  private final static SimComparator simComparator = new SimComparator();
  private static class SimComparator implements Comparator<PartialSolution>
  { public int compare(PartialSolution sol1, PartialSolution sol2)
    { if(sol1.getSim()==null) return (sol2.getSim()==null ? 0 : 1);
      else return -sol1.getSim().compareTo(sol2.getSim());
      //reverse order, Array.sort is ascending, we need descending
    }
  }
   
  private static PartialSolution[] deleteFirst(PartialSolution[] cands)
  { PartialSolution[] res = new PartialSolution[cands.length-1];
    System.arraycopy(cands, 1, res, 0, cands.length-1);
    return res;
  }
  // insert the nonRedundant newCands to the front of the current cands
  // this method will not return a sorted candidates list
  // this candidates list will be sorted by Array.sort(this.candidates, simComparator)
  private static PartialSolution[] insertAll(PartialSolution[] newCands, PartialSolution[] cands)
  { int nonRedundant = 0;
    // surpress redundant new candidates
    for(int k = 0; k < newCands.length; k++)
    { if(newCands[k].isRedundant(cands)) newCands[k]=null;
      else nonRedundant++;
    }
    if(nonRedundant == 0) return cands;
    PartialSolution[] res = new PartialSolution[cands.length+nonRedundant];
    int j = 0;
    for(int k =0; k < newCands.length; k++)
      if(newCands[k]!=null) res[j++] = newCands[k];
    for(int i=0; i < cands.length; i++)
      res[j++] = cands[i];
    return res;
  }
 
  // inner class, i.e. it has implicit reference to ListDiff i.e. a, b
  private class PartialSolution  
  { private final Trace trace;
    private PartialSolution(Trace trace){ this.trace = trace;}
   
    public int getSource(){ return (this.trace == null ? 0 : trace.ia);}
    public int getTarget(){ return (this.trace == null ? 0 : trace.ib);}
 
    public String toString(){ return "["+(trace == null ? "" : trace.toString())+"]"+getSim();}  
    //public String html(){ return HTML.TABLE(trace.html()+(SIM ? HTML.TD2(HTML.CHG,getSim().getPercentage()):""));}
    public Sim getSim(){ return (trace == null ? Sim.UNKNOWN(ListDiff.this.a.size()+ListDiff.this.b.size()) : trace.getSim());}
        
    public boolean refine(){ if(trace==null) return false; else return trace.refine();}        
    
    public PartialSolution delete()
    { EditOperation op = new Delete(ListDiff.this.a.get(getSource()));
      Trace trace = new Trace(this.trace, op);
      return new PartialSolution(trace);
    }        
    public PartialSolution insert()
    { EditOperation op = new Insert(ListDiff.this.b.get(getTarget()));
      Trace trace = new Trace(this.trace, op);
      return new PartialSolution(trace);
    }        
    public PartialSolution copy()
    { EditOperation op = new Copy(ListDiff.this.b.get(getTarget()));
      Trace trace = new Trace(this.trace, op);
      return new PartialSolution(trace);
    }      
    public PartialSolution change()
    { EditOperation op;
      Trace trace;
      TYPE baseTYPE = a.getBaseTYPE();
      if(baseTYPE.isCHAR()) 
      { op= new Change(new PrimCharDiff((PrimChar)ListDiff.this.a.get(getSource()), (PrimChar)ListDiff.this.b.get(getTarget())));
        trace = new Trace(this.trace, op);
        return new PartialSolution(trace);
      }
      else if(baseTYPE.isSTRING())
      { op= new Change(new PrimStringDiff((PrimString)ListDiff.this.a.get(getSource()), (PrimString)ListDiff.this.b.get(getTarget())));
        trace = new Trace(this.trace, op);
        return new PartialSolution(trace);
      }
      else//(baseTYPE.isLIST())
      { op= new Change(new ListDiff((TypeList)ListDiff.this.a.get(getSource()), (TypeList)ListDiff.this.b.get(getTarget())));
        trace = new Trace(this.trace, op);
        return new PartialSolution(trace);
      }
    }        
    private PartialSolution[] expand()
    { if(ListDiff.this.b.size() ==  getTarget())
      { if(ListDiff.this.a.size() == getSource()) return new PartialSolution[0];
        else return new PartialSolution[]{ delete()};
      }
      else if(ListDiff.this.a.size() == getSource()) 
             return new PartialSolution[]{ insert()};
      // both are non-empty lists
      else if(ListDiff.this.a.get(getSource()).equals(ListDiff.this.b.get(getTarget()))) 
             return new PartialSolution[]{ copy()};
      else if(ListDiff.this.a.get(getSource()).weight()==0) return new PartialSolution[]{ delete(), insert()};// delete an empty line 
      else if(ListDiff.this.b.get(getTarget()).weight()==0) return new PartialSolution[]{ delete(), insert()};// insert an empty line
      else return new PartialSolution[]{ change(), insert(), delete()};
    }        
    
    // Get the last copy operation position
    public int getStopper(Trace trace)
    { if(trace == null) return -1;
      else if(trace.op instanceof Copy) return trace.ia;
      else return getStopper(trace.trace);
    }  
    // Get the last delete operation position
    public int getLastDelete(Trace trace)
    { if(trace == null) return -1;
      else if(trace.op instanceof Copy) return -1;
      else if(trace.op instanceof Delete) return trace.ia;
      else return getLastDelete(trace.trace);
    } 
    // Get the last insert operation position
    public int getLastInsert(Trace trace)
    { if(trace == null) return -1;
      else if(trace.op instanceof Copy) return -1;
      else if(trace.op instanceof Insert) return trace.ib;
      else return getLastInsert(trace.trace);
    }
    public boolean isRedundant(PartialSolution[] active) 
    { int stopper = getStopper(this.trace);
      int lastDelete = getLastDelete(this.trace);
      int lastInsert = getLastInsert(this.trace);
      for(int i = 0; i < active.length; i++)
      { Trace t = active[i].trace;
        if(stopper == getStopper(t) && lastDelete == getLastDelete(t) && lastInsert == getLastInsert(t))
          return true;
      }    
      return false;
    }      
  } 
  // a trace is a (reverse) List of Edit Operations appplied to a specific position
  private class Trace
  { private final Trace trace;
    private final int ia, ib;
    private final EditOperation op;
    private Sim sim;
    
    public Trace(Trace trace, EditOperation op)
    { this.trace = trace;
      this.op = op;
      this.ia = op.nextA(trace == null ? 0 : trace.ia);
      this.ib = op.nextB(trace == null ? 0 : trace.ib);
      this.sim = op.calculate(trace == null ? ListDiff.this.getUnknown() :  trace.getSim());
    }        
    public String toString(){ return (this.trace ==  null ? "" : this.trace.toString())+this.op;}
    //public String html(){ return(this.trace != null ? this.trace.html() : "")+HTML.TR(op.html(ia,ib));}
    public Sim getSim(){ return this.sim;}

    public boolean refine()
    { if(this.op.refine()) return true;
      this.sim=this.op.calculate(trace==null ? ListDiff.this.getUnknown() : trace.getSim());
      return false;
    }
  }
 
  private abstract static class EditOperation
  { //abstract String html(int ia, int ib);

    abstract Sim calculate(Sim sim);
    abstract boolean refine();// return true if there was a refinement possible, false otherwise
    abstract int nextA(int ia);// return the position of the character in string a after a specific edit EditOperation
    abstract int nextB(int ib);// return the position of the character in string b after a specific edit EditOperation
  }
  private final static class Insert extends EditOperation
  { private final TypeT c;
    public Insert(TypeT c){ this.c=c;}
    public String toString(){ return "+"+c;}
    //public String html(int ia, int ib)
    //{ return HTML.TD("")+HTML.TD(HTML.INS,ib)+(SIM ? HTML.TD("") : "")+HTML.TD(HTML.INS, HTML.encode(c));}

    public Sim calculate(Sim sim){ return sim.dec(c.weight());}
    public boolean refine(){ return true;}
    public int nextA(int ia){ return ia;}
    public int nextB(int ib){ return ib+1;}
  }
  private final static class Delete extends EditOperation
  { private final TypeT c;
    public Delete(TypeT c){ this.c=c;}
    public String toString(){ return "-"+c;}
    //public String html(int ia, int ib)
    //{ return HTML.TD("")+HTML.TD(HTML.DEL,ia)+(SIM ? HTML.TD("") : "")+HTML.TD(HTML.DEL, HTML.encode(c));}

    public Sim calculate(Sim sim){ return sim.dec(c.weight());}
    public boolean refine(){ return true;}
    public int nextA(int ia){ return ia+1;}
    public int nextB(int ib){ return ib;}
  }
  private final static class Copy extends EditOperation
  { private final TypeT c;
    public Copy(TypeT c){ this.c=c;}
    public String toString(){ return "="+c;}
    //public String html(int ia, int ib)
    //{ return HTML.TD(HTML.CPY,ia)+HTML.TD(HTML.CPY,ib)+(SIM ? HTML.TD("") : "")+HTML.TD(HTML.CPY, HTML.encode(c));}

    public Sim calculate(Sim sim){ return sim.inc(2*c.weight());}
    public boolean refine(){ return true;}
    public int nextA(int ia){ return ia+1;}
    public int nextB(int ib){ return ib+1;}
  }
  private final static class Change extends EditOperation
  { private final Diff diff;
    public Change(Diff diff){ this.diff=diff;}
    public String toString(){ return "!"+diff;}
    //public String html(int ia, int ib)
    //{ return HTML.TD(HTML.CHG,ia)+HTML.TD(HTML.CHG,ib)+(SIM ? HTML.TD(""+diff.getSim().getPercentage1()+" ") : "")+HTML.TD(HTML.CHG, diff.html());}

    public Sim calculate(Sim sim)
    { return sim.inc(this.diff.getSim().getIncrement()).dec(this.diff.getSim().getDecrement());}
    public boolean refine(){ return diff.refine();}
    public int nextA(int ia){ return ia+1;}
    public int nextB(int ib){ return ib+1;}
  }
}
