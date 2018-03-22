package diff;

import value.PrimUnit;
import sim.Sim;

import dcprototype.Main;
import dcprototype.HTML;

public class PrimUnitDiff extends Diff 
{ private final PrimUnit u1, u2;
  public PrimUnitDiff(PrimUnit u1, PrimUnit u2){ this.u1=u1; this.u2=u2;}  

  public String toString(){ return "="+u1+" "+this.getSim();}
  public String html(){ return HTML.TD("")+HTML.TD(HTML.CPY, HTML.encode(u1.toString()));}
  public Sim getSim(){ return Sim.EQUAL(2);}

  public boolean isFinal(){ return true;}
  public boolean refine()
  { System.out.println(this.toString()); 
    System.out.println(this.getSim().getPercentage());
    return true;
  }
}
