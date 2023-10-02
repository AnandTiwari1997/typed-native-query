package open.source.typednativequery.expressions;

import open.source.typednativequery.function.arithmatic.*;
import open.source.typednativequery.metaentity.Table;

/**
 * The interface Aggregate function expression.
 *
 * @param <T> the type parameter
 * @param <C> the type parameter
 */
public interface AggregateFunctionExpression<T extends Table, C extends Number> {

  /**
   * Sum sum function.
   *
   * @return the sum function
   */
  SumFunction<T, C> sum();

  /**
   * Avg avg function.
   *
   * @return the avg function
   */
  AvgFunction<T, C> avg();

  /**
   * Count count function.
   *
   * @return the count function
   */
  CountFunction<T, C> count();

  /**
   * Abs abs function.
   *
   * @return the abs function
   */
  AbsFunction<T, C> abs();

  /**
   * Mod mod function.
   *
   * @return the mod function
   */
  ModFunction<T, C> mod();

  /**
   * Sqrt sqrt function.
   *
   * @return the sqrt function
   */
  SqrtFunction<T, C> sqrt();

  /**
   * Quot quot function.
   *
   * @return the quot function
   */
  QuotFunction<T, C> quot();

  /**
   * Diff diff function.
   *
   * @return the diff function
   */
  DiffFunction<T, C> diff();

  /**
   * Prod prod function.
   *
   * @return the prod function
   */
  ProdFunction<T, C> prod();
}
