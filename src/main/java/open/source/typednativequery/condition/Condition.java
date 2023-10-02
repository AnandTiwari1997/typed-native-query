package open.source.typednativequery.condition;

import open.source.typednativequery.columns.NativeQueryColumn;
import open.source.typednativequery.core.CustomQueryParameter;
import open.source.typednativequery.metaentity.Table;
import open.source.typednativequery.operator.EqualsOperator;

/**
 * The interface Condition.
 *
 * @param <T> the type parameter
 */
public interface Condition<T> extends Predicate {
  /**
   * Not t.
   *
   * @return the t
   */
  T not();

  /**
   * Gets condition.
   *
   * @return the condition
   */
  default T getCondition() {
    return (T) this;
  }

  /** The enum Conditions. */
  enum Conditions {
    /** The Equals. */
    EQUALS {
      public <U extends Table, C> Condition<EqualsOperator> getOperator(
          NativeQueryColumn<U, C> column, C value) {
        return column.equal(new CustomQueryParameter<>(value));
      }
    };

    /**
     * Gets operator.
     *
     * @param <U> the type parameter
     * @param <C> the type parameter
     * @param column the column
     * @param value the value
     * @return the operator
     */
    public abstract <U extends Table, C> Condition<?> getOperator(
        NativeQueryColumn<U, C> column, C value);
  }
}
