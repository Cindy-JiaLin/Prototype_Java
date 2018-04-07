package dcprototype;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import java.util.List;
import java.util.ArrayList;

import type.*;
import value.*;
import diff.*;

public class Main 
{ public static boolean VERBOSE = false;// if true all intermediate states will be logged
  public static boolean SIM = false;// displays similarity as percentage
  public static boolean DIFF = false;// displays difference as a solution (PartialSolution) 
  public static boolean INFO = false;// displays runtime statistics
    
  public static void main(String[] args) throws UnsupportedEncodingException
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

    TypeT model1 = model(resTYPE, resV1);
    TypeT model2 = model(resTYPE, resV2);
        
    if(VERBOSE)
    { System.out.println("SOURCE:"); System.out.println(resV1);
      System.out.println("TARGET:"); System.out.println(resV2);
    }    
    if(source!=null && target!=null)
    { if(resTYPE.isUNIT()||resTYPE.isBOOL()||resTYPE.isCHAR()||
         resTYPE.isNAT()||resTYPE.isINT()||resTYPE.isREAL())
      { PrimDiff diff = new PrimDiff(resV1, resV2);
        for(; !diff.refine(); );
        if(!(VERBOSE) && (DIFF)) 
           Encoding.printUnicode(""+diff.getSolution());
        if(SIM) 
           System.out.println(diff.getSim().getPercentage());
      }
      else if(resTYPE.isSTRING())
      { PrimStringDiff diff = new PrimStringDiff((PrimString)resV1, (PrimString)resV2);
        for(; !diff.refine(); );
        if(!(VERBOSE) && (DIFF)) 
           Encoding.printUnicode(""+diff.getSolution());
        if(SIM) 
           System.out.println(diff.getSim().getPercentage());
      }
      else if(resTYPE.isPRODUCT())
      { ProductDiff diff = new ProductDiff((TypeProduct)resV1, (TypeProduct)resV2);
        for(; !diff.refine(); );
        if(!(VERBOSE) && (DIFF)) 
           System.out.println(diff.getSolution());
        if(SIM) 
          System.out.println(diff.getSim().getPercentage());
      }
      else if(resTYPE.isUNION())
      { UnionDiff diff = new UnionDiff((TypeUnion)resV1, (TypeUnion)resV2);
        for(; !diff.refine(); );
        if(!(VERBOSE) && (DIFF)) 
           System.out.println(diff.getSolution());
        if(SIM) 
          System.out.println(diff.getSim().getPercentage());
      }
      else if(resTYPE.isLIST())
      { TypeList list1 = (TypeList)resV1;
        TypeList list2 = (TypeList)resV2;
        if(list1.isEmptyList()&&list2.isEmptyList())
          System.out.println(Console.cpy(""+list1));
        else
        { ListDiff diff = new ListDiff(list1, list2);
          for(; !diff.refine(); );
          if(!(VERBOSE) && (DIFF)) 
            System.out.println(diff.getSolution());
          if(SIM) 
            System.out.println(diff.getSim().getPercentage());
        }
      }
      else if(resTYPE.isSET())
      { TypeSet set1 = (TypeSet)resV1;
        TypeSet set2 = (TypeSet)resV2;
        if(set1.isEmptySet()&&set2.isEmptySet())
          System.out.println(Console.cpy(""+set1));
        else
        { SetDiff diff = new SetDiff(set1, set2);
          for(; !diff.refine(); );
          if(!(VERBOSE) && (DIFF)) 
             System.out.println(diff.getSolution());
          if(SIM) 
            System.out.println(diff.getSim().getPercentage());
        }
      }

    }  
    final long endTime   = System.currentTimeMillis();
    final long totalTime = (endTime - startTime)/1000;
    System.out.println("duration:"+totalTime+"s");
  }  
  // model values with their TYPE
  public static TypeT model(TYPE T, TypeT t)
  { //model primitive types one by one
    if(T.isUNIT() && t.typeOf().isUNIT())
    { return new PrimUnit(T);
    }  
    else if(T.isBOOL() && t.typeOf().isBOOL()) 
    { PrimBool v = (PrimBool)t;
      return new PrimBool(T, v.getValue());
    }
    else if(T.isCHAR() && t.typeOf().isCHAR())
    { PrimChar v = (PrimChar)t;
      return new PrimChar(T, v.getValue());
    } 
    else if(T.isSTRING() && t.typeOf().isSTRING())
    { PrimString v = (PrimString)t;
      return new PrimString(T, v.getValue());
    }    
    else if(T.isNAT() && t.typeOf().isNAT())
    { PrimNat v = (PrimNat)t;
      return new PrimNat(T, v.getValue());
    }
    else if(T.isINT() && t.typeOf().isINT())
    { PrimInt v = (PrimInt)t;
      return new PrimInt(T, v.getValue());
    }  
    else if(T.isREAL() && t.typeOf().isREAL())
    { PrimReal v = (PrimReal)t;
      return new PrimReal(T, v.getValue());
    } 
    // model structured types one by one
    else if(T.isPRODUCT() && t.typeOf().isPRODUCT() && T.equals(t.typeOf()))
    { TypeProduct v =(TypeProduct)t;
      return new TypeProduct(T, v.getLabels(), v.getValues());
    }   
    else if(T.isUNION() && t.typeOf().isUNION() && T.equals(t.typeOf()))
    { TypeUnion v = (TypeUnion)t;
      return new TypeUnion(T, v.getLabel(), v.getValue());
    }    
    else if(T.isREC() && TYPE.unfold(T).equals(t.typeOf()))
    { return new TypeRec(T, t);}    
    else if(T.isLIST() && t.typeOf().isLIST() && T.getBaseTYPE().equals(t.typeOf().getBaseTYPE()))
    { TypeList v = (TypeList) t;
      if(v.isEmptyList()) return new TypeList(T.getBaseTYPE());
      else{ return new TypeList(T.getBaseTYPE(), v.getValue());}
    }
    else if(T.isSET() && t.typeOf().isSET() && T.getBaseTYPE().equals(t.typeOf().getBaseTYPE()))
    { TypeSet v = (TypeSet) t;
      if(v.isEmptySet()) return new TypeSet(T.getBaseTYPE());
      else{ return new TypeSet(T.getBaseTYPE(), v.getValue());}
    }    
    else if(T.isMSET() && t.typeOf().isMSET() && T.getBaseTYPE().equals(t.typeOf().getBaseTYPE()))
    { TypeMultiset v = (TypeMultiset)t;
      if(v.isEmptyMultiset()) return new TypeMultiset(T.getBaseTYPE());
        else { return new TypeMultiset(T.getBaseTYPE(), v.getValue());}
    }
    else if(T.isMAPPING() && t.typeOf().isMAPPING() && T.equals(t.typeOf()))
    { TypeMapping v = (TypeMapping)t;
      if(v.isEmptyMapping()) return new TypeMapping(T.getDOM(), T.getCOD());
      else{ return new TypeMapping(T.getDOM(), T.getCOD(), v.getDomFst(), v.getCodFst(), v.getRest());}
    }  
    else { throw new RuntimeException("There is no other TYPE currently");}
  }        
}
