package diff;

import sim.Sim;
import type.*;
import dcprototype.*;

import value.TypeT;
import value.PrimReal;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import java.util.List;
import java.util.ArrayList;


// Diff between PrimUnit, PrimBool, PrimChar, PrimNat, PrimInt
public class PrimDiff extends Diff 
{ private final TypeT a, b;
  public PrimDiff(TypeT a, TypeT b)
  { this.a=a; this.b=b;}  

  public TypeT getSourceValue(){ return this.a;}
  public TypeT getTargetValue(){ return this.b;}
  public Sim getUnknown(){ return Sim.UNKNOWN(this.a.weight()+this.b.weight());}
  public String getSolution(){ return toString();}
  public String toString()
  { TYPE t = this.a.typeOf();
    if(t.isPRIMITIVE())
    { if(this.a.equals(this.b)) return Console.cpy(""+this.a);
      else return Console.del(""+this.a)+Console.ins(""+this.b);
    }
    else if(t.isREAL())
    { PrimReal r1=(PrimReal)this.a;
      PrimReal r2=(PrimReal)this.b;
      double acc = r1.typeOf().getAcc();
      if(r1.isSimilar(r2)) return Console.chg(""+this.a+Encoding.APPROX+this.b);
      else return Console.chg(Console.del(""+this.a)+Console.ins(""+this.b));
    }
    else throw new RuntimeException("Type error, must be the primitive type.");
  }
  public String html()
  { TYPE t = this.a.typeOf();
    String rows = "";// only one row contains two cells
    if(t.isPRIMITIVE())
    { if(this.a.equals(this.b)) rows = HTML.TR((SIM ? HTML.TD("100%") : "")+
                                                      HTML.TD(HTML.CPY, HTML.encode(""+this.a)));
      else rows = HTML.TR((SIM ? HTML.TD("0%") : "")+
                                 HTML.TD(HTML.DEL, HTML.encode(""+this.a))+
                                 HTML.TD(HTML.INS, HTML.encode(""+this.b)));
      return HTML.TABLE(rows);
    }
    else if(t.isREAL())
    { PrimReal r1=(PrimReal)this.a;
      PrimReal r2=(PrimReal)this.b;
      double acc = r1.typeOf().getAcc();
      if(r1.isSimilar(r2)) rows = HTML.TR((SIM ? HTML.TD(""+this.getSim().getPercentage1()) : "")+
                                                 HTML.TD(HTML.CHG, HTML.encode(""+this.a+HTML.encode('~')+this.b)));
      else HTML.TR((SIM ? HTML.TD("0%") : "")+
                          HTML.TD(HTML.DEL, HTML.encode(""+this.a))+
                          HTML.TD(HTML.INS, HTML.encode(""+this.b)));
      return HTML.TABLE(rows);
    }
    else throw new RuntimeException("Type error, must be the primitive type.");
  }

  public Sim getSim()
  { TYPE t = this.a.typeOf();
    if(t.isPRIMITIVE())
    { if(this.a.equals(this.b)) return Sim.EQUAL(2);
      else return Sim.DIFF(2);
    }
    else if(t.isREAL())
    { PrimReal r1=(PrimReal)this.a;
      PrimReal r2=(PrimReal)this.b;
      double acc = r1.typeOf().getAcc();
      double simR = 1-(Math.abs(r1.getValue()-r2.getValue())/acc);
      if(r1.isSimilar(r2)) return new Sim(2*simR, 2*simR, 2);
      else return Sim.DIFF(2);
    }
    else throw new RuntimeException("Type error, must be the primitive type.");
  }

  public boolean isFinal(){ return true;}
  public boolean refine(){ return true;}

  private static String htmlFileName;
  
  private static void writeHTML(String table)
  { if(htmlFileName!=null)
    try
    { FileWriter out = new FileWriter(htmlFileName);
      out.write(HTML.BODY(table));
      out.flush();
      out.close();
    }  
    catch(IOException e){ System.err.println("Promblem writing to:"+htmlFileName);}
  }    

  public static boolean VERBOSE = false;// if true all intermediate states will be logged
  public static boolean SIM = false;// displays similarity as percentage
  public static boolean DIFF = false;// displays difference as a solution (PartialSolution) 
  public static boolean INFO = false;// displays runtime statistics
  public static boolean HTMLCODE=false;// displays difference as text

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

    if(VERBOSE)
    { System.out.println("SOURCE:"); System.out.println(source);
      System.out.println("TARGET:"); System.out.println(target);
    }
    if(source!=null && target!=null)
    { PrimDiff diff = new PrimDiff(resV1, resV2);
      for(; !diff.refine(); );
      if(!VERBOSE && DIFF) System.out.println(""+diff);
      if(HTMLCODE) writeHTML(diff.html());
      if(SIM) System.out.println(diff.getSim().getPercentage());
    } 
    final long endTime   = System.currentTimeMillis();
    final long totalTime = (endTime - startTime)/1000;
    System.out.println("duration:"+totalTime+"s"); 
  }  

}
