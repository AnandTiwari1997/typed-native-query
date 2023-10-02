package open.source.typednativequery.core;

import java.util.Arrays;
import java.util.Collection;
import open.source.typednativequery.columns.NativeQueryColumn;
import open.source.typednativequery.condition.*;
import open.source.typednativequery.function.CaseOperator;
import open.source.typednativequery.function.arithmatic.AvgFunction;
import open.source.typednativequery.function.arithmatic.CountFunction;
import open.source.typednativequery.function.arithmatic.SumFunction;
import open.source.typednativequery.metaentity.Table;
import open.source.typednativequery.operator.*;

/** The type Native query builder. */
public class NativeQueryBuilder {

  /**
   * Create custom query native query.
   *
   * @param <T> the type parameter
   * @param <R> the type parameter
   * @param tClass the t class
   * @return the native query
   */
  public <T extends Table, R extends NativeQuerySelect<T, ?, ?>>
      NativeQuery<T, R> createCustomQuery(Class tClass) {
    return new NativeQuery<>();
  }

  /**
   * Equal condition.
   *
   * @param <T> the type parameter
   * @param <R> the type parameter
   * @param <C> the type parameter
   * @param leftOperand the left operand
   * @param rightOperand the right operand
   * @return the condition
   */
  public <T extends Table, R extends Table, C> Condition<EqualsOperator> equal(
      NativeQueryColumn<T, C> leftOperand, NativeQueryColumn<R, C> rightOperand) {
    return new EqualsOperator(leftOperand, rightOperand);
  }

  /**
   * Equal condition.
   *
   * @param <T> the type parameter
   * @param <R> the type parameter
   * @param <E> the type parameter
   * @param <C> the type parameter
   * @param leftOperand the left operand
   * @param rightOperand the right operand
   * @return the condition
   */
  public <T extends Table, R extends Table, E extends NativeQuerySelect<R, ?, C>, C>
      Condition<EqualsOperator> equal(
          NativeQueryColumn<T, C> leftOperand, NativeSubQuery<R, E> rightOperand) {
    return new EqualsOperator(leftOperand, rightOperand);
  }

  /**
   * Equal condition.
   *
   * @param <T> the type parameter
   * @param <C> the type parameter
   * @param leftOperand the left operand
   * @param value the value
   * @return the condition
   */
  public <T extends Table, C> Condition<EqualsOperator> equal(
      NativeQueryColumn<T, C> leftOperand, C value) {
    return new EqualsOperator(leftOperand, new CustomQueryParameter<>(value));
  }

  /**
   * Equal condition.
   *
   * @param <T> the type parameter
   * @param <C> the type parameter
   * @param leftOperand the left operand
   * @param value the value
   * @return the condition
   */
  public <T extends Table, C> Condition<EqualsOperator> equal(
      NativeQueryColumn<T, C> leftOperand, CustomQueryParameter<C> value) {
    return new EqualsOperator(leftOperand, value);
  }

  /**
   * Equal condition.
   *
   * @param <T> the type parameter
   * @param <C> the type parameter
   * @param leftOperand the left operand
   * @param value the value
   * @return the condition
   */
  public <T extends Table, C> Condition<EqualsOperator> equal(
      NativeQueryColumn<T, C> leftOperand, NamedQueryParameter value) {
    return new EqualsOperator(leftOperand, value);
  }

  /**
   * Is true condition.
   *
   * @param <T> the type parameter
   * @param <C> the type parameter
   * @param operand the operand
   * @return the condition
   */
  public <T extends Table, C> Condition<UnaryOperator<C>> isTrue(NativeQueryColumn<T, C> operand) {
    return new UnaryOperator<>(operand);
  }

  /**
   * Is false condition.
   *
   * @param <T> the type parameter
   * @param <C> the type parameter
   * @param operand the operand
   * @return the condition
   */
  public <T extends Table, C> Condition<UnaryOperator<C>> isFalse(NativeQueryColumn<T, C> operand) {
    return new UnaryOperator<>(operand).not();
  }

  /**
   * And and operator.
   *
   * @param conditions the conditions
   * @return the and operator
   */
  public AndPredicate and(Predicate... conditions) {
    return new AndPredicate(Arrays.asList(conditions));
  }

  /**
   * Or or operator.
   *
   * @param conditions the conditions
   * @return the or operator
   */
  public OrPredicate or(Predicate... conditions) {
    return new OrPredicate(Arrays.asList(conditions));
  }

  /**
   * Not condition.
   *
   * @param query the query
   * @return the condition
   */
  public Condition<NotOperator> not(IQuery query) {
    return new NotOperator(query);
  }

  /**
   * Exist condition.
   *
   * @param <T> the type parameter
   * @param table the table
   * @return the condition
   */
  public <T extends Table> Condition<ExistOperator> exist(NativeQueryTable table) {
    return new ExistOperator(table);
  }

  /**
   * Exist condition.
   *
   * @param <T> the type parameter
   * @param <O> the type parameter
   * @param table the table
   * @return the condition
   */
  public <T extends Table, O extends NativeQuerySelect<T, ?, ?>> Condition<ExistOperator> exist(
      NativeSubQuery<T, O> table) {
    return new ExistOperator(table);
  }

  /**
   * Sum sum function.
   *
   * @param <T> the type parameter
   * @param <C> the type parameter
   * @param <R> the type parameter
   * @param select the select
   * @return the sum function
   */
  // Aggregate Function
  public <T extends Table, C, R extends Number> SumFunction<T, R> sum(
      NativeQuerySelect<T, C, R> select) {
    return new SumFunction<T, R>(select);
  }

  /**
   * Sql case case operator.
   *
   * @param <T> the type parameter
   * @param <R> the type parameter
   * @param type the type
   * @return the case operator
   */
  public <T extends Table, R> CaseOperator<T, R> sqlCase(Class<R> type) {
    return new CaseOperator<>();
  }

  /**
   * Avg avg function.
   *
   * @param <T> the type parameter
   * @param <C> the type parameter
   * @param <R> the type parameter
   * @param select the select
   * @return the avg function
   */
  public <T extends Table, C, R extends Number> AvgFunction<T, R> avg(
      NativeQuerySelect<T, C, R> select) {
    return new AvgFunction<>(select);
  }

  /**
   * Count count function.
   *
   * @param <T> the type parameter
   * @param <C> the type parameter
   * @param <R> the type parameter
   * @param select the select
   * @return the count function
   */
  public <T extends Table, C, R extends Number> CountFunction<T, R> count(
      NativeQuerySelect<T, C, R> select) {
    return new CountFunction<>(select);
  }

  /**
   * Is not null condition.
   *
   * @param <T> the type parameter
   * @param <C> the type parameter
   * @param <R> the type parameter
   * @param select the select
   * @return the condition
   */
  // Null Function
  public <T extends Table, C, R> Condition<NullOperator<T>> isNotNull(
      NativeQuerySelect<T, C, R> select) {
    return new NullOperator<>(select).not();
  }

  /**
   * Is null condition.
   *
   * @param <T> the type parameter
   * @param <C> the type parameter
   * @param <R> the type parameter
   * @param select the select
   * @return the condition
   */
  public <T extends Table, C, R> Condition<NullOperator<T>> isNull(
      NativeQuerySelect<T, C, R> select) {
    return new NullOperator<>(select);
  }

  /**
   * In condition.
   *
   * @param <T> the type parameter
   * @param <C> the type parameter
   * @param <R> the type parameter
   * @param select the select
   * @return the condition
   */
  public <T extends Table, C, R> Condition<InOperator<T, T, R>> in(
      NativeQuerySelect<T, C, R> select) {
    return new InOperator<>(select);
  }

  /**
   * In condition.
   *
   * @param <T> the type parameter
   * @param <U> the type parameter
   * @param <C> the type parameter
   * @param <R> the type parameter
   * @param select the select
   * @param value the value
   * @return the condition
   */
  public <T extends Table, U extends Table, C, R> Condition<InOperator<T, U, R>> in(
      NativeQuerySelect<T, C, R> select, NativeSubQuery<U, NativeQueryColumn<U, R>> value) {
    return new InOperator<>(select, value);
  }

  /**
   * In condition.
   *
   * @param <T> the type parameter
   * @param <C> the type parameter
   * @param <R> the type parameter
   * @param select the select
   * @param value the value
   * @return the condition
   */
  public <T extends Table, C, R> Condition<InOperator<T, T, R>> in(
      NativeQuerySelect<T, C, R> select, Collection<R> value) {
    return new InOperator<>(select, value);
  }

  /**
   * Not in condition.
   *
   * @param <T> the type parameter
   * @param <C> the type parameter
   * @param <R> the type parameter
   * @param select the select
   * @return the condition
   */
  public <T extends Table, C, R> Condition<InOperator<T, T, R>> notIn(
      NativeQuerySelect<T, C, R> select) {
    return new InOperator<T, T, R>(select).not();
  }

  /**
   * Not in condition.
   *
   * @param <T> the type parameter
   * @param <U> the type parameter
   * @param <C> the type parameter
   * @param <R> the type parameter
   * @param select the select
   * @param value the value
   * @return the condition
   */
  public <T extends Table, U extends Table, C, R> Condition<InOperator<T, U, R>> notIn(
      NativeQuerySelect<T, C, R> select, NativeSubQuery<U, NativeQueryColumn<U, R>> value) {
    return new InOperator<>(select, value).not();
  }

  /**
   * Not in condition.
   *
   * @param <T> the type parameter
   * @param <C> the type parameter
   * @param <R> the type parameter
   * @param select the select
   * @param value the value
   * @return the condition
   */
  public <T extends Table, C, R> Condition<InOperator<T, T, R>> notIn(
      NativeQuerySelect<T, C, R> select, Collection<R> value) {
    return new InOperator<T, T, R>(select, value).not();
  }

  /**
   * Union native selection.
   *
   * @param <T> the type parameter
   * @param <G> the type parameter
   * @param left the left
   * @param right the right
   * @return the native selection
   */
  public <T extends Table, G extends Table> INativeSelection union(
      NativeQuery<T, ?> left, NativeQuery<G, ?> right) {
    return new NativeQueryUnion(left, right);
  }
}
