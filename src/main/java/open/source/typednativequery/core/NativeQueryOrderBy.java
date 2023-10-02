package open.source.typednativequery.core;

import java.util.List;
import open.source.typednativequery.columns.NativeQueryColumn;

/** The type Native query order by. */
public class NativeQueryOrderBy implements IQuery {
  /** The Column. */
  NativeQueryColumn<?, ?> column;
  /** The Ascending. */
  boolean ascending;

  /**
   * Instantiates a new Native query order by.
   *
   * @param column the column
   */
  public NativeQueryOrderBy(NativeQueryColumn<?, ?> column) {
    this.column = column;
    this.ascending = true;
  }

  /**
   * Instantiates a new Native query order by.
   *
   * @param column the column
   * @param ascending the ascending
   */
  public NativeQueryOrderBy(NativeQueryColumn<?, ?> column, boolean ascending) {
    this.column = column;
    this.ascending = ascending;
  }

  public String render(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    List<String> tokens =
        List.of(this.column.render(nativeQueryGeneratorContext), this.ascending ? "ASC" : "DESC");
    return renderQuery(tokens);
  }
}
