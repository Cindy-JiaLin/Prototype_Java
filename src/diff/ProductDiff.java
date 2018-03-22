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

public class ProductDiff extends Diff 
{ private final TypeProduct a, b;
  private PartialSolution[] candidates;
  
  public ProductDiff(TypeProduct a, TypeProduct b)
  { // These two product values must have the same size
    if(a.size()!=b.size()) throw new RuntimeException("Different size Product values cannot be compared.");
    this.a=a; this.b=b;
    this.candidates = new PartialSolution[] { new PartialSolution(null)};
  }        
  
  public Sim getUnknown(){ return Sim.UNKNOWN(this.a.weight()+this.b.weight());} 
  public String toString(){ return this.candidates[0].toString();}
  public String html(){ return this.candidates[0].html();}
  public Sim getSim(){ return this.candidates[0].getSim();}  

  public boolean isFinal(){ return this.candidates[0].getSim().isFinal();}

  public boolean refine()
  { if (Main.VERBOSE || VERBOSE) 
    { System.out.println(this.candidates[0]);
      for(int i=0; i<this.candidates.length; i++)
        System.out.println(""+i+": "+this.candidates[i].getSim());
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
    { if(!(Main.VERBOSE||VERBOSE) && (Main.DIFF||DIFF)) 
      System.out.println(this.candidates[0]);
      if(HTMLCODE) writeHTML(this.candidates[0]);
      if(Main.SIM||SIM) 
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
   
    public int getIndex(){ return (this.trace == null ? 0 : trace.index);}
 
    public String toString(){ return "["+(trace == null ? "" : trace.toString())+"]"+getSim();}  
    public String html()
    { return HTML.TABLE(trace.html()+
      (SIM ? HTML.TD2(HTML.CHG,getSim().getPercentage()):""));}
    public Sim getSim()
    { return (trace == null ? Sim.UNKNOWN(ProductDiff.this.a.weight()+
                                          ProductDiff.this.b.weight()) : trace.getSim());}
        
    public boolean refine(){ if(trace==null) return false; else return trace.refine();}        
           
    public PartialSolution copy()
    { EditOperation op = new Copy(ProductDiff.this.a.getValues().get(getIndex()));
      Trace trace = new Trace(this.trace, op);
      return new PartialSolution(trace);
    }      
    public PartialSolution change()
    { EditOperation op;
      Trace trace;
      TYPE t = a.typeOf().getTYPEs().get(getIndex());
      if(t.isUNIT()) 
      { op= new Change(new PrimUnitDiff((PrimUnit)ProductDiff.this.a.getValues().get(getIndex()), 
                                        (PrimUnit)ProductDiff.this.b.getValues().get(getIndex())));
        trace = new Trace(this.trace, op);
        return new PartialSolution(trace);
      }
      else if(t.isBOOL()) 
      { op= new Change(new PrimBoolDiff((PrimBool)ProductDiff.this.a.getValues().get(getIndex()), 
                                        (PrimBool)ProductDiff.this.b.getValues().get(getIndex())));
        trace = new Trace(this.trace, op);
        return new PartialSolution(trace);
      }
      else if(t.isCHAR()) 
      { op= new Change(new PrimCharDiff((PrimChar)ProductDiff.this.a.getValues().get(getIndex()), 
                                        (PrimChar)ProductDiff.this.b.getValues().get(getIndex())));
        trace = new Trace(this.trace, op);
        return new PartialSolution(trace);
      }
      else if(t.isSTRING())
      { op= new Change(new PrimStringDiff((PrimString)ProductDiff.this.a.getValues().get(getIndex()), 
                                          (PrimString)ProductDiff.this.b.getValues().get(getIndex())));       
        trace = new Trace(this.trace, op);
        return new PartialSolution(trace);
      }
      else if(t.isNAT()) 
      { op= new Change(new PrimNatDiff((PrimNat)ProductDiff.this.a.getValues().get(getIndex()), 
                                       (PrimNat)ProductDiff.this.b.getValues().get(getIndex())));
        trace = new Trace(this.trace, op);
        return new PartialSolution(trace);
      }
      else//(t.isLIST())
      { op= new Change(new ListDiff((TypeList)ProductDiff.this.a.getValues().get(getIndex()), 
                                    (TypeList)ProductDiff.this.b.getValues().get(getIndex())));        
        trace = new Trace(this.trace, op);
        return new PartialSolution(trace);
      }
    }        
    private PartialSolution[] expand()
    { if(ProductDiff.this.a.size()==getIndex()) return new PartialSolution[0];// reach the end
      else if(ProductDiff.this.a.getValues().get(getIndex()).equals(ProductDiff.this.b.getValues().get(getIndex()))) 
             return new PartialSolution[]{ copy()};
      else return new PartialSolution[]{ change()};
    }        
    
    // Get the last copy operation position
    public int getStopper(Trace trace)
    { if(trace == null) return -1;
      else if(trace.op instanceof Copy) return trace.index;
      else return getStopper(trace.trace);
    }  
    public boolean isRedundant(PartialSolution[] active) 
    { int stopper = getStopper(this.trace);
      for(int i = 0; i < active.length; i++)
      { Trace t = active[i].trace;
        if(stopper == getStopper(t)) return true;
      }    
      return false;
    }      
  } 
  // a trace is a (reverse) List of Edit Operations appplied to a specific position
  // Each element in a has the corresponding matching components in b at the position index
  private class Trace
  { private final Trace trace;
    private final int index;
    private final EditOperation op;
    private Sim sim;
    
    public Trace(Trace trace, EditOperation op)
    { this.trace = trace;
      this.op = op;
      this.index = op.next(trace == null ? 0 : trace.index);
      this.sim = op.calculate(trace == null ? ProductDiff.this.getUnknown() :  trace.getSim());
    }        
    public String toString()
    { return (this.trace ==  null ? "" : this.trace.toString())+this.op;}
    public String html()
    { return(this.trace != null ? this.trace.html() : "")+HTML.TR(op.html(index));}
    public Sim getSim(){ return this.sim;}

    public boolean refine()
    { if(this.op.refine()) return true;
      this.sim=this.op.calculate(trace==null ? ProductDiff.this.getUnknown() : trace.getSim());
      return false;
    }
  }
 
  private abstract static class EditOperation
  { abstract String html(int index);
    abstract Sim calculate(Sim sim);
    abstract boolean refine();// return true if there was a refinement possible, false otherwise
    abstract int next(int index);
    // return the position of the element in a after a specific edit EditOperation
  }
  private final static class Copy extends EditOperation
  { private final TypeT c;
    public Copy(TypeT c){ this.c=c;}
    public String toString(){ return "="+c;}
    public String html(int index)
    { return HTML.TD(HTML.CPY,index)+
      (SIM ? HTML.TD("") : "")+
             HTML.TD(HTML.CPY, HTML.encode(c.toString()));}

    public Sim calculate(Sim sim){ return sim.inc(2*c.weight());}
    public boolean refine(){ return true;}
    public int next(int index){ return index+1;}
  }
  private final static class Change extends EditOperation
  { private final Diff diff;
    public Change(Diff diff){ this.diff=diff;}
    public String toString(){ return "!"+diff;}
    public String html(int index)
    { if(diff instanceof PrimUnitDiff)
      { return HTML.TD(HTML.CHG,index)+
        (SIM ? HTML.TD(""+((PrimUnitDiff)diff).getSim().getPercentage1()+" ") : "")+
               HTML.TD(HTML.CHG, ((PrimUnitDiff)diff).html());
      }
      else if(diff instanceof PrimBoolDiff)
      { return HTML.TD(HTML.CHG,index)+
        (SIM ? HTML.TD(""+((PrimBoolDiff)diff).getSim().getPercentage1()+" ") : "")+
               HTML.TD(HTML.CHG, ((PrimBoolDiff)diff).html());
      }
      else if(diff instanceof PrimCharDiff)
      { return HTML.TD(HTML.CHG,index)+
        (SIM ? HTML.TD(""+((PrimCharDiff)diff).getSim().getPercentage1()+" ") : "")+
               HTML.TD(HTML.CHG, ((PrimCharDiff)diff).html());
      }
      else if(diff instanceof PrimStringDiff)
      { return HTML.TD(HTML.CHG,index)+
        (SIM ? HTML.TD(""+((PrimStringDiff)diff).getSim().getPercentage1()+" ") : "")+
               HTML.TD(HTML.CHG, ((PrimStringDiff)diff).html());
      }
      else if(diff instanceof PrimNatDiff)
      { return HTML.TD(HTML.CHG,index)+
        (SIM ? HTML.TD(""+((PrimNatDiff)diff).getSim().getPercentage1()+" ") : "")+
               HTML.TD(HTML.CHG, ((PrimNatDiff)diff).html());
      }
      else if(diff instanceof ProductDiff)
      { return HTML.TD(HTML.CHG,index)+
        (SIM ? HTML.TD(""+((ProductDiff)diff).getSim().getPercentage1()+" ") : "")+
               HTML.TD(HTML.CHG, ((ProductDiff)diff).html());
      }
      else if(diff instanceof ListDiff)
      { return HTML.TD(HTML.CHG,index)+
        (SIM ? HTML.TD(""+((ListDiff)diff).getSim().getPercentage1()+" ") : "")+
               HTML.TD(HTML.CHG, ((ListDiff)diff).html());
      }
      else throw new RuntimeException("Currently, there is no other diff.");
    }

    public Sim calculate(Sim sim)
    { return sim.inc(this.diff.getSim().getIncrement()).dec(this.diff.getSim().getDecrement());}
    public boolean refine(){ return diff.refine();}
    public int next(int index){ return index+1;}
  }

  public static boolean VERBOSE = false;// if true all intermediate states will be logged
  public static boolean SIM = false;// displays similarity as percentage
  public static boolean DIFF = false;// displays difference as a solution (PartialSolution) 
  public static boolean INFO = false;// displays runtime statistics
  public static boolean HTMLCODE=false;// displays difference as text
    
  private static String htmlFileName;
  
  private static void writeHTML(PartialSolution solution)
  { if(htmlFileName!=null)
    try
    { FileWriter out = new FileWriter(htmlFileName);
      out.write(HTML.BODY(solution.html()));
      out.flush();
      out.close();
    }  
    catch(IOException e){ System.err.println("Promblem writing to:"+htmlFileName);}
  }    
  public static void main(String[] args) 
  { final long startTime = System.currentTimeMillis();
    if(Options.isSet(args, "-verbose")) { VERBOSE = true; args = Options.remove(args, "-verbose");}
    if(Options.isSet(args, "-sim")) { SIM = true; args = Options.remove(args, "-sim");}
    if(Options.isSet(args, "-diff")) { DIFF = true; args = Options.remove(args, "-diff");}
    
    htmlFileName=Options.getOption(args, "-html");
    if(htmlFileName!=null){ HTMLCODE=true; args=Options.remove(args,"-html", htmlFileName);}
     
    String typeFileName=Options.getOption(args, "-type");//get the arg after the -type
    if(typeFileName!=null) args = Options.remove(args, "-type", typeFileName);

    String sourceFileName = Options.getOption(args, "-source");// get the arg after the -source
    if(sourceFileName!=null) args = Options.remove(args, "-source", sourceFileName);
      
    String targetFileName = Options.getOption(args, "-target");// get the arg after the -target
    if(targetFileName!=null) args = Options.remove(args, "-target", targetFileName);

    String strTYPE = null;
    if(typeFileName==null)
    { strTYPE = Options.getFirst(args); args = Options.removeFirst(args);
      //System.out.println("strTYPE: "+strTYPE);
    }
    else strTYPE = Options.getFileContentsAsString(typeFileName);
    //System.out.println("strTYPE: "+strTYPE);
    List<String> lovs = new ArrayList<String>();
    TYPE resTYPE=ParseTYPEresult.parseTYPE(lovs, strTYPE).getResult();//parse TYPE
    System.out.println("resTYPE: "+resTYPE);

    String source = null;
    // if sourceFileName is null get the first arg as source string to be compared
    if(sourceFileName==null) 
    { source = Options.getFirst(args); args = Options.removeFirst(args);
      //System.out.println("source: "+source);
    }
    else source = Options.getFileContentsAsString(sourceFileName);
    //System.out.println("source: "+source);
    TypeT resV1=ParseVALUEresult.parseVALUE(resTYPE, source).getResult();//parse VALUE1 
    System.out.println("resV1: "+resV1);
    
    String target = null;
    // if targetFileName is null get the first arg as target string to be compared
    if(targetFileName==null) 
    { target = Options.getFirst(args); args = Options.removeFirst(args);
      //System.out.println("target: "+target);
    }
    else target = Options.getFileContentsAsString(targetFileName);
    //System.out.println("target: "+target);
    TypeT resV2=ParseVALUEresult.parseVALUE(resTYPE, target).getResult();//parse VALUE2
    System.out.println("resV2: "+resV2);

    TypeT model1 = Main.model(resTYPE, resV1);
    TypeT model2 = Main.model(resTYPE, resV2);
        
    if(VERBOSE)
    { System.out.println("SOURCE:"); System.out.println(resV1);
      System.out.println("TARGET:"); System.out.println(resV2);
    }    
    if(source!=null && target!=null)
    { if(resTYPE.isUNIT())
      { PrimUnitDiff diff = new PrimUnitDiff((PrimUnit)resV1, (PrimUnit)resV2);
        for(; !diff.refine(); );
      }
      else if(resTYPE.isBOOL())
      { PrimBoolDiff diff = new PrimBoolDiff((PrimBool)resV1, (PrimBool)resV2);
        for(; !diff.refine(); );
      }
      else if(resTYPE.isCHAR())
      { PrimCharDiff diff = new PrimCharDiff((PrimChar)resV1, (PrimChar)resV2);
        for(; !diff.refine(); );
      }
      else if(resTYPE.isSTRING())
      { PrimStringDiff diff = new PrimStringDiff((PrimString)resV1, (PrimString)resV2);
        for(; !diff.refine(); );
      }
      else if(resTYPE.isNAT())
      { PrimNatDiff diff = new PrimNatDiff((PrimNat)resV1, (PrimNat)resV2);
        for(; !diff.refine(); );
      }
      else if(resTYPE.isPRODUCT())
      { ProductDiff diff = new ProductDiff((TypeProduct)resV1, (TypeProduct)resV2);
        for(; !diff.refine(); );
      }
      else if(resTYPE.isLIST())
      { ListDiff diff = new ListDiff((TypeList)resV1, (TypeList)resV2);
        for(; !diff.refine(); );
      }
    }  
    final long endTime   = System.currentTimeMillis();
    final long totalTime = (endTime - startTime)/1000;
    System.out.println("duration:"+totalTime+"s");
  }  
}
