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
  public TypeT getSourceValue(){ return this.a;}
  public TypeT getTargetValue(){ return this.b;}
 
  public ProductDiff(TypeProduct a, TypeProduct b)
  { // These two product values must have the same size
    if(a.size()!=b.size()) 
       throw new RuntimeException("Different size Product values cannot be compared.");
    this.a=a; this.b=b;
    this.candidates = new PartialSolution[] { new PartialSolution(null)};
  }        
  
  public Sim getUnknown(){ return Sim.UNKNOWN(this.a.weight()+this.b.weight());} 

  public String toString(){ return this.candidates[0].toString();}
  public String html(){ return this.candidates[0].html();}
  public Sim getSim(){ return this.candidates[0].getSim();}
  public PartialSolution getSolution(){ return this.candidates[0];}  

  public PartialSolution getFirstCand(){ return this.candidates[0];}
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
    else if(!this.candidates[0].refine())
    { Arrays.sort(this.candidates, simComparator);// sort the candidates after each inside refine step
      return false;
    }
    else if (isFinal()){ return true;}    
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
 
    public String toString()
    { return Console.chg("(")+(trace == null ? "" : trace.toString())+Console.chg(")");}  
    public String html()
    { return HTML.TABLE(HTML.TD(HTML.CHG, "(")+trace.html()+HTML.TD(HTML.CHG,")")+(SIM ? HTML.TD2(HTML.CHG,getSim().getPercentage()):""));}
    public Sim getSim()
    { return (trace == null ? Sim.UNKNOWN(ProductDiff.this.a.weight()+
                                          ProductDiff.this.b.weight()) : trace.getSim());}
        
    public boolean refine(){ if(trace==null) return false; else return trace.refine();}        
    
    public PartialSolution delete()
    { EditOperation op = new Delete(ProductDiff.this.a.getValues().get(getSource()));
      Trace trace = new Trace(this.trace, op);
      return new PartialSolution(trace);
    }        
    public PartialSolution insert()
    { EditOperation op = new Insert(ProductDiff.this.b.getValues().get(getTarget()));
      Trace trace = new Trace(this.trace, op);
      return new PartialSolution(trace);
    }        
    public PartialSolution copy()
    { EditOperation op = new Copy(ProductDiff.this.b.getValues().get(getTarget()));
      Trace trace = new Trace(this.trace, op);
      return new PartialSolution(trace);
    }      
    public PartialSolution change()
    { EditOperation op;
      Trace trace;
      TYPE t = a.typeOf().getTYPEs().get(getSource());
      if(t.isUNIT()||t.isBOOL()||t.isCHAR()||t.isNAT()||t.isINT()||t.isREAL()) 
      { op= new Change(new PrimDiff(ProductDiff.this.a.getValues().get(getSource()), 
                                    ProductDiff.this.b.getValues().get(getTarget())));
        trace = new Trace(this.trace, op);
        return new PartialSolution(trace);
      }
      else if(t.isSTRING())
      { op= new Change(new PrimStringDiff((PrimString)ProductDiff.this.a.getValues().get(getSource()), 
                                          (PrimString)ProductDiff.this.b.getValues().get(getTarget())));
        trace = new Trace(this.trace, op);
        return new PartialSolution(trace);
      }
      else if(t.isPRODUCT()) 
      { op= new Change(new ProductDiff((TypeProduct)ProductDiff.this.a.getValues().get(getSource()), 
                                       (TypeProduct)ProductDiff.this.b.getValues().get(getTarget())));
        trace = new Trace(this.trace, op);
        return new PartialSolution(trace);
      }
      else if(t.isUNION()) 
      { op= new Change(new UnionDiff((TypeUnion)ProductDiff.this.a.getValues().get(getSource()), 
                                     (TypeUnion)ProductDiff.this.b.getValues().get(getTarget())));
        trace = new Trace(this.trace, op);
        return new PartialSolution(trace);
      }
      else if(t.isLIST())
      { op= new Change(new ListDiff((TypeList)ProductDiff.this.a.getValues().get(getSource()), 
                                    (TypeList)ProductDiff.this.b.getValues().get(getTarget())));
        trace = new Trace(this.trace, op);
        return new PartialSolution(trace);
      }
       else if(t.isSET())
      { op= new Change(new SetDiff((TypeSet)ProductDiff.this.a.getValues().get(getSource()), 
                                   (TypeSet)ProductDiff.this.b.getValues().get(getTarget())));
        trace = new Trace(this.trace, op);
        return new PartialSolution(trace);
      }

      else throw new RuntimeException("More Types need to be explored.");
    }        
    private PartialSolution[] expand(){ return new PartialSolution[]{ change()};}      
    
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
        if(stopper == getStopper(t) && 
           lastDelete == getLastDelete(t) && 
           lastInsert == getLastInsert(t))
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
      this.sim = op.calculate(trace == null ? ProductDiff.this.getUnknown() :  trace.getSim());
    }        
    public String toString()
    { String label=ProductDiff.this.a.getLabels().get(ia-1);
      return (this.trace ==  null ? "" : this.trace.toString())+
              Console.cpy(label.equals("nolabel") ? "" : label+".")+
              this.op+(ProductDiff.this.a.size()==ia ? "" : Console.cpy(","));
    }
    public String html()
    { String label=ProductDiff.this.a.getLabels().get(ia-1);
      return (this.trace != null ? this.trace.html() : "")+
             HTML.TR(HTML.TD(HTML.CPY, label.equals("nolabel") ? "" : label+".")+
             op.html(ia,ib)+HTML.TD(HTML.CHG, ","));}
    public Sim getSim(){ return this.sim;}

    public boolean refine()
    { if(this.op.refine()) return true;
      this.sim=this.op.calculate(trace==null ? ProductDiff.this.getUnknown() : trace.getSim());
      return false;
    }
  }
 
  private abstract static class EditOperation
  { abstract String html(int ia, int ib);
    abstract Sim calculate(Sim sim);
    abstract boolean refine();// return true if there was a refinement possible, false otherwise
    abstract int nextA(int ia);// return the position of the element in a after a specific edit EditOperation
    abstract int nextB(int ib);// return the position of the element in b after a specific edit EditOperation
  }
  private final static class Insert extends EditOperation
  { private final TypeT c;
    public Insert(TypeT c){ this.c=c;}
    public String toString(){ return Console.ins(""+c);}
    
    public String html(int ia, int ib)
    { return HTML.TD("")+
             HTML.TD(HTML.INS,ib)+
      (SIM ? HTML.TD("") : "")+
             HTML.TD(HTML.INS, HTML.encode(c.toString()));}

    public Sim calculate(Sim sim){ return sim.dec(c.weight());}
    public boolean refine(){ return true;}
    public int nextA(int ia){ return ia;}
    public int nextB(int ib){ return ib+1;}
  }
  private final static class Delete extends EditOperation
  { private final TypeT c;
    public Delete(TypeT c){ this.c=c;}
    public String toString(){ return Console.del(""+c);}
    public String html(int ia, int ib)
    { return HTML.TD("")+
             HTML.TD(HTML.DEL,ia)+
      (SIM ? HTML.TD("") : "")+
             HTML.TD(HTML.DEL, HTML.encode(c.toString()));}

    public Sim calculate(Sim sim){ return sim.dec(c.weight());}
    public boolean refine(){ return true;}
    public int nextA(int ia){ return ia+1;}
    public int nextB(int ib){ return ib;}
  }
  private final static class Copy extends EditOperation
  { private final TypeT c;
    public Copy(TypeT c){ this.c=c;}
    public String toString(){ return "="+c;}
    public String html(int ia, int ib)
    { return HTML.TD(HTML.CPY,ia)+
             HTML.TD(HTML.CPY,ib)+
      (SIM ? HTML.TD("") : "")+
             HTML.TD(HTML.CPY, HTML.encode(c.toString()));}

    public Sim calculate(Sim sim){ return sim.inc(2*c.weight());}
    public boolean refine(){ return true;}
    public int nextA(int ia){ return ia+1;}
    public int nextB(int ib){ return ib+1;}
  }
  private final static class Change extends EditOperation
  { private final Diff diff;
    public Change(Diff diff){ this.diff=diff;}
    public String toString(){ return Console.chg(""+diff);}
    public String html(int ia, int ib)
    { if(diff instanceof PrimDiff)
      { return //HTML.TD(HTML.CHG,ia)+
               //HTML.TD(HTML.CHG,ib)+
        (SIM ? HTML.TD(""+((PrimDiff)diff).getSim().getPercentage1()+" ") : "")+
               HTML.TD(HTML.CHG, ((PrimDiff)diff).html());
      }
      else if(diff instanceof PrimStringDiff)
      { PrimStringDiff primStringDiff=(PrimStringDiff)diff;
        return //HTML.TD(HTML.CHG,ia)+
               //HTML.TD(HTML.CHG,ib)+
        (SIM ? HTML.TD(""+((PrimStringDiff)diff).getSim().getPercentage1()+" ") : "")+
               HTML.TD((primStringDiff.getSim().isEqual()? HTML.CPY : HTML.CHG), primStringDiff.html());
      }
      else if(diff instanceof ProductDiff)
      { return //HTML.TD(HTML.CHG,ia)+
               //HTML.TD(HTML.CHG,ib)+
        (SIM ? HTML.TD(""+((ProductDiff)diff).getSim().getPercentage1()+" ") : "")+
               HTML.TD(HTML.CHG, ((ProductDiff)diff).html());
      }
      else if(diff instanceof UnionDiff)
      { return //HTML.TD(HTML.CHG,ia)+
               //HTML.TD(HTML.CHG,ib)+
        (SIM ? HTML.TD(""+((UnionDiff)diff).getSim().getPercentage1()+" ") : "")+
               HTML.TD(HTML.CHG, ((UnionDiff)diff).html());
      }
      else if(diff instanceof ListDiff)
      { return //HTML.TD(HTML.CHG,ia)+
               //HTML.TD(HTML.CHG,ib)+
        (SIM ? HTML.TD(""+((ListDiff)diff).getSim().getPercentage1()+" ") : "")+
               HTML.TD(HTML.CHG, ((ListDiff)diff).html());
      }
      else throw new RuntimeException("Currently, there is no other diff.");
    }

    public Sim calculate(Sim sim)
    { return sim.inc(this.diff.getSim().getIncrement()).dec(this.diff.getSim().getDecrement());}
    public boolean refine(){ return diff.refine();}
    public int nextA(int ia){ return ia+1;}
    public int nextB(int ib){ return ib+1;}
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
    // parse the TYPE
    String strTYPE = null;
    if(typeFileName==null)
    { strTYPE = Options.getFirst(args); args = Options.removeFirst(args);}
    else strTYPE = Options.getFileContentsAsString(typeFileName);
    List<String> lovs = new ArrayList<String>();
    TYPE resTYPE=ParseTYPEresult.parseTYPE(lovs, strTYPE).getResult();
    System.out.println("TYPE: \n"+resTYPE);
    // parse the first value
    String source = null;
    // if sourceFileName is null get the first arg as source string to be compared
    if(sourceFileName==null) 
    { source = Options.getFirst(args); args = Options.removeFirst(args);}
    else source = Options.getFileContentsAsString(sourceFileName);
    TypeT resV1=ParseVALUEresult.parseVALUE(resTYPE, source).getResult();
    System.out.println("Value1: \n"+resV1);
    // parse the second value
    String target = null;
    // if targetFileName is null get the first arg as target string to be compared
    if(targetFileName==null) 
    { target = Options.getFirst(args); args = Options.removeFirst(args);}
    else target = Options.getFileContentsAsString(targetFileName);
    TypeT resV2=ParseVALUEresult.parseVALUE(resTYPE, target).getResult();
    System.out.println("Value2: \n"+resV2);

    TypeT model1 = Main.model(resTYPE, resV1);
    TypeT model2 = Main.model(resTYPE, resV2);
        
    if(VERBOSE)
    { System.out.println("SOURCE:"); System.out.println(resV1);
      System.out.println("TARGET:"); System.out.println(resV2);
    }    
    if(source!=null && target!=null)
    { ProductDiff diff = new ProductDiff((TypeProduct)resV1, (TypeProduct)resV2);
      for(; !diff.refine(); );
      if(!(Main.VERBOSE||VERBOSE) && (Main.DIFF||DIFF)) 
      System.out.println(diff.getSolution());
      if(HTMLCODE) writeHTML(diff.getSolution());
      if(Main.SIM||SIM) 
      System.out.println(diff.getSim().getPercentage());
    }
    final long endTime   = System.currentTimeMillis();
    final long totalTime = (endTime - startTime)/1000;
    System.out.println("duration:"+totalTime+"s");
  }  
}
