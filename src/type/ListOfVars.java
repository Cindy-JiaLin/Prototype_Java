package type;

  //This ListOfVars will be used in TYPE and ParseTYPEresult classes.
  //This ListOfVars is used to store listofVarNames in parseTYPE(ListOfVars, String) method.
  //This ListOfVars is used to store listofLabels in PRODUCT or UNION TYPE as well.
  public class ListOfVars 
  { private final String head;
    private final ListOfVars rest;
    public ListOfVars(){ this.head=null; this.rest=null;}// constructor for empty ListOfVars
    public ListOfVars(String head, ListOfVars rest){ this.head=head; this.rest=rest;}// constructor for non-empty ListOfVars
    @Override
    public String toString()
    { StringBuffer buf=new StringBuffer();
      buf.append("[");
      if(!isEmptyListOfStrings()){ dump(buf);}
      buf.append("]");
      return buf.toString();
    }
    private void dump(StringBuffer buf)
    { buf.append(this.head);
      if (!this.rest.isEmptyListOfStrings()){ buf.append(","); this.rest.dump(buf);}  
    }    
    @Override
    public boolean equals(Object obj)
    { if(obj instanceof ListOfVars)
      { ListOfVars that=(ListOfVars)obj;
        if(!this.isEmptyListOfStrings()&&!that.isEmptyListOfStrings())
        { return this.head.equals(that.head)&&this.rest.equals(that.rest);}
        else { return this.isEmptyListOfStrings()&&that.isEmptyListOfStrings();}
      }
      else{ throw new RuntimeException("This "+obj+" is not instance of ListOfStrings.");}
    }  
    
    public String head()
    { if(!this.isEmptyListOfStrings()) return this.head;
      else throw new RuntimeException("There is no head element in an empty ListOfStrings."); 
    }
    public ListOfVars rest()
    { if(!this.isEmptyListOfStrings()) return this.rest;
      else return new ListOfVars();
    }
    public boolean isEmptyListOfStrings(){ return this.head==null&&this.rest==null;}
    
    public int size()
    { if(!this.isEmptyListOfStrings()) return 1+this.rest.size();
      else return 0;
    }        
    public boolean hasStr(String newStr)
    { if(!this.isEmptyListOfStrings())
      { if(newStr.equals(this.head)) return true;
        else return this.rest.hasStr(newStr);  
      }
      else return false;
    }        
  
    public ListOfVars ins(String newStr){ return new ListOfVars(newStr, this);} 
    public ListOfVars append(String newStr)
    { if(!this.isEmptyListOfStrings())
      { if(!this.head.equals(newStr)) return new ListOfVars(this.head, this.rest.append(newStr)); 
        else return this;
      }
      else return new ListOfVars(newStr, this);
    }   
  }
