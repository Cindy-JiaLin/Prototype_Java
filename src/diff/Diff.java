package diff; 

import sim.Sim;
import value.TypeT;

public abstract class Diff 
{ public abstract boolean isFinal();
  public abstract boolean refine();
  public abstract Sim getSim();
}
