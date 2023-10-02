package open.source.typednativequery.core;

import java.util.ArrayList;
import java.util.List;
import open.source.typednativequery.metaentity.Table;

/**
 * The type Native query with.
 *
 * @param <T> the type parameter
 */
public class NativeQueryWith<T extends Table> implements IQuery {

  /** The Table. */
  NativeQueryTable<T> table;
  /** The Cte. */
  INativeSelection cte;

  /** The Recursive. */
  boolean recursive = false;

  /**
   * Instantiates a new Native query with.
   *
   * @param table the table
   * @param cte the cte
   */
  public NativeQueryWith(NativeQueryTable<T> table, INativeSelection cte) {
    this.cte = cte;
    this.table = table;
    this.table.getPossibleColumns().clear();
    this.table.bindSelection(this.cte.getSelection());
    this.table.bindEntityDefinedColumns(
        this.table.entity.getColumns(), this.table.getPossibleColumns().isEmpty());
  }

  /**
   * Recursive native query with.
   *
   * @return the native query with
   */
  public NativeQueryWith<T> recursive() {
    this.recursive = true;
    return this;
  }

  @Override
  public String render(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    List<String> tokens = new ArrayList<>();
    tokens.add("WITH");
    if (this.recursive) tokens.add("RECURSIVE");
    tokens.add(this.table.getFrom());
    tokens.add("AS");
    String renderedCTE = this.cte.render(nativeQueryGeneratorContext);
    if (!renderedCTE.trim().startsWith("(") && !renderedCTE.trim().endsWith(")")) {
      tokens.add(wrap(renderedCTE));
    } else tokens.add(renderedCTE);
    return renderQuery(tokens);
  }
}
