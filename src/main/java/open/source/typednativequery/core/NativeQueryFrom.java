package open.source.typednativequery.core;

import java.util.Set;
import open.source.typednativequery.columns.NativeQueryColumn;
import open.source.typednativequery.metaentity.Table;

/**
 * The interface Native query from.
 *
 * @param <U> the type parameter
 * @param <T> the type parameter
 */
public interface NativeQueryFrom<U, T extends Table> extends IQuery, QueryAlias<U> {

  /**
   * Bind selection.
   *
   * @param selection the selection
   */
  void bindSelection(Set<NativeQuerySelect<?, ?, ?>> selection);

  /**
   * Join native query table join.
   *
   * @param <C> the type parameter
   * @param tableName the table name
   * @return the native query table join
   */
  <C extends Table> NativeQueryTableJoin<C> join(Class<C> tableName);

  /**
   * Join native query table join.
   *
   * @param <C> the type parameter
   * @param tableName the table name
   * @param joinType the join type
   * @return the native query table join
   */
  <C extends Table> NativeQueryTableJoin<C> join(
      Class<C> tableName, NativeQueryTableJoinType joinType);

  /**
   * Join native query table join.
   *
   * @param <C> the type parameter
   * @param table the table
   * @return the native query table join
   */
  <C extends Table> NativeQueryTableJoin<C> join(NativeQueryTable<C> table);

  /**
   * Join native query table join.
   *
   * @param <C> the type parameter
   * @param table the table
   * @param joinType the join type
   * @return the native query table join
   */
  <C extends Table> NativeQueryTableJoin<C> join(
      NativeQueryTable<C> table, NativeQueryTableJoinType joinType);

  /**
   * Join native query table join.
   *
   * @param <C> the type parameter
   * @param table the table
   * @return the native query table join
   */
  <C extends NativeQuerySelect<T, ?, ?>> NativeQueryTableJoin<T> join(NativeSubQuery<T, C> table);

  /**
   * Join native query table join.
   *
   * @param <G> the type parameter
   * @param <C> the type parameter
   * @param table the table
   * @param joinType the join type
   * @return the native query table join
   */
  <G extends Table, C extends NativeQuerySelect<G, ?, ?>> NativeQueryTableJoin<G> join(
      NativeSubQuery<G, C> table, NativeQueryTableJoinType joinType);

  /**
   * Gets from.
   *
   * @return the from
   */
  String getFrom();

  /**
   * Exist boolean.
   *
   * @param column the column
   * @return the boolean
   */
  boolean exist(NativeQueryColumn<?, ?> column);

  /**
   * Gets join.
   *
   * @return the join
   */
  NativeQueryTableJoin<?> getJoin();
}
