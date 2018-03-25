package diff;

import value.PrimReal;
import sim.Sim;

import dcprototype.Main;
import dcprototype.HTML;

public class PrimRealDiff extends Diff 
{ private final PrimReal r1, r2;
  public PrimRealDiff(PrimReal r1, PrimReal r2)
  { if(!r1.hasSameAcc(r2)) 
      throw new RuntimeException("These two real numbers has different accuracy.");
    this.r1=r1; this.r2=r2;
  }  

  public String toString()
  { return (this.r1.isSimilar(this.r2) ? "chg("+r1+","+r2+")" : "rep(-"+r1+",+"+r2+")")+this.getSim();}
  public String html()
  { return HTML.TD("")+
    (this.r1.equals(this.r2) ? HTML.TD(HTML.CHG, HTML.encode("chg("+r1+","+r2+")")): 
                               HTML.TD(HTML.DEL, HTML.encode(r1.toString()))+
                               HTML.TD(HTML.INS, HTML.encode(r2.toString()))
    );
  }
  public Sim getSim(){ return (this.r1.equals(this.r2) ? Sim.EQUAL(2) : Sim.DIFF(2));}

  public boolean isFinal(){ return true;}
  public boolean refine()
  { //System.out.println(this.toString()); 
    //System.out.println(this.getSim().getPercentage());
    return true;
  }
}
