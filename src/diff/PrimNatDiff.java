package diff;

import value.PrimNat;
import sim.Sim;

import dcprototype.Main;
import dcprototype.HTML;

public class PrimNatDiff extends Diff 
{ private final PrimNat n1, n2;
  public PrimNatDiff(PrimNat n1, PrimNat n2){ this.n1=n1; this.n2=n2;}  

  public String toString()
  { return (this.n1.equals(this.n2) ? "="+n1 : "-"+n1+", +"+n2)+this.getSim();}
  public String html()
  { return HTML.TD("")+
    (this.n1.equals(this.n2) ? HTML.TD(HTML.CPY, HTML.encode(n1.toString())): 
                               HTML.TD(HTML.DEL, HTML.encode(n1.toString()))+
                               HTML.TD(HTML.INS, HTML.encode(n2.toString()))
    );
  }
  public Sim getSim(){ return (this.n1.equals(this.n2) ? Sim.EQUAL(2) : Sim.DIFF(2));}

  public boolean isFinal(){ return true;}
  public boolean refine()
  { //System.out.println(this.toString()); 
    //System.out.println(this.getSim().getPercentage());
    return true;
  }
}
