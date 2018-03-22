package diff;

import value.PrimBool;
import sim.Sim;

import dcprototype.Main;
import dcprototype.HTML;

public class PrimBoolDiff extends Diff 
{ private final PrimBool t1, t2;
  public PrimBoolDiff(PrimBool t1, PrimBool t2)
  { this.t1=t1; this.t2=t2;}  

  public String toString()
  { return (this.t1.equals(this.t2) ? "="+t1 : "-"+t1+", +"+t2)+this.getSim();}
  public String html()
  { return HTML.TD("")+
    (this.t1.equals(this.t2) ? HTML.TD(HTML.CPY, HTML.encode(t1.toString())): 
                               HTML.TD(HTML.DEL, HTML.encode(t1.toString()))+
                               HTML.TD(HTML.INS, HTML.encode(t2.toString()))
    );
  }
  public Sim getSim(){ return (this.t1.equals(this.t2) ? Sim.EQUAL(2) : Sim.DIFF(2));}

  public boolean isFinal(){ return true;}
  public boolean refine()
  { //System.out.println(this.toString()); 
    //System.out.println(this.getSim().getPercentage());
    return true;
  }
}
