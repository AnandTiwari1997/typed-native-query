package open.source.typednativequery.core;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import open.source.typednativequery.metaentity.Table;

/** The type Native query union. */
public class NativeQueryUnion implements INativeSelection {

  /** The Left. */
  NativeQuery<?, ?> left;
  /** The Right. */
  NativeQuery<?, ?> right;
  /** The Selections. */
  Set<NativeQuerySelect<?, ?, ?>> selections = null;

  /**
   * Instantiates a new Native query union.
   *
   * @param left the left
   * @param right the right
   */
  public <T extends Table, G extends Table> NativeQueryUnion(
      NativeQuery<T, ?> left, NativeQuery<G, ?> right) {

    List<NativeQuerySelect<?, ?, ?>> leftSelection = new LinkedList<>(left.getSelection());
    List<NativeQuerySelect<?, ?, ?>> rightSelection = new LinkedList<>(right.getSelection());

    // Verify same number of columns
    if (leftSelection.size() != rightSelection.size()) {
      throw new RuntimeException("UNION Query must have same number of selection");
    }

    // Verify same order of column and type
    for (int i = 0; i < leftSelection.size(); i++) {
      NativeQuerySelect<?, ?, ?> leftColumn = leftSelection.get(i);
      NativeQuerySelect<?, ?, ?> rightColumn = rightSelection.get(i);
      if (!leftColumn.getName().equals(rightColumn.getName())) {
        throw new RuntimeException("UNION Query selection must be in same order");
      }
      if (!leftColumn.getResultType().equals(rightColumn.getResultType())) {
        throw new RuntimeException("UNION Query selection must have same data type");
      }
    }

    this.selections = new LinkedHashSet<>();
    this.selections.addAll(right.getSelection());

    this.left = left;
    this.right = right;
    this.left.getOrderBy().clear();
  }

  @Override
  public String render(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    List<String> tokens =
        List.of(
            this.left.render(nativeQueryGeneratorContext),
            "UNION",
            this.right.render(nativeQueryGeneratorContext));
    return renderQuery(tokens);
  }

  @Override
  public Set<NativeQuerySelect<?, ?, ?>> getSelection() {
    return this.selections;
  }
}
