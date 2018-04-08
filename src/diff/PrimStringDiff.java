package diff;

import sim.Sim;
import dcprototype.Console;
import dcprototype.HTML;
import dcprototype.Main;
import dcprototype.Options;

import type.TYPE;
import value.TypeT;
import value.PrimString;

public class PrimStringDiff extends Diff 
{ private final PrimString a, b;
  private PartialSolution[] candidates;
  
  public PrimStringDiff(PrimString a, PrimString b)
  { this.a=a; this.b=b;
    this.candidates = new PartialSolution[] { new PartialSolution(null)};
  }        
  public TypeT getSourceValue(){ return this.a;}
  public TypeT getTargetValue(){ return this.b;}
 
  public String toString(){ return this.candidates[0].toString();}
  public String html(){ return this.candidates[0].html();}
  public Sim getSim(){ return this.candidates[0].getSim();}  
  public PartialSolution getSolution(){ return this.candidates[0];}     
  
  public Sim getUnknown(){ return Sim.UNKNOWN(this.a.weight()+this.b.weight());}
  
  // lwb==upb
  public boolean isFinal(){ return this.candidates[0].getSim().isFinal();}
  public boolean refine()
  { if (Main.VERBOSE || VERBOSE)
    { for(int i=0; i<this.candidates.length; i++)
       System.out.println("["+i+"]"+this.candidates[i]);
      System.out.println("\n"); 
    }
    // System.out.println(this.candidates[0]);
    if (isFinal()){ return true;}    
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
  { int nonRedundant = 0;
    Sim sim = null;
    // surpress redundant new candidates
    for(int k = 0; k < newCands.length; k++)
    { if(newCands[k].isRedundant(cands)) newCands[k]=null;
      else
      { sim = newCands[k].getSim();
        // newCands are generated from expand method, the length of newCands either one or two
        // when newCands contain two members, insert() and delete() have the same similarity
        // since the insert a character and delete a character have the same affect on the
        // current similarity.
        nonRedundant++;
      }   
    }
    if(nonRedundant == 0) return cands;
    PartialSolution[] res = new PartialSolution[cands.length+nonRedundant];
    int i = 0;
    int j = 0;
    for(; i < cands.length && cands[i].getSim().compareTo(sim)>=0; i++)
      res[j++] = cands[i];
    for(int k =0; k < newCands.length; k++)
      if(newCands[k]!=null) res[j++] = newCands[k];
    for(; i < cands.length; i++)
      res[j++] = cands[i];
    return res;
  } 
  // inner class, i.e. it has implicit reference to PrimStringDiff i.e. a, b
  private class PartialSolution  
  { private final Trace trace;
    private PartialSolution(Trace trace){ this.trace = trace;}
   
    public int getSource(){ return (this.trace == null ? 0 : trace.ia);}
    public int getTarget(){ return (this.trace == null ? 0 : trace.ib);}   
 
    public String toString(){ return Console.cpy("\"")+(trace == null ? "" : trace.toString())+Console.cpy("\"");}
    //{ return "\""+(trace == null ? "" : trace.toString())+"?"+PrimStringDiff.this.a.substring(getSource())+"?"+PrimStringDiff.this.b.substring(getTarget())+"\""+getSim();}
    public String beautify(){ return (trace == null ? "" : trace.toString());}
    public String html(){ return HTML.TABLE("\""+(trace == null ? "" : trace.html())+"\"");}
    public Sim getSim()
    { return (trace == null ? Sim.UNKNOWN(PrimStringDiff.this.a.weight()+PrimStringDiff.this.b.weight()) : trace.getSim());}

    public PartialSolution delete()
    { EditOperation op = new Delete(PrimStringDiff.this.a.charAt(getSource()));
      Trace trace = new Trace(this.trace, op);
      return new PartialSolution(trace);
    }        
    public PartialSolution insert()
    { EditOperation op = new Insert(PrimStringDiff.this.b.charAt(getTarget()));
      Trace trace = new Trace(this.trace, op);
      return new PartialSolution(trace);
    }        
    public PartialSolution copy()
    { EditOperation op = new Copy(PrimStringDiff.this.a.charAt(getSource()));
      Trace trace = new Trace(this.trace, op);
      return new PartialSolution(trace);
    }      
    private PartialSolution[] expand()
    { if(PrimStringDiff.this.b.weight() ==  getTarget())
      { if(PrimStringDiff.this.a.weight() == getSource()) return new PartialSolution[0];
        else return new PartialSolution[]{delete()};
      }
      else if(PrimStringDiff.this.a.weight() == getSource()) 
             return new PartialSolution[]{insert()};
      else if(PrimStringDiff.this.a.charAt(getSource()) == PrimStringDiff.this.b.charAt(getTarget())) 
             return new PartialSolution[]{copy()};
      else return new PartialSolution[]{ insert(), delete()};
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
    // let x, y be two characters -x, +y and +y, -x is the same partial solution
    // this isRedundant method is used to pick out such partial solution
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
      this.sim = op.calculate(trace == null ? PrimStringDiff.this.getUnknown() :  trace.getSim());
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
  private final static class Insert extends EditOperation
  { private final char c;
    public Insert(char c){ this.c=c;}
    public String toString(){ return Console.ins(""+c);}
    public String html(){ return HTML.INS(c);}

    public Sim calculate(Sim sim){ return sim.dec(1);}
    public int nextA(int ia){ return ia;}
    public int nextB(int ib){ return ib+1;}
  }
  private final static class Delete extends EditOperation
  { private final char c;
    public Delete(char c){ this.c=c;}
    public String toString(){ return Console.del(""+c);}
    public String html(){ return HTML.DEL(c);}

    public Sim calculate(Sim sim){ return sim.dec(1);}
    public int nextA(int ia){ return ia+1;}
    public int nextB(int ib){ return ib;}
  }
  private final static class Copy extends EditOperation
  { private final char c;
    public Copy(char c){ this.c=c;}
    public String toString(){ return Console.cpy(""+c);}
    public String html(){ return HTML.CPY(c);}

    public Sim calculate(Sim sim){ return sim.inc(2);}
    public int nextA(int ia){ return ia+1;}
    public int nextB(int ib){ return ib+1;}
  }

  public static boolean VERBOSE = false;// if true all intermediate states will be logged
  public static boolean SIM = false;// displays similarity as percentage
  public static boolean DIFF = false;// displays difference as a solution (PartialSolution) 
  public static boolean INFO = false;// displays runtime statistics
        
  public static void main(String[] args) 
  { final long startTime = System.currentTimeMillis();
    if(Options.isSet(args, "-verbose")) { VERBOSE = true; args = Options.remove(args, "-verbose");}
    if(Options.isSet(args, "-sim")) { SIM = true; args = Options.remove(args, "-sim");}
    if(Options.isSet(args, "-diff")) { DIFF = true; args = Options.remove(args, "-diff");}
    if(Options.isSet(args, "-info")) { INFO = true; args = Options.remove(args, "-info");}
    
    String sourceFileName = Options.getOption(args, "-source");// get the arg after the -source
    if(sourceFileName!=null) args = Options.remove(args, "-source", sourceFileName);
      
    String targetFileName = Options.getOption(args, "-target");// get the arg after the -target
    if(targetFileName!=null) args = Options.remove(args, "-target", targetFileName);
      
    String source = null;
    // if sourceFileName is null get the first arg as source string to be compared
    if(sourceFileName==null) { source = Options.getFirst(args); args = Options.removeFirst(args);}
    else source = Options.getFileContentsAsString(sourceFileName);
      
    String target = null;
    // if targetFileName is null get the first arg as target string to be compared
    if(targetFileName==null) { target = Options.getFirst(args); args = Options.removeFirst(args);}
    else target = Options.getFileContentsAsString(targetFileName);
    
    if(VERBOSE)
    { System.out.println("SOURCE:"); System.out.println(source);
      System.out.println("TARGET:"); System.out.println(target);
    }    
    if(source!=null && target!=null)
    { PrimStringDiff diff = new PrimStringDiff(new PrimString(TYPE.STRING, source), 
                                               new PrimString(TYPE.STRING, target));
      for(; !diff.refine(); );
      if(!(Main.VERBOSE||VERBOSE) && (Main.DIFF||DIFF)) 
        System.out.println(diff.getSolution());
      if(Main.SIM || SIM) 
        System.out.println(diff.getSim().getPercentage());
    }
    final long endTime   = System.currentTimeMillis();
    final long totalTime = (endTime - startTime)/1000;
    System.out.println("duration:"+totalTime+"s");  
  }  

}
