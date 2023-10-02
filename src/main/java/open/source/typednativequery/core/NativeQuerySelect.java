package open.source.typednativequery.core;

import open.source.typednativequery.metaentity.Table;

/**
 * The interface Native query select.
 *
 * @param <T> the type parameter
 * @param <C> the type parameter
 * @param <R> the type parameter
 */
public interface NativeQuerySelect<T extends Table, C, R> extends QueryAlias<C>, IQuery {

  /**
   * Gets result type.
   *
   * @return the result type
   */
  default Class<R> getResultType() {
    return null;
  }

  /**
   * Gets name.
   *
   * @return the name
   */
  default String getName() {
    return null;
  }

  /**
   * Get c.
   *
   * @return the c
   */
  default C get() {
    return null;
  }

  /**
   * Is primitive boolean.
   *
   * @return the boolean
   */
  default boolean isPrimitive() {
    return true;
  }

  /**
   * Is list collection boolean.
   *
   * @return the boolean
   */
  default boolean isListCollection() {
    return false;
  }
}
