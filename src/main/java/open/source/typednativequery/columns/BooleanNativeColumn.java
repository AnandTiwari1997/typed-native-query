package open.source.typednativequery.columns;

import open.source.typednativequery.condition.Condition;
import open.source.typednativequery.expressions.BooleanOps;
import open.source.typednativequery.metaentity.Table;
import open.source.typednativequery.operator.UnaryOperator;

/**
 * The type Boolean native column.
 *
 * @param <T> the type parameter
 */
public class BooleanNativeColumn<T extends Table> extends NativeQueryColumn<T, Boolean>
    implements BooleanOps {

  /**
   * Instantiates a new Boolean native column.
   *
   * @param name the name
   * @param javaType the java type
   */
  public BooleanNativeColumn(String name, Class<Boolean> javaType) {
    super(name, javaType);
  }

  @Override
  public Condition<UnaryOperator<Boolean>> isTrue() {
    return new UnaryOperator<>(this);
  }

  @Override
  public Condition<UnaryOperator<Boolean>> isFalse() {
    return new UnaryOperator<>(this).not();
  }
}
