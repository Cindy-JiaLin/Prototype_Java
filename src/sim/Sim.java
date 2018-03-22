package sim;

public final class Sim implements Comparable<Sim>
{ // lwb=num1/den
  // upb=num2/den
  private final int num1, num2, den;//den stands for denominator
  private Sim(int num1, int num2, int den){ this.num1=num1; this.num2=num2; this.den=den;}
  public String toString(){ return (isUnknown() ? "[0,1]" : "["+this.num1+"/"+this.den+","+this.num2+"/"+this.den+"]");}
  
  public int getDecrement(){ return den-num2;}
  public int getIncrement(){ return num1;}
  
  public final static Sim UNKNOWN(int n){ return new Sim(0,n,n);}

  public final static Sim EQUAL(int n){ return new Sim(n,n,n);}
  public final static Sim DIFF(int n){ return new Sim(0,0,n);}

  public Sim inc(int n){ return new Sim(this.num1+n, this.num2, this.den);}
  public Sim dec(int n){ return new Sim(this.num1, this.num2-n, this.den);}
  
  public boolean isFinal(){ return this.num1==this.num2;}
  public boolean isEqual(){ return this.num1==this.den && this.num2==this.den;}
  public boolean isUnknown(){ return this.num1==0 && this.num2==this.den;}
  
  public String getPercentage(int num, int den)
  { if(num==den) return "100%";
    else 
    { String s=""+(int)(100000*(1.0+(1.0*num)/den));
      return s.substring(1,3)+"."+s.substring(3,6)+"%";// 3 decimal
    }
  }  
  public String getPercentage1(int num, int den)
  { if(num==den) return "100%";
    else 
    { String s=""+(int)(100000*(1.0+(1.0*num)/den));
      return s.substring(1,3)+"."+s.substring(3,4)+"%";// 1 decimal
    }
  } 
  public String getPercentage0(int num, int den)
  { if(num==den) return "100%";
    else 
    { String s=""+(int)(100000*(1.0+(1.0*num)/den));
      return s.substring(1,3)+"%";// 0 decimal
    }
  } 
  public String getPercentage()
  { if(num1==num2) return getPercentage(num1,den);
    else return getPercentage(num2,den)+".."+getPercentage(num2,den);
  } 
  public String getPercentage1()
  { if(num1==num2) return getPercentage1(num1,den);
    else return getPercentage(num2,den)+".."+getPercentage(num2,den);
  }         
  public String getPercentage0()
  { if(num1==num2) return getPercentage0(num1,den);
    else return getPercentage(num2,den)+".."+getPercentage(num2,den);
  } 
  
  // used to sort partial solutions upb first then lwb
  public int compareTo(Sim sim)
  { if(sim==null) return 1;
    else if(this.den==sim.den)//the normal case
    { if(this.num2<sim.num2) return -1;
      else if(this.num2>sim.num2) return +1;
      else // this.num2==sim.num2
      if(this.num1<sim.num1) return -1;
      else if(this.num1>sim.num1) return +1;
      else // this.num1==sim.num1
      return 0;
    }  
    else
    { int t2=this.num2*sim.den;// t1--this.num1, t2--this.num2
      int s2=sim.num2*this.den;// s1 --sim.num1, s2 --sim.num2 
      if(t2<s2) return -1;
      else if(t2>s2) return +1;
      else// t2==s2
      { int t1=this.num1*sim.den;
        int s1=sim.num1*this.den;
        if(t1<s1) return -1;
        else if(t1>s2) return +1;
        else return 0;
      }
    }    
  }        
}
