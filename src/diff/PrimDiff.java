package diff;

import value.TypeT;

import sim.Sim;

import dcprototype.Main;
import dcprototype.HTML;
// Diff between PrimUnit, PrimBool, PrimChar, PrimNat, PrimInt
public class PrimDiff extends Diff 
{ private final TypeT v1, v2;
  public PrimDiff(TypeT v1, TypeT v2)
  { if(!v1.typeOf().equals(v2.typeOf()))
      throw new RuntimeException("The type of v1="+v1.typeOf()+
                            " and the type of v2="+v2.typeOf()+" are different.");
    if(!v1.typeOf().isPRIMITIVE())
      throw new RuntimeException("They are not of primitive types.");
    this.v1=v1; this.v2=v2;
  }  

  public String toString()
  { return (this.v1.equals(this.v2) ? "cpy."+v1 : "rep.(-"+v1+", +"+v2+")");}
  public String html()
  { return HTML.TD("")+
    (this.v1.equals(this.v2) ? HTML.TD(HTML.CPY, HTML.encode(v1.toString())): 
                               HTML.TD(HTML.DEL, HTML.encode(v1.toString()))+
                               HTML.TD(HTML.INS, HTML.encode(v2.toString()))
    );
  }
  public Sim getSim(){ return (this.v1.equals(this.v2) ? Sim.EQUAL(2) : Sim.DIFF(2));}

  public boolean isFinal(){ return true;}
  public boolean refine(){ return true;}
}
