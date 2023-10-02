package open.source.typednativequery.expressions;

import open.source.typednativequery.columns.NativeQueryColumn;
import open.source.typednativequery.core.CustomQueryParameter;
import open.source.typednativequery.core.IQuery;
import open.source.typednativequery.metaentity.Table;
import open.source.typednativequery.operator.MinusOperator;
import open.source.typednativequery.operator.PlusOperator;

/**
 * The interface Math function expression.
 *
 * @param <T> the type parameter
 * @param <C> the type parameter
 */
public interface MathFunctionExpression<T extends Table, C extends Number> extends IQuery {

  /**
   * Plus plus operator.
   *
   * @param <X> the type parameter
   * @param column the column
   * @return the plus operator
   */
  default <X extends Table> PlusOperator<T, C> plus(NativeQueryColumn<X, C> column) {
    return new PlusOperator<>(this, column);
  }

  /**
   * Plus plus operator.
   *
   * @param column the column
   * @return the plus operator
   */
  default PlusOperator<T, C> plus(CustomQueryParameter<C> column) {
    return new PlusOperator<>(this, column);
  }

  /**
   * Plus plus operator.
   *
   * @param column the column
   * @return the plus operator
   */
  default PlusOperator<T, C> plus(C column) {
    return new PlusOperator<>(this, new CustomQueryParameter<>(column));
  }

  /**
   * Minus minus operator.
   *
   * @param <X> the type parameter
   * @param column the column
   * @return the minus operator
   */
  default <X extends Table> MinusOperator<T, C> minus(NativeQueryColumn<X, C> column) {
    return new MinusOperator<>(this, column);
  }

  /**
   * Minus minus operator.
   *
   * @param column the column
   * @return the minus operator
   */
  default MinusOperator<T, C> minus(CustomQueryParameter<C> column) {
    return new MinusOperator<>(this, column);
  }

  /**
   * Minus minus operator.
   *
   * @param column the column
   * @return the minus operator
   */
  default MinusOperator<T, C> minus(C column) {
    return new MinusOperator<>(this, new CustomQueryParameter<>(column));
  }
}
