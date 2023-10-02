package open.source.typednativequery.function;

import open.source.typednativequery.core.NativeQuerySelect;
import open.source.typednativequery.metaentity.Table;

/**
 * The interface Function.
 *
 * @param <T> the type parameter
 * @param <I> the type parameter
 * @param <O> the type parameter
 */
public interface Function<T extends Table, I, O> extends NativeQuerySelect<T, I, O> {}
