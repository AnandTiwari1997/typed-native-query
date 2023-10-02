package open.source.typednativequery.core;

import java.util.*;
import lombok.AccessLevel;
import lombok.Getter;
import open.source.typednativequery.columns.NativeQueryColumn;
import open.source.typednativequery.condition.Condition;
import open.source.typednativequery.condition.Predicate;
import open.source.typednativequery.function.arithmatic.AggregateFunction;
import open.source.typednativequery.metaentity.Table;
import open.source.typednativequery.operator.ExistOperator;

/**
 * The type Native query.
 *
 * @param <U> the type parameter
 * @param <R> the type parameter
 */
@Getter(value = AccessLevel.PROTECTED)
public class NativeQuery<U extends Table, R extends NativeQuerySelect<U, ?, ?>>
    implements IQuery, INativeSelection {

  private boolean distinct = false;
  private boolean useWith = false;
  private Integer offset;
  private Integer limit;
  private final Set<NativeQueryTable<?>> tables;
  private final Set<Predicate> expressions;

  @Getter(value = AccessLevel.PUBLIC)
  private final Set<NativeQuerySelect<?, ?, ?>> selection;

  private final Set<NativeQueryOrderBy> orderBy;
  private final Set<NativeQueryGroupBy> groupBy;
  private final Set<NativeQueryWith<?>> with;
  private final NativeQueryGeneratorContext nativeQueryGeneratorContext;

  /** Instantiates a new Native query. */
  NativeQuery() {
    tables = new LinkedHashSet<>();
    expressions = new LinkedHashSet<>();
    selection = new LinkedHashSet<>();
    orderBy = new LinkedHashSet<>();
    groupBy = new LinkedHashSet<>();
    with = new LinkedHashSet<>();
    nativeQueryGeneratorContext = new NativeQueryGeneratorContext(this);
  }

  /**
   * Instantiates a new Native query.
   *
   * @param distinct the distinct
   * @param offset the offset
   * @param limit the limit
   * @param tables the tables
   * @param expressions the expressions
   * @param selection the selection
   * @param orderBy the order by
   * @param groupBy the group by
   * @param with the with
   * @param nativeQueryGeneratorContext the native query generator context
   */
  public NativeQuery(
      boolean distinct,
      Integer offset,
      Integer limit,
      Set<NativeQueryTable<?>> tables,
      Set<Predicate> expressions,
      Set<NativeQuerySelect<?, ?, ?>> selection,
      Set<NativeQueryOrderBy> orderBy,
      Set<NativeQueryGroupBy> groupBy,
      Set<NativeQueryWith<?>> with,
      NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    this.distinct = distinct;
    this.offset = offset;
    this.limit = limit;
    this.tables = tables;
    this.expressions = expressions;
    this.selection = selection;
    this.orderBy = orderBy;
    this.groupBy = groupBy;
    this.with = with;
    if (!this.with.isEmpty()) this.useWith = true;
    this.nativeQueryGeneratorContext = nativeQueryGeneratorContext;
  }

  /**
   * Create native query native query.
   *
   * @param <T> the type parameter
   * @param <C> the type parameter
   * @param tClass the t class
   * @return the native query
   */
  public static <T extends Table, C extends NativeQuerySelect<T, ?, ?>>
      NativeQuery<T, C> createNativeQuery(Class<T> tClass) {
    return new NativeQuery<>();
  }

  /**
   * Gets query.
   *
   * @return the query
   */
  public String getQuery() {
    return nativeQueryGeneratorContext.compile();
  }

  @Override
  public String render(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    nativeQueryGeneratorContext
        .getParameters()
        .values()
        .forEach(this.nativeQueryGeneratorContext::parameter);
    String query = this.nativeQueryGeneratorContext.compile();
    this.nativeQueryGeneratorContext
        .getParameters()
        .values()
        .forEach(nativeQueryGeneratorContext::parameter);
    return query;
  }

  private boolean isValidColumn(NativeQueryColumn<?, ?> columnName) {
    for (NativeQueryFrom<?, ?> from : this.tables) {
      if (from.exist(columnName)) return true;
      NativeQueryTableJoin<?> join = from.getJoin();
      while (join != null) {
        if (join.exist(columnName)) return true;
        join = join.getJoin();
      }
    }
    return false;
  }

  private <C, E> void checkGroupCondition(NativeQuerySelect<?, C, E> column) {
    if (column instanceof AggregateFunction) {
      for (NativeQuerySelect<?, ?, ?> select : this.selection) {
        if (select instanceof NativeQueryColumn) {
          groupBy(select);
        }
      }
    }
  }

  /**
   * Select multiple native query.
   *
   * @param <C> the type parameter
   * @param <E> the type parameter
   * @param column the column
   * @return the native query
   */
  public <C, E> NativeQuery<U, R> selectMultiple(NativeQuerySelect<? extends U, C, E> column) {
    if (column instanceof NativeQueryColumn) {
      if (!isValidColumn((NativeQueryColumn<?, ?>) column)) {
        throw new RuntimeException(
            "Cannot find property `"
                + ((NativeQueryColumn<?, ?>) column).getColumnName()
                + "` in Table.");
      }
    }
    checkGroupCondition(column);
    selection.add(column);
    return this;
  }

  /**
   * Select multiple native query.
   *
   * @param table the table
   * @return the native query
   */
  public NativeQuery<U, R> selectMultiple(NativeQueryTable<? extends U> table) {
    selection.addAll(table.getPossibleColumns().values());
    return this;
  }

  /**
   * Select native query.
   *
   * @param table the table
   * @return the native query
   */
  public NativeQuery<U, R> select(NativeQueryTable<U> table) {
    selection.clear();
    selection.addAll(table.getPossibleColumns().values());
    return this;
  }

  /**
   * Select native query.
   *
   * @param <C> the type parameter
   * @param <E> the type parameter
   * @param column the column
   * @return the native query
   */
  public <C, E> NativeQuery<U, R> select(NativeQuerySelect<U, C, E> column) {
    selection.add(column);
    return this;
  }

  /**
   * From native query table.
   *
   * @param <T> the type parameter
   * @param from the from
   * @return the native query table
   */
  public <T extends Table> NativeQueryTable<T> from(Class<T> from) {
    NativeQueryTable<T> table = new NativeQueryTable<>(from);
    this.tables.add(table);
    return table;
  }

  /**
   * From native query table.
   *
   * @param <T> the type parameter
   * @param from the from
   * @return the native query table
   */
  public <T extends Table> NativeQueryTable<T> from(NativeQueryTable<T> from) {
    this.tables.add(from);
    return from;
  }

  /**
   * From native query table.
   *
   * @param <X> the type parameter
   * @param from the from
   * @return the native query table
   */
  public <X extends Table> NativeQueryTable<X> from(NativeSubQuery<X, ?> from) {
    NativeQueryTable<X> table = new NativeQueryTable<>(from, from.alias);
    this.tables.add(table);
    return table;
  }

  /**
   * Where native query.
   *
   * @param condition the condition
   * @return the native query
   */
  public NativeQuery<U, R> where(Predicate... condition) {
    expressions.addAll(Arrays.asList(condition));
    return this;
  }

  /**
   * Order by native query.
   *
   * @param <T> the type parameter
   * @param <C> the type parameter
   * @param column the column
   * @param ascending the ascending
   * @return the native query
   */
  public <T extends Table, C> NativeQuery<U, R> orderBy(
      NativeQueryColumn<T, C> column, boolean ascending) {
    NativeQueryOrderBy order = new NativeQueryOrderBy(column, ascending);
    orderBy.add(order);
    return this;
  }

  /**
   * Group by native query.
   *
   * @param column the column
   * @return the native query
   */
  public final NativeQuery<U, R> groupBy(IQuery... column) {
    NativeQueryGroupBy nativeQueryGroupBy = new NativeQueryGroupBy(column);
    groupBy.add(nativeQueryGroupBy);
    return this;
  }

  /**
   * Group by roll up native query.
   *
   * @param <T> the type parameter
   * @param <C> the type parameter
   * @param column the column
   * @return the native query
   */
  @SafeVarargs
  public final <T extends Table, C> NativeQuery<U, R> groupByRollUp(
      NativeQueryColumn<T, C>... column) {
    NativeQueryGroupBy nativeQueryGroupBy = new NativeQueryGroupBy(column);
    nativeQueryGroupBy.rollUp();
    groupBy.add(nativeQueryGroupBy);
    return this;
  }

  /**
   * Asc native query.
   *
   * @param <T> the type parameter
   * @param <C> the type parameter
   * @param column the column
   * @return the native query
   */
  public <T extends Table, C> NativeQuery<U, R> asc(NativeQueryColumn<T, C> column) {
    orderBy(column, true);
    return this;
  }

  /**
   * Desc native query.
   *
   * @param <T> the type parameter
   * @param <C> the type parameter
   * @param column the column
   * @return the native query
   */
  public <T extends Table, C> NativeQuery<U, R> desc(NativeQueryColumn<T, C> column) {
    orderBy(column, false);
    return this;
  }

  /**
   * Distinct native query.
   *
   * @return the native query
   */
  public NativeQuery<U, R> distinct() {
    this.distinct = true;
    return this;
  }

  /**
   * Sub query native sub query.
   *
   * @param <T> the type parameter
   * @param <C> the type parameter
   * @param type the type
   * @return the native sub query
   */
  public <T extends Table, C extends NativeQuerySelect<T, ?, ?>> NativeSubQuery<T, C> subQuery(
      Class<T> type) {
    return new NativeSubQuery<>(type);
  }

  /**
   * Sub query native sub query.
   *
   * @param <B> the type parameter
   * @param <C> the type parameter
   * @param customQuery the custom query
   * @return the native sub query
   */
  public <B extends Table, C extends NativeQuerySelect<B, ?, ?>> NativeSubQuery<B, C> subQuery(
      NativeQuery<B, C> customQuery) {
    return new NativeSubQuery<>(customQuery);
  }

  /**
   * With native query.
   *
   * @param <T> the type parameter
   * @param table the table
   * @param customSubQuery the custom sub query
   * @return the native query
   */
  public <T extends Table> NativeQuery<U, R> with(
      NativeQueryTable<T> table, NativeSubQuery<T, ?> customSubQuery) {
    this.useWith = true;
    with.add(new NativeQueryWith<>(table, customSubQuery));
    return this;
  }

  /**
   * With recursive native query.
   *
   * @param <T> the type parameter
   * @param table the table
   * @param customSubQuery the custom sub query
   * @return the native query
   */
  public <T extends Table> NativeQuery<U, R> withRecursive(
      NativeQueryTable<T> table, INativeSelection customSubQuery) {
    this.useWith = true;
    with.add(new NativeQueryWith<>(table, customSubQuery).recursive());
    return this;
  }

  /**
   * Offset native query.
   *
   * @param offset the offset
   * @return the native query
   */
  public NativeQuery<U, R> offset(int offset) {
    this.offset = offset;
    return this;
  }

  /**
   * Limit native query.
   *
   * @param limit the limit
   * @return the native query
   */
  public NativeQuery<U, R> limit(int limit) {
    this.limit = limit;
    return this;
  }

  /**
   * Gets parameters.
   *
   * @return the parameters
   */
  public Map<String, QueryParameter> getParameters() {
    return this.nativeQueryGeneratorContext.getParameters();
  }

  /**
   * Exist condition.
   *
   * @param <T> the type parameter
   * @return the condition
   */
  public <T extends Table> Condition<ExistOperator> exist() {
    return new ExistOperator(this);
  }
}
