package diff;

import value.PrimChar;
import sim.Sim;

import dcprototype.Main;
import dcprototype.HTML;

public class PrimCharDiff extends Diff 
{ private final PrimChar c1, c2;
  public PrimCharDiff(PrimChar c1, PrimChar c2){ this.c1=c1; this.c2=c2;}  

  public String toString(){ return (this.c1.equals(this.c2) ? "="+c1 : "-"+c1+", +"+c2)+this.getSim();}
  //public String html(){ return HTML.TD("")+(this.c1.equals(this.c2) ? HTML.TD(HTML.CPY, HTML.encode(c1.toString)): HTML.TD(HTML.DEL, HTML.encode(c1.toString())+HTML.TD(HTML.INS, HTML.encode(c2.toString()))));}
  public Sim getSim(){ return (this.c1.equals(this.c2) ? Sim.EQUAL(2) : Sim.DIFF(2));}

  public boolean isFinal(){ return true;}
  public boolean refine()
  { System.out.println(this.toString()); 
    System.out.println(this.getSim().getPercentage());
    return true;
  }
}
