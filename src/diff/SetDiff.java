package diff;

import java.io.FileWriter;
import java.io.IOException;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

import sim.Sim;
import type.*;
import value.*;
import dcprototype.*;

public class SetDiff extends Diff 
{ private final TypeSet a, b;
  private PartialSolution[] candidates;
  
  public SetDiff(TypeSet a, TypeSet b)
  { if(!a.getBaseTYPE().equals(b.getBaseTYPE()))
      throw new RuntimeException("These two sets have different base type values.");
    this.a=a; this.b=b;
    this.candidates = new PartialSolution[] { new PartialSolution(null, a, b)};
  }        
   
  public String toString(){ return this.candidates[0].toString();}
  public Sim getSim(){ return this.candidates[0].getSim();}  
  public Sim getUnknown(){ return Sim.UNKNOWN(this.a.weight()+this.b.weight());}
  public PartialSolution getSolution(){ return this.candidates[0];}

  public boolean isFinal(){ return this.candidates[0].getSim().isFinal();}
  public boolean refine()
  { if (Main.VERBOSE || VERBOSE) 
    { System.out.println(this.candidates[0]);
      for(int i=0; i<this.candidates.length; i++)
        System.out.println(""+i+": "+this.candidates[i].getSim());
      System.out.println();
    }
    if(this.candidates[0].trace==null)
    { this.candidates = insertAll(this.candidates[0].expand(), this.deleteFirst(this.candidates));
      Arrays.sort(this.candidates, simComparator);
      return false;
    }
    else if(!this.candidates[0].refine())
    { Arrays.sort(this.candidates, simComparator);
      return false;
    }
    else if (isFinal()){ return true;}    
    else
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
  { PartialSolution[] res = new PartialSolution[cands.length+newCands.length];
    int j = 0;
    for(int i =0; i < newCands.length; i++)
      res[j++] = newCands[i];
    for(int i=0; i < cands.length; i++)
      res[j++] = cands[i];
    return res;
  } 
  // append a deletion to an array of changes
  private static PartialSolution[] addOne(PartialSolution[] changes, PartialSolution del)
  { PartialSolution[] res = new PartialSolution[changes.length+1];
    System.arraycopy(changes, 0, res, 0, changes.length);
    res[changes.length]=del;
    return res;
  }
 
  // inner class, i.e. it has implicit reference to ListDiff i.e. a, b
  private class PartialSolution  
  { private final Trace trace;
    private final TypeSet remain_a;
    private final TypeSet remain_b;

    private PartialSolution(Trace trace, TypeSet remain_a, TypeSet remain_b)
    { this.trace = trace; this.remain_a=remain_a; this.remain_b= remain_b;}

    public String toString(){ return "{"+(trace == null ? "" : trace.toString())+"}"+"? "+remain_a+", ?"+remain_b;}  
    public Sim getSim(){ return (trace == null ? Sim.UNKNOWN(SetDiff.this.a.weight()+
                                                             SetDiff.this.b.weight()) : trace.getSim());}   
    public boolean refine(){ if(trace==null) return false; else return trace.refine();}        
    
    public PartialSolution delete()
    { TypeSet set_a = SetDiff.this.a;
      EditOperation op = new Delete(SetDiff.this.a.get(0));
      Trace trace = new Trace(this.trace, op);
      return new PartialSolution(trace, set_a.remove(0), SetDiff.this.b);
    }        
    public PartialSolution insert()
    { TypeSet set_b = SetDiff.this.b;
      EditOperation op = new Insert(SetDiff.this.b.get(0));
      Trace trace = new Trace(this.trace, op);
      return new PartialSolution(trace, SetDiff.this.a, set_b.remove(0));
    }        
    public PartialSolution copy()
    { TypeSet set_a = SetDiff.this.a;
      TypeSet set_b = SetDiff.this.b;
      EditOperation op = new Copy(SetDiff.this.a.get(0));
      Trace trace = new Trace(this.trace, op);
      return new PartialSolution(trace, set_a.remove(0), set_b.remove(0));
    }      
    public PartialSolution[] changes()
    { TypeSet set_a = SetDiff.this.a;
      TypeSet set_b = SetDiff.this.b;
      int b_size = SetDiff.this.b.size();

      EditOperation[] op = new EditOperation[b_size];
      Trace[] trace = new Trace[b_size];
      PartialSolution[] changes = new PartialSolution[b_size];

      TYPE baseTYPE = SetDiff.this.a.getBaseTYPE();
      TypeSet remain_a = set_a.remove(0);
      System.out.println("set_a="+set_a);
      System.out.println("set_b="+set_b);
      System.out.println("remain_a="+remain_a);
     
      for(int i=0; i<b_size; i++)
      { if(baseTYPE.isUNIT()||baseTYPE.isBOOL()||baseTYPE.isCHAR()||
           baseTYPE.isNAT()||baseTYPE.isINT()|| baseTYPE.isREAL()) 
        { op[i]= new Change(new PrimDiff(set_a.get(0), set_b.get(i)));
          trace[i] = new Trace(this.trace, op[i]);
          changes[i]= new PartialSolution(trace[i], remain_a, set_b.remove(i));
          System.out.println("In loop: ("+i+") set_b="+set_b);
        }
        else if(baseTYPE.isSTRING())
        { op[i]= new Change(new PrimStringDiff((PrimString)SetDiff.this.a.get(0), 
                                               (PrimString)SetDiff.this.b.get(i)));
          trace[i] = new Trace(this.trace, op[i]);
          changes[i]= new PartialSolution(trace[i], remain_a, set_b.remove(i));
        }
        else if(baseTYPE.isPRODUCT()) 
        { op[i]= new Change(new ProductDiff((TypeProduct)SetDiff.this.a.get(0), 
                                            (TypeProduct)SetDiff.this.b.get(i)));
          trace[i] = new Trace(this.trace, op[i]);
          changes[i]= new PartialSolution(trace[i], remain_a, set_b.remove(i));
        }
        else if(baseTYPE.isUNION()) 
        { op[i]= new Change(new UnionDiff((TypeUnion)SetDiff.this.a.get(0), 
                                          (TypeUnion)SetDiff.this.b.get(i)));
          trace[i] = new Trace(this.trace, op[i]);
          changes[i]= new PartialSolution(trace[i], remain_a, set_b.remove(i));
        }
        else if(baseTYPE.isLIST())
        { op[i]= new Change(new ListDiff((TypeList)SetDiff.this.a.get(0), 
                                         (TypeList)SetDiff.this.b.get(i)));
          trace[i] = new Trace(this.trace, op[i]);
          changes[i]= new PartialSolution(trace[i], remain_a, set_b.remove(i));
        }
        else if(baseTYPE.isSET())
        { op[i]= new Change(new SetDiff((TypeSet)SetDiff.this.a.get(0), 
                                        (TypeSet)SetDiff.this.b.get(i)));
          trace[i] = new Trace(this.trace, op[i]);
          changes[i]= new PartialSolution(trace[i], remain_a, set_b.remove(i));
        }
        else throw new RuntimeException("More Types need to be explored.");
      }
      return changes;
    }
    private PartialSolution[] expand()
    { int a_size = SetDiff.this.a.size();
      int b_size = SetDiff.this.b.size();
     
      if(a_size == 0 && b_size == 0) return new PartialSolution[0];
      else if(a_size > 0 && b_size == 0) return new PartialSolution[]{ delete()};
      else if(a_size == 0 && b_size > 0) return new PartialSolution[]{ insert()};
      // if they primitive type value
      else if(SetDiff.this.a.weight() == 1 && SetDiff.this.b.weight() == 1)      
      { if(SetDiff.this.a.get(0).equals(SetDiff.this.b.get(0))) 
          return new PartialSolution[]{ copy()};
        else return new PartialSolution[] { delete(), insert()};
      }
      else return addOne(changes(), delete());
    }        
  } 
  // a trace is a (reverse) List of Edit Operations appplied to a specific position
  private class Trace
  { private final Trace trace;
    private final EditOperation op;
    private Sim sim;
    
    public Trace(Trace trace, EditOperation op)
    { this.trace = trace;
      this.op = op;
      this.sim = op.calculate(trace == null ? SetDiff.this.getUnknown() : trace.getSim());
    }        
    public String toString()
    { return (this.trace ==  null ? "" : this.trace.toString())+this.op;}
    public Sim getSim(){ return this.sim;}

    public boolean refine()
    { if(this.op.refine()) return true;
      this.sim=this.op.calculate(trace==null ? SetDiff.this.getUnknown() : trace.getSim());
      return false;
    }
  }
 
  private abstract static class EditOperation
  { abstract Sim calculate(Sim sim);
    abstract boolean refine();// return true if there was a refinement possible, false otherwise
  }
  private final static class Insert extends EditOperation
  { private final TypeT c;
    public Insert(TypeT c){ this.c=c;}
    public String toString(){ return "+"+c;}
    public Sim calculate(Sim sim){ return sim.dec(c.weight());}
    public boolean refine(){ return true;}
  }
  private final static class Delete extends EditOperation
  { private final TypeT c;
    public Delete(TypeT c){ this.c=c;}
    public String toString(){ return "-"+c;}
    public Sim calculate(Sim sim){ return sim.dec(c.weight());}
    public boolean refine(){ return true;}
  }
  private final static class Copy extends EditOperation
  { private final TypeT c;
    public Copy(TypeT c){ this.c=c;}
    public String toString(){ return "="+c;}
    public Sim calculate(Sim sim){ return sim.inc(2*c.weight());}
    public boolean refine(){ return true;}
  }
  private final static class Change extends EditOperation
  { private final Diff diff;
    public Change(Diff diff){ this.diff=diff;}
    public String toString(){ return "!"+diff;}
    public Sim calculate(Sim sim)
    { return sim.inc(this.diff.getSim().getIncrement()).dec(this.diff.getSim().getDecrement());}
    public boolean refine(){ return diff.refine();}
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
    
    String typeFileName=Options.getOption(args, "-type");//get the arg after the -type
    if(typeFileName!=null) args = Options.remove(args, "-type", typeFileName);
    String sourceFileName = Options.getOption(args, "-source");// get the arg after the -source
    if(sourceFileName!=null) args = Options.remove(args, "-source", sourceFileName);
    String targetFileName = Options.getOption(args, "-target");// get the arg after the -target
    if(targetFileName!=null) args = Options.remove(args, "-target", targetFileName);
    // parse TYPE
    String strTYPE = null;
    if(typeFileName==null)
    { strTYPE = Options.getFirst(args); args = Options.removeFirst(args);}
    else strTYPE = Options.getFileContentsAsString(typeFileName);
    List<String> lovs = new ArrayList<String>();
    TYPE resTYPE=ParseTYPEresult.parseTYPE(lovs, strTYPE).getResult();//parse TYPE
    System.out.println("resTYPE: "+resTYPE);
    // parse the first value
    String source = null;
    // if sourceFileName is null get the first arg as source string to be compared
    if(sourceFileName==null) 
    { source = Options.getFirst(args); args = Options.removeFirst(args);}
    else source = Options.getFileContentsAsString(sourceFileName);
    TypeT resV1=ParseVALUEresult.parseVALUE(resTYPE, source).getResult();//parse VALUE1 
    System.out.println("resV1: "+resV1);
    // parse the second value
    String target = null;
    // if targetFileName is null get the first arg as target string to be compared
    if(targetFileName==null) 
    { target = Options.getFirst(args); args = Options.removeFirst(args);}
    else target = Options.getFileContentsAsString(targetFileName);
    TypeT resV2=ParseVALUEresult.parseVALUE(resTYPE, target).getResult();//parse VALUE2
    System.out.println("resV2: "+resV2);
    // model values to be typed values
    TypeT model1 = Main.model(resTYPE, resV1);
    TypeT model2 = Main.model(resTYPE, resV2);
        
    if(VERBOSE)
    { System.out.println("SOURCE:"); System.out.println(resV1);
      System.out.println("TARGET:"); System.out.println(resV2);
    }    
    if(source!=null && target!=null)
    { SetDiff diff = new SetDiff((TypeSet)resV1, (TypeSet)resV2);
      for(; !diff.refine(); );
      if(!(Main.VERBOSE||VERBOSE) && (Main.DIFF||DIFF)) 
      System.out.println(diff.getSolution());
      if(Main.SIM||SIM) 
      System.out.println(diff.getSim().getPercentage());    
    }
    final long endTime   = System.currentTimeMillis();
    final long totalTime = (endTime - startTime)/1000;
    System.out.println("duration:"+totalTime+"s");
  }  
}
