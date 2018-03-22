package string;
public class StringDiff
{ private final String a, b;
  private PartialSolution[] candidates;
  public StringDiff(String a, String b)
  { this.a=(a==null ? "" : a);
    this.b=(b==null ? "" : b);
    this.candidates = new PartialSolution[] { new PartialSolution(null)};
  }
}
