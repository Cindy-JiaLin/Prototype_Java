package type;

import java.util.List;
import java.util.ArrayList;

import value.TypeT;

public class TYPE
{ private final String name;
  private final String varName;//For VAR TYPE and REC TYPE
  private final double acc;//For REAL TYPE
  private final List<String> labels;//For PRODUCT, UNION
  private final List<TYPE> TYPEs;// For PRODUCT, UNION 
  private final TYPE T1, T2;//For SET,MSET,LIST and MAPPING
    
  private final static String sUNIT="UNIT";
  private final static String sBOOL="BOOL";
  private final static String sCHAR="CHAR";
  private final static String sSTRING="STRING";
  private final static String sNAT="NAT";
  private final static String sINT="INT";
  private final static String sREAL="REAL";
  private final static String sPRODUCT="PRODUCT";
  private final static String sUNION="UNION";
  private final static String sVAR="VAR";
  private final static String sREC="REC";
  private final static String sLIST ="LIST";
  private final static String sSET="SET";
  private final static String sMSET ="MSET";
  private final static String sMAPPING="MAPPING";
         
  // PRIMITIVE, UNIT, BOOL, NAT, INT, CHAR, STRING, REAL(without acc)
  private TYPE(String name)
  { this.name=name; this.varName=null; this.acc=1.0; this.labels=null; this.TYPEs=null; 
    this.T1=null; this.T2=null;
  }
  // REAL
  private TYPE(String name, double acc)
  { this.name=name; this.varName=null; this.acc=acc; this.labels=null; this.TYPEs=null; 
    this.T1=null; this.T2=null;
  }        
  // VAR
  private TYPE(String name, String varName)
  { this.name=name; this.varName=varName; this.acc=0; this.labels=null; this.TYPEs=null; 
    this.T1=null; this.T2=null;
  }
  //LIST, SET, MSET: name is sLIST, sSET, sMSET respectively
  //this TYPE is the baseTYPE of LIST, SET and MSET.
  private TYPE(String name, TYPE baseTYPE)
  { this.name=name; this.varName=null; this.acc=0; this.labels=null; this.TYPEs=null; 
    this.T1=baseTYPE; this.T2=null;
  }
  //REC: name is sREC
  //bodyTYPE contains VAR(varName)
  private TYPE(String name, String varName, TYPE bodyTYPE)
  { this.name=name; this.varName=varName; this.acc=0; this.labels=null; this.TYPEs=null; 
    this.T1=bodyTYPE; this.T2=null;
  }  
  //MAPPING name is sMAPPING
  private TYPE(String name, TYPE T1, TYPE T2)
  { this.name=name; this.varName=null; this.acc=0; this.labels=null; this.TYPEs=null;    
    this.T1=T1; this.T2=T2;
  }      
  //PRODUCT name is sPRODUCT
  //UNION name is sUNION
  private TYPE(String name, List<String> labels, List<TYPE> TYPEs)
  { if(labels.size()!=TYPEs.size()) 
      throw new RuntimeException("In PRODUCT or UNION, the number of labels and TYPEs must be the same.");
    if(TYPEs.isEmpty()) 
      throw new RuntimeException("PRODUCT or UNION has at least one Element.");
    this.name=name; this.varName=null; this.acc=0; this.labels=labels; this.TYPEs=TYPEs; 
    this.T1=null; this.T2=null;
  }
         
  public final static TYPE UNIT=new TYPE(sUNIT);
  public final static TYPE BOOL=new TYPE(sBOOL);
  public final static TYPE CHAR=new TYPE(sCHAR);
  public final static TYPE STRING = new TYPE(sSTRING);
  public final static TYPE NAT=new TYPE(sNAT);
  public final static TYPE INT=new TYPE(sINT);
  public final static TYPE REAL=new TYPE(sREAL);// without acc, the default acc==1.0
  public final static TYPE REAL(double acc){ return new TYPE(sREAL, acc);}
  
  public final static TYPE PRODUCT(List<String> labels, List<TYPE> TYPEs)
  { return new TYPE(sPRODUCT, labels, TYPEs);}
  public final static TYPE UNION(List<String> labels, List<TYPE> TYPEs)
  { return new TYPE(sUNION, labels, TYPEs);}

  public final static TYPE VAR(String varName){ return new TYPE(sVAR, varName);}
  public final static TYPE REC(String varName, TYPE TypeBody){ return new TYPE(sREC, varName, TypeBody);}
  
  public final static TYPE LIST(TYPE baseTYPE){ return new TYPE(sLIST, baseTYPE);}
  public final static TYPE SET(TYPE baseTYPE){ return new TYPE(sSET, baseTYPE);}
  public final static TYPE MSET(TYPE baseTYPE){ return new TYPE(sMSET, baseTYPE);}
  public final static TYPE MAPPING(TYPE T1, TYPE T2){ return new TYPE(sMAPPING, T1, T2);}
  

  public boolean isUNIT(){ return this.name.equals(sUNIT);}
  public boolean isBOOL(){ return this.name.equals(sBOOL);}
  public boolean isNAT(){ return this.name.equals(sNAT);}
  public boolean isINT(){ return this.name.equals(sINT);}
  public boolean isREAL(){ return this.name.equals(sREAL);}
  public boolean isCHAR(){ return this.name.equals(sCHAR);}
  public boolean isSTRING(){ return this.name.equals(sSTRING);}
  // REAL TYPE is not in this method, since when compare REAL TYPE the acc needs to be considered.
  // For example in equals method
  // REAL is not in, since the accuracy needs to be compared in equals method
  // STRING is not in, since when compute the Diff, the PrimStringDiff is discussed separately with PrimDiff
  public boolean isPRIMITIVE(){ return this.name.equals(sUNIT)
                                     ||this.name.equals(sBOOL)
                                     ||this.name.equals(sCHAR)
                                     ||this.name.equals(sNAT)
                                     ||this.name.equals(sINT);
                              }
  
  public boolean isPRODUCT(){ return this.name.equals(sPRODUCT);}
  public boolean isUNION(){ return this.name.equals(sUNION);}
 
  public boolean isREC(){ return this.name.equals(sREC);}
  public boolean isVAR(){ return this.name.equals(sVAR);}
 
  public boolean isLIST(){ return this.name.equals(sLIST);}
  public boolean isSET(){ return this.name.equals(sSET);}
  public boolean isMSET(){ return this.name.equals(sMSET);}
  public boolean isMAPPING(){ return this.name.equals(sMAPPING);}
  
  // for REAL
  public double getAcc()
  { if(isREAL()){ return this.acc;}
    else{ throw new RuntimeException(this+" is not a REAL TYPE.");}
  }
  // Get the labels from the corresponding PRODUCT or UNION TYPE.
  public List<String> getLabels()
  { if(isPRODUCT()||isUNION()){ return this.labels;}
    else throw new RuntimeException(this+" is not a PRODUCT or UNION.");
  }
  // Get the TYPEs from the corresponding PRODUCT or UNION TYPE. 
  public List<TYPE> getTYPEs()
  { if(isPRODUCT()||isUNION()){ return this.TYPEs;}
    else throw new RuntimeException(this+" is not a PRODUCT UNION.");
  }  

  public boolean hasSameLabels(List<String> labels){ return this.labels.equals(labels);}
  public boolean hasSameTYPEs(List<TypeT> TypeTs)
  { for(int i=0; i<this.TYPEs.size(); i++)
    { if(!this.TYPEs.get(i).equals(TypeTs.get(i).typeOf()))
        return false;
    } 
    return true;
  }
  public TYPE getTYPE(String label)//
  { if(!this.labels.contains(label)) 
      throw new RuntimeException("This label="+label+" is not existing.");
    else
    { int j=0;
      for(int i=0; i<this.labels.size(); i++)
        if(this.labels.get(i).equals(label)) { j=i; break;}
      return this.TYPEs.get(j);
    }
  }
  // VAR TYPE
  // REC TYPE  
  // public final static TYPE REC(String varName, TYPE T){return new TYPE(sREC, varName, T);}
  public String getVarName()//return the varName not the sVAR.
  { if(!isVAR()||!isREC()) throw new RuntimeException(this+" is not a VAR or REC TYPE.");
    return this.varName;
  }  
  public TYPE getVAR()
  { if(!isREC()) throw new RuntimeException(this+" is not a REC TYPE.");
    return VAR(this.varName);
  }
  public TYPE getBodyTYPE()
  { if(!isREC()) throw new RuntimeException(this+" is not a REC TYPE.");
    return T1;
  } 

  // Get the baseTYPE of LIST, SET, MSET
  public TYPE getBaseTYPE()
  { if(isLIST()||isSET()||isMSET()){ return this.T1;}
    else { throw new RuntimeException(this+" is not a LIST, SET or MSET TYPE, we cannot get the baseTYPE of them.");}  
  }
  //GET domain and codomain TYPE in MAPPING
  public TYPE getDOM()
  { if(isMAPPING()) return this.T1;
    else throw new RuntimeException(this+" is not a MAPPING TYPE.");
  }
  public TYPE getCOD()
  { if(isMAPPING()) return this.T2;
    else throw new RuntimeException(this+" is not a MAPPING TYPE.");
  }
 
  /* 
  private TypeSet emptySet;
  public TypeSet getEmptySet()
  { if (emptySet==null) emptySet=new TypeSet(this);
    return emptySet; 
  }
  
  private TypeMultiset emptyMultiset;
  public TypeMultiset getEmptyMultiset()
  { if (emptyMultiset==null) emptyMultiset=new TypeMultiset(this);
    return emptyMultiset; 
  }
  
  private TypeMapping emptyMapping;
  public TypeMapping getEmptyMapping()
  { if (emptyMapping==null) emptyMapping=new TypeMapping(this);
    return emptyMapping; 
  }
  */
       
  public boolean equals(Object obj)
  { if (obj instanceof TYPE)
    { TYPE that=(TYPE)obj;
      if(this.isPRIMITIVE()&&that.isPRIMITIVE()){ return this.name.equals(that.name);}
      else if(this.isSTRING()&&that.isSTRING()){ return this.name.equals(that.name);}
      else if(this.isREAL()&&that.isREAL()){ return Math.abs(this.acc-that.acc)<1.0e-6;}
      //For Structured TYPEs, if they are the same TYPE, 
      //the name of them should be equals to each other.
      else if(this.isPRODUCT()&&that.isPRODUCT())
      { return this.labels.equals(that.labels)&&this.TYPEs.equals(that.TYPEs);}
      else if(this.isUNION()&&that.isUNION())
      { return this.labels.equals(that.labels)&&this.TYPEs.equals(that.TYPEs);}
      else if(this.isVAR()&&that.isVAR()){ return this.varName.equals(that.varName);}
      else if(this.isREC()||that.isREC())
      { if(this.isREC()&&that.isREC())
        { if(this.varName.equals(that.varName)){ return this.T1.equals(that.T1);}  
          //if the varNames are different, unify varNames with this.varName, 
          //e.g. REC(x:Tx) REC(y:Ty) maybe the same REC TYPE.
          else{ return this.equals(that.unifyVarName(that.varName, this.varName));} 
        }
        else if(this.isREC()){ return unfold(this).equals(that);}//!that.isREC()
        else { return this.equals(unfold(that));}//!this.isREC()
      } 
      else if(this.isMAPPING()&&that.isMAPPING())
      { return this.T1.equals(that.T1)&&this.T2.equals(that.T2);}
      else if(this.isLIST()&&that.isLIST()){ return this.T1.equals(that.T1);}
      else if(this.isSET()&&that.isSET()){ return this.T1.equals(that.T1);}
      else if(this.isMSET()&&that.isMSET()){ return this.T1.equals(that.T1);}
      else { return false;}// They are different TYPEs
    }
    else{ throw new RuntimeException("This "+obj+" is not instance of TYPE.");}  
  }        
  
  public String toString()
  { if(isPRIMITIVE()){ return this.name;}
    else if(isSTRING()){ return this.name;}
    else if(isREAL()){ return this.name+"("+acc+")";}
    else if(isPRODUCT())
    { StringBuffer buf = new StringBuffer();
      buf.append("{");
      for(int i=0; i<this.TYPEs.size(); i++)
      { if(!labels.get(i).equals("nolabel")){ buf.append(this.labels.get(i)); buf.append(".");}
        buf.append(this.TYPEs.get(i));
        if(i<this.TYPEs.size()-1) buf.append("*");
      }
      buf.append("}");
      return buf.toString();
    }
    else if(isUNION())
    { StringBuffer buf = new StringBuffer();
      buf.append("[");
      for(int i=0; i<this.TYPEs.size(); i++)
      { buf.append(this.labels.get(i));
        if(!this.TYPEs.get(i).isUNIT()) { buf.append(".");buf.append(this.TYPEs.get(i));}
        if(i<this.TYPEs.size()-1) buf.append("|");
      }
      buf.append("]");
      return buf.toString();
    }
    else if(isREC()){return this.name+"("+this.varName+":"+this.T1+")";}
    else if(isVAR()){ return this.varName;}
    else if(isLIST()||isSET()||isMSET()){ return this.name+"("+this.T1+")";}
    else if(isMAPPING()){ return this.T1+"=>"+this.T2;}
    else{ throw new RuntimeException("There is no other type at this stage, TYPE toString().");} 
  }   
  //If this TYPE contains a VAR TYPE whose name is the varName
  //When the TYPE is REC TYPE, this contains() method is used to check 
  //Either it is a bound variable, i.e. this.varName==varName,
  //or it is a free variable, i.e. this.varName!=varName&&this.T1.contains(varName)
  public boolean contains(String varName)
  { if(isPRIMITIVE()){ return false;}
    else if(isSTRING()){ return false;}
    else if(isREAL()){ return false;}
    else if(isPRODUCT()||isUNION())
    { for(int i=0; i<this.TYPEs.size(); i++)
         if(this.TYPEs.get(i).contains(varName)) return true;
      return false;
    }
    else if(isVAR())
    { return (this.varName == null ? varName == null : this.varName.equals(varName));}
    //When the VAR TYPE is contains in TYPE body.
    else if(isREC())
    { return (this.varName == null ? varName == null : this.varName.equals(varName))||this.T1.contains(varName);}
    else if(this.isLIST()||isSET()||this.isMSET()){ return this.T1.contains(varName);}
    else if(this.isMAPPING()){ return this.T1.contains(varName)&&this.T2.contains(varName);}
    else { throw new RuntimeException("There is no more TYPE at this stage, TYPE contains(varName).");}
  }   
  //Replace the origVarName by the targVarName
  //Find the origVarName in the specific TYPE, and replace it by the targVarName
  public TYPE unifyVarName(String origVarName, String targVarName)
  { if(!contains(origVarName)){ return this;}
    else if(isPRODUCT())
    { List<TYPE> newTYPEs=new ArrayList<TYPE>();
      for(int i=0; i<this.TYPEs.size(); i++)
        newTYPEs.add(this.TYPEs.get(i).unifyVarName(origVarName, targVarName));
      return PRODUCT(this.labels, newTYPEs);
    }
    else if(isUNION())
    { List<TYPE> newTYPEs=new ArrayList<TYPE>();
      for(int i=0; i<this.TYPEs.size(); i++)
        newTYPEs.add(this.TYPEs.get(i).unifyVarName(origVarName, targVarName));
      return UNION(this.labels, newTYPEs);
    }
    else if(isVAR()){ return VAR(targVarName);}
    else if(isREC())
    { if(this.varName == null ? origVarName == null : this.varName.equals(origVarName)){ return REC(targVarName, this.T1.unifyVarName(origVarName, targVarName));}
      else{ return REC(this.varName, this.T1.unifyVarName(origVarName, targVarName));}
    }
    else if(isLIST()){ return LIST(this.T1.unifyVarName(origVarName, targVarName));}
    else if(isSET()){ return SET(this.T1.unifyVarName(origVarName, targVarName));}
    else if(isMSET()){ return MSET(this.T1.unifyVarName(origVarName, targVarName));}
    else if(isMAPPING()){ return MAPPING(this.T1.unifyVarName(origVarName, targVarName), 
                                         this.T2.unifyVarName(origVarName, targVarName));}
    else { throw new RuntimeException("There is no more TYPE at this stage, TYPE unifyVarName(varName).");}  
  }       
  // varTYPE is the TYPE variable
  // T is the REC TYPE
  // Target is the TYPE contains the varName 
  // This method is used to replace the varName in Target by T
  public static TYPE substitute(String varName, TYPE T, TYPE Target)
  { if(!Target.contains(varName)){return Target;}
    else if(Target.isVAR()&&(Target.varName == null ? varName == null : Target.varName.equals(varName)))
    { return T;}
    else if(Target.isPRODUCT()) 
    { List<TYPE> newTYPEs=new ArrayList<TYPE>();
      for(int i=0; i<Target.getTYPEs().size(); i++)
        newTYPEs.add(substitute(varName, T, Target.getTYPEs().get(i)));
      return PRODUCT(Target.getLabels(), newTYPEs);
    }
    else if(Target.isUNION()) 
    { List<TYPE> newTYPEs=new ArrayList<TYPE>();
      for(int i=0; i<Target.getTYPEs().size(); i++)
        newTYPEs.add(substitute(varName, T, Target.TYPEs.get(i)));
      return UNION(Target.getLabels(), newTYPEs);
    }
    else if(Target.isREC())
    { if(Target.varName == null ? varName == null : Target.varName.equals(varName)){ return Target; }
      else{ return REC(Target.varName, substitute(varName, T, Target.T1));} 
      // not quite correct as T may contain Target.varName
      // in this case use unifyVarName 
    }
    else{ throw new RuntimeException("Currently, there is only these recursive constructor.");}    
  }        
  public static TYPE unfold(TYPE T)
  { if(T.isVAR()||T.isPRIMITIVE()||T.isSTRING()||T.isREAL()){ return T;}
    else if(T.isPRODUCT())
    { List<TYPE> newTYPEs=new ArrayList<TYPE>();
      for(int i=0; i<T.getTYPEs().size(); i++)
        newTYPEs.add(unfold(T.getTYPEs().get(i)));
      return PRODUCT(T.getLabels(), newTYPEs);
    }
    else if(T.isUNION())
    { List<TYPE> newTYPEs=new ArrayList<TYPE>();
      for(int i=0; i<T.getTYPEs().size(); i++)
        newTYPEs.add(unfold(T.getTYPEs().get(i)));
      return UNION(T.getLabels(), newTYPEs);
    }
    else if(T.isREC()){ return substitute(T.varName, T, T.getBodyTYPE());}
    else if(T.isLIST()){ return LIST(unfold(T.getBaseTYPE()));}
    else if(T.isSET()){ return SET(unfold(T.getBaseTYPE()));}
    else if(T.isMSET()){ return MSET(unfold(T.getBaseTYPE()));}
    else if(T.isMAPPING()){ return MAPPING(unfold(T.getDOM()), unfold(T.getCOD()));}
    else { throw new RuntimeException("There is no other type at this stage.");}    
  } 
}
