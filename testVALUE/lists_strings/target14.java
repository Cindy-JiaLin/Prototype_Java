package string;
import java.util.List;
public class ListDiff
{ private final List<String> a, b;
  private PartialSolution[] candidates;
  public ListDiff(List<String> a, List<String> b)
  { this.a=(a==null ? new ArrayList() : a);
    this.b=(b==null ? new ArrayList() : b);
    this.candidates = new PartialSolution[] { new PartialSolution(null)};
  }
}
