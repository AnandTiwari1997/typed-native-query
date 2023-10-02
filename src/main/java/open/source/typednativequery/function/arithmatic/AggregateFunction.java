package open.source.typednativequery.function.arithmatic;

import open.source.typednativequery.function.Function;
import open.source.typednativequery.metaentity.Table;

/**
 * The interface Aggregate function.
 *
 * @param <T> the type parameter
 * @param <I> the type parameter
 * @param <O> the type parameter
 */
public interface AggregateFunction<T extends Table, I, O extends Number> extends Function<T, I, O> {

  @SuppressWarnings(value = {"unchecked"})
  @Override
  default Class<O> getResultType() {
    return (Class<O>) Number.class;
  }
}
