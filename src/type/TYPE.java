package type;

//import value.TypeList;
//import value.TypeMapping;
//import value.TypeMultiset;
//import value.TypeSet;
import value.TypeList;
//import utility.ListOfLabelandTYPEs;

public class TYPE
{ private final String name;
  private final String varName;//For VAR TYPE and REC TYPE
  private final double acc;//For REAL TYPE, this is an accuracy number to measure the real numbers.
  //private final ListOfLabelandTYPEs labelandTYPEs;//For PRODUCT, UNION and MAPPING TYPEs (the UNION TYPE only contains two TYPEs)
  private final TYPE T1, T2;//For SET,MSET,LIST and MAPPING
    
  private final static String sUNIT="UNIT";
  private final static String sBOOL="BOOL";
  private final static String sNAT="NAT";
  private final static String sREAL="REAL";
  private final static String sCHAR="CHAR";
  private final static String sSTRING="STRING";
  private final static String sPRODUCT="PRODUCT";
  private final static String sUNION="UNION";
  private final static String sLIST ="LIST";
  private final static String sSET="SET";
  private final static String sMSET ="MSET";
  private final static String sMAPPING="MAPPING";
  private final static String sVAR="VAR";
  private final static String sREC="REC";
       
  // PRIMITIVE, UNIT, BOOL, NAT, CHAR, STRING
  private TYPE(String name)
  { this.name=name; this.varName=null; this.acc=0; //this.labelandTYPEs=null; 
    this.T1=null; this.T2=null;}
  // REAL
  private TYPE(String name, double acc)
  { this.name=name; this.varName=null; this.acc=acc; //this.labelandTYPEs=null; 
    this.T1=null; this.T2=null;}        
  // VAR
  private TYPE(String name, String varName)
  { this.name=name; this.varName=varName; this.acc=0; //this.labelandTYPEs=null; 
    this.T1=null; this.T2=null;}
  //SET, MSET, LIST: name is sSET, sMSET, or sLIST
  //this TYPE is the baseTYPE of SET, MSET and LIST.
  private TYPE(String name, TYPE baseTYPE)
  { this.name=name; this.varName=null; this.acc=0; //this.labelandTYPEs=null; 
    this.T1=baseTYPE; this.T2=null;}
  //REC: name is sREC
  //bodyTYPE contains VAR(varName)
  private TYPE(String name, String varName, TYPE bodyTYPE)
  { this.name=name; this.varName=varName; this.acc=0; //this.labelandTYPEs=null; 
    this.T1=bodyTYPE; this.T2=null;}        
  //PRODUCT or UNION: name is sPRODUCT
  //MAPPING, labelandTYPEs.length==2
  //private TYPE(String name, ListOfLabelandTYPEs labelandTYPEs)
  //{ this.name=name; this.varName=null; this.acc=0; this.labelandTYPEs=labelandTYPEs; this.T1=null; this.T2=null;}       

  public final static TYPE UNIT=new TYPE(sUNIT);
  public final static TYPE BOOL=new TYPE(sBOOL);
  public final static TYPE NAT=new TYPE(sNAT);
  public final static TYPE CHAR=new TYPE(sCHAR);
  public final static TYPE STRING = new TYPE(sSTRING);
  
  public final static TYPE REAL(double acc){ return new TYPE(sREAL, acc);}
  
  //public final static TYPE PRODUCT(ListOfLabelandTYPEs labelandTYPEs){ return new TYPE(sPRODUCT,labelandTYPEs);}
  //public final static TYPE UNION(ListOfLabelandTYPEs labelandTYPEs){ return new TYPE(sUNION, labelandTYPEs);}
  //public final static TYPE MAPPING(ListOfLabelandTYPEs labelandTYPEs){ return new TYPE(sMAPPING, labelandTYPEs);}
  
  public final static TYPE SET(TYPE baseTYPE){ return new TYPE(sSET, baseTYPE);}
  public final static TYPE MSET(TYPE baseTYPE){ return new TYPE(sMSET, baseTYPE);}
  public final static TYPE LIST(TYPE baseTYPE){ return new TYPE(sLIST, baseTYPE);}
  
  
  public final static TYPE VAR(String varName){ return new TYPE(sVAR, varName);}
  public final static TYPE REC(String varName, TYPE TypeBody){ return new TYPE(sREC, varName, TypeBody);}
  
  public boolean isUNIT(){ return this.name.equals(sUNIT);}
  public boolean isBOOL(){ return this.name.equals(sBOOL);}
  public boolean isNAT(){ return this.name.equals(sNAT);}
  public boolean isREAL(){ return this.name.equals(sREAL);}
  public boolean isCHAR(){ return this.name.equals(sCHAR);}
  public boolean isSTRING(){ return this.name.equals(sSTRING);}
  public boolean isPRIMITIVE(){ return this.name.equals(sUNIT)
                                     ||this.name.equals(sBOOL)
                                     ||this.name.equals(sNAT)
                                     ||this.name.equals(sCHAR)
                                     ||this.name.equals(sSTRING);}
  
  public boolean isPRODUCT(){ return this.name.equals(sPRODUCT);}
  public boolean isUNION(){ return this.name.equals(sUNION);}
  public boolean isSET(){ return this.name.equals(sSET);}
  public boolean isMSET(){ return this.name.equals(sMSET);}
  public boolean isLIST(){ return this.name.equals(sLIST);}
  public boolean isMAPPING(){ return this.name.equals(sMAPPING);}
  
  public boolean isREC(){ return this.name.equals(sREC);}
  public boolean isVAR(){ return this.name.equals(sVAR);}
                  
  
  public double getAcc()
  { if(isREAL()){ return this.acc;}
  else{ throw new RuntimeException(this+" is not a REAL TYPE.");}
  }
  //Get the labelandTYPEs from the corresponding PRODUCT or UNION TYPE.
  //public ListOfLabelandTYPEs getMembers()
  //{ if(isPRODUCT()||isUNION()||isMAPPING()){ return this.labelandTYPEs;}
    //else { throw new RuntimeException(this+" is not a PRODUCT or UNION or MAPPING TYPE, we cannot get member TYPEs of them.");}
  //}       
  //Get the baseTYPE of SET, MSET, LIST
  public TYPE getBaseTYPE()
  { if(isSET()||isMSET()||isLIST()){ return this.T1;}
    else { throw new RuntimeException(this+" is not a SET, MSET or LIST TYPE, we cannot get the baseTYPE of them.");}  
  } 
  /* 
  private TypeList emptyList;
  public TypeList getEmptyList(){ if (emptyList==null) emptyList=new TypeList(this); return emptyList; }
  
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
  
       
  //GET domain and codomain TYPE in MAPPING
  public TYPE getDOM()
  { if(!isMAPPING()||this.labelandTYPEs.size()!=2) throw new RuntimeException(this+" is not a MAPPING TYPE.");
    else{ return this.labelandTYPEs.head().getTYPE();}
  }
  public TYPE getCOD()
  { if(!isMAPPING()||this.labelandTYPEs.size()!=2) throw new RuntimeException(this+" is not a MAPPING TYPE.");
    else{ return this.labelandTYPEs.rest().head().getTYPE();}
  }
  */
  //REC TYPE  
  //public final static TYPE REC(String varName, TYPE T){return new TYPE(sREC, varName, T);}
  public String getVarName()//return the varName not the sVAR.
  { if(!isREC()) throw new RuntimeException(this+" is not a REC TYPE.");
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
  @Override
  public boolean equals(Object obj)
  { if (obj instanceof TYPE)
    { TYPE that=(TYPE)obj;
      if(this.isPRIMITIVE()&&that.isPRIMITIVE()){ return this.name.equals(that.name);}
      else if(this.isREAL()&&that.isREAL()){ return this.acc==that.acc;}
      //For Structured TYPEs, if they are the same TYPE, the name of them should be equals to each other.
      //else if(this.isPRODUCT()&&that.isPRODUCT()){ return this.labelandTYPEs.equals(that.labelandTYPEs);}
      //else if(this.isUNION()&&that.isUNION()){ return this.labelandTYPEs.equals(that.labelandTYPEs);}
      //else if(this.isMAPPING()&&that.isMAPPING()){ return this.labelandTYPEs.equals(that.labelandTYPEs);}
      else if(this.isSET()&&that.isSET()){ return this.T1.equals(that.T1);}
      else if(this.isMSET()&&that.isMSET()){ return this.T1.equals(that.T1);}
      else if(this.isLIST()&&that.isLIST()){ return this.T1.equals(that.T1);}
      else if(this.isVAR()&&that.isVAR()){ return this.varName.equals(that.varName);}
      else if(this.isREC()||that.isREC())
      { if(this.isREC()&&that.isREC())
        { if(this.varName.equals(that.varName)){ return this.T1.equals(that.T1);}  
          //if the varNames are different, unify varNames with this.varName, e.g. REC(x:Tx) REC(y:Ty) maybe the same REC TYPE.
          else{ return this.equals(that.unifyVarName(that.varName, this.varName));} 
        }
        else if(this.isREC()){ return unfold(this).equals(that);}//!that.isREC()
        else { return this.equals(unfold(that));}//!this.isREC()
      } 
      else{ return false;}// They are different TYPEs
    }
    else{ throw new RuntimeException("This "+obj+" is not instance of TYPE.");}  
  }        
  @Override
  public String toString()
  { if(isPRIMITIVE()){ return this.name;}
    else if(isREAL()){ return this.name+"("+acc+")";}
    //else if(isPRODUCT()){ return this.name+"("+this.labelandTYPEs.toString("*")+")";}
    //else if(isUNION()){ return this.name+"("+this.labelandTYPEs.toString("|")+")";}
    else if(isSET()||isMSET()||isLIST()){ return this.name+"("+this.T1+")";}
    //else if(isMAPPING()){ return this.name+"("+this.labelandTYPEs.toString("=>")+")";}
    else if(isREC()){return this.name+"("+this.varName+":"+this.T1+")";}
    else if(isVAR()){ return this.name+"("+this.varName+")";}
    else{ throw new RuntimeException("There is no other type at this stage, TYPE toString().");} 
  }   
  //If this TYPE contains a VAR TYPE whose name is the varName
  //When the TYPE is REC TYPE, this contains() method is used to check 
  //Either it is a bound variable, i.e. this.varName==varName,
  //or it is a free variable, i.e. this.varName!=varName&&this.T1.contains(varName)
  public boolean contains(String varName)
  { if(isPRIMITIVE()){ return false;}
    else if(isREAL()){ return false;}
    //else if(isPRODUCT()||isUNION()||isMAPPING()){ return this.labelandTYPEs.contains(varName);}
    else if(isSET()||this.isMSET()||this.isLIST()){ return this.T1.contains(varName);}
    else if(isVAR()){ return (this.varName == null ? varName == null : this.varName.equals(varName));}//When the VAR TYPE is contains in TYPE body.
    else if(isREC()){ return (this.varName == null ? varName == null : this.varName.equals(varName))||this.T1.contains(varName);}
    else { throw new RuntimeException("There is no more TYPE at this stage, TYPE contains(varName).");}
  }   
  //Replace the origVarName by the targVarName
  //Find the origVarName in the specific TYPE, and replace it by the targVarName
  public TYPE unifyVarName(String origVarName, String targVarName)
  { if(!contains(origVarName)){ return this;}
    //else if(isPRODUCT()){ return PRODUCT(this.labelandTYPEs.unifyVarName(origVarName, targVarName));}
    //else if(isUNION()){ return UNION(this.labelandTYPEs.unifyVarName(origVarName, targVarName));}
    //else if(isMAPPING()){ return MAPPING(this.labelandTYPEs.unifyVarName(origVarName, targVarName));}
    else if(isSET()){ return SET(this.T1.unifyVarName(origVarName, targVarName));}
    else if(isMSET()){ return MSET(this.T1.unifyVarName(origVarName, targVarName));}
    else if(isLIST()){ return LIST(this.T1.unifyVarName(origVarName, targVarName));}
    else if(isVAR()){ return VAR(targVarName);}
    else if(isREC())
    { if(this.varName == null ? origVarName == null : this.varName.equals(origVarName)){ return REC(targVarName, this.T1.unifyVarName(origVarName, targVarName));}
      else{ return REC(this.varName, this.T1.unifyVarName(origVarName, targVarName));}
    }
    else { throw new RuntimeException("There is no more TYPE at this stage, TYPE unifyVarName(varName).");}  
  }       
  // varTYPE is the TYPE variable
  // T is the REC TYPE
  // Target is the TYPE contains the varName 
  // This method is used to replace the varName in Target by T
  public static TYPE substitute(String varName, TYPE T, TYPE Target)
  { if(!Target.contains(varName)){return Target;}
    else if(Target.isVAR()&&(Target.varName == null ? varName == null : Target.varName.equals(varName))){ return T;}
    //else if(Target.isPRODUCT()) { return PRODUCT(Target.labelandTYPEs.substitute(varName, T));}
    //else if(Target.isUNION()) { return UNION(Target.labelandTYPEs.substitute(varName, T));}  
    else if(Target.isREC())
    { if(Target.varName == null ? varName == null : Target.varName.equals(varName)){ return Target; }
      else{ return REC(Target.varName, substitute(varName, T, Target.T1));} 
      // not quite correct as T may contain Target.varName
      // in this case use unifyVarName 
    }
    else{ throw new RuntimeException("Currently, there is only these recursive constructor.");}    
  }        
  public static TYPE unfold(TYPE T)
  { if(T.isVAR()||T.isPRIMITIVE()||T.isREAL()){ return T;}
    //else if(T.isPRODUCT()){ return PRODUCT(T.labelandTYPEs.unfold());}
    //else if(T.isUNION()){ return UNION(T.labelandTYPEs.unfold());}  
    //else if(T.isMAPPING()){ return MAPPING(T.labelandTYPEs.unfold());}
    else if(T.isSET()){ return SET(unfold(T.getBaseTYPE()));}
    else if(T.isMSET()){ return MSET(unfold(T.getBaseTYPE()));}
    else if(T.isLIST()){ return LIST(unfold(T.getBaseTYPE()));}
    else if(T.isREC()){ return substitute(T.varName, T, T.getBodyTYPE());}
    else { throw new RuntimeException("There is no other type at this stage.");}    
  } 
}
