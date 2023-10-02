package open.source.typednativequery.expressions;

import java.util.Collection;
import open.source.typednativequery.columns.NativeQueryColumn;
import open.source.typednativequery.condition.Condition;
import open.source.typednativequery.operator.InOperator;
import open.source.typednativequery.core.*;
import open.source.typednativequery.function.CaseOperator;
import open.source.typednativequery.metaentity.Table;
import open.source.typednativequery.operator.EqualsOperator;
import open.source.typednativequery.operator.NullOperator;

/**
 * The interface Common operator expression.
 *
 * @param <T> the type parameter
 * @param <C> the type parameter
 */
public interface CommonOperatorExpression<T extends Table, C> {
  /**
   * Is not null condition.
   *
   * @return the condition
   */
  Condition<NullOperator<T>> isNotNull();

  /**
   * Is null condition.
   *
   * @return the condition
   */
  Condition<NullOperator<T>> isNull();

  /**
   * In condition.
   *
   * @param <U> the type parameter
   * @param value the value
   * @return the condition
   */
  <U extends Table> Condition<InOperator<T, U, C>> in(
      NativeSubQuery<U, NativeQueryColumn<U, C>> value);

  /**
   * In condition.
   *
   * @param value the value
   * @return the condition
   */
  Condition<InOperator<T, T, C>> in(Collection<C> value);

  /**
   * Not in condition.
   *
   * @param <U> the type parameter
   * @param value the value
   * @return the condition
   */
  <U extends Table> Condition<InOperator<T, U, C>> notIn(
      NativeSubQuery<U, NativeQueryColumn<U, C>> value);

  /**
   * Not in condition.
   *
   * @param value the value
   * @return the condition
   */
  Condition<InOperator<T, T, C>> notIn(Collection<C> value);

  /**
   * Equal condition.
   *
   * @param <U> the type parameter
   * @param rightOperand the right operand
   * @return the condition
   */
  <U extends Table> Condition<EqualsOperator> equal(NativeQueryColumn<U, C> rightOperand);

  /**
   * Equal condition.
   *
   * @param <R> the type parameter
   * @param <E> the type parameter
   * @param rightOperand the right operand
   * @return the condition
   */
  <R extends Table, E extends NativeQuerySelect<R, ?, C>> Condition<EqualsOperator> equal(
      NativeSubQuery<R, E> rightOperand);

  /**
   * Equal condition.
   *
   * @param value the value
   * @return the condition
   */
  Condition<EqualsOperator> equal(C value);

  /**
   * Equal condition.
   *
   * @param value the value
   * @return the condition
   */
  Condition<EqualsOperator> equal(CustomQueryParameter<C> value);

  /**
   * Equal condition.
   *
   * @param value the value
   * @return the condition
   */
  Condition<EqualsOperator> equal(NamedQueryParameter value);

  /**
   * Asc native query order by.
   *
   * @return the native query order by
   */
  NativeQueryOrderBy asc();

  /**
   * Desc native query order by.
   *
   * @return the native query order by
   */
  NativeQueryOrderBy desc();

  /**
   * Sql case case operator.
   *
   * @param <R> the type parameter
   * @param conditions the conditions
   * @param value the value
   * @param thenValue the then value
   * @return the case operator
   */
  <R> CaseOperator<T, R> sqlCase(Condition.Conditions conditions, C value, R thenValue);
}
