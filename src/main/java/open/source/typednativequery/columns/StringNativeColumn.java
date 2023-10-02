package open.source.typednativequery.columns;

import open.source.typednativequery.expressions.StringFunctionExpression;
import open.source.typednativequery.metaentity.Table;

/**
 * The type String native column.
 *
 * @param <X> the type parameter
 */
public class StringNativeColumn<X extends Table> extends NativeQueryColumn<X, String>
    implements StringFunctionExpression<X> {

  /**
   * Instantiates a new String native column.
   *
   * @param name the name
   * @param javaType the java type
   */
  public StringNativeColumn(String name, Class<String> javaType) {
    super(name, javaType);
  }
}
