package open.source.typednativequery.metaentity;

import java.util.List;
import open.source.typednativequery.columns.NativeQueryColumn;

/** The interface Meta entity. */
public interface Table {
  /**
   * Gets table name.
   *
   * @return the table name
   */
  String getTableName();

  /**
   * Gets columns.
   *
   * @return the columns
   */
  List<NativeQueryColumn<?, ?>> getColumns();
}
