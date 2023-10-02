package open.source.typednativequery.columns;

import open.source.typednativequery.expressions.AggregateFunctionExpression;
import open.source.typednativequery.expressions.MathFunctionExpression;
import open.source.typednativequery.function.arithmatic.*;
import open.source.typednativequery.metaentity.Table;

/**
 * The type Number native column.
 *
 * @param <T> the type parameter
 * @param <C> the type parameter
 */
public class NumberNativeColumn<T extends Table, C extends Number> extends NativeQueryColumn<T, C>
    implements AggregateFunctionExpression<T, C>, MathFunctionExpression<T, C> {

  /**
   * Instantiates a new Number native column.
   *
   * @param name the name
   * @param javaType the java type
   */
  public NumberNativeColumn(String name, Class<C> javaType) {
    super(name, javaType);
  }

  @Override
  public SumFunction<T, C> sum() {
    return new SumFunction<>(this);
  }

  @Override
  public AvgFunction<T, C> avg() {
    return new AvgFunction<>(this);
  }

  @Override
  public CountFunction<T, C> count() {
    return new CountFunction<T, C>(this);
  }

  @Override
  public AbsFunction<T, C> abs() {
    return new AbsFunction<>(this);
  }

  @Override
  public ModFunction<T, C> mod() {
    return new ModFunction<>(this);
  }

  @Override
  public SqrtFunction<T, C> sqrt() {
    return new SqrtFunction<>(this);
  }

  @Override
  public QuotFunction<T, C> quot() {
    return new QuotFunction<>(this);
  }

  @Override
  public DiffFunction<T, C> diff() {
    return new DiffFunction<>(this);
  }

  @Override
  public ProdFunction<T, C> prod() {
    return new ProdFunction<>(this);
  }
}
