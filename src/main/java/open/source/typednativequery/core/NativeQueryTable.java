package open.source.typednativequery.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.*;
import lombok.SneakyThrows;
import open.source.typednativequery.columns.*;
import open.source.typednativequery.metaentity.Table;
import org.apache.commons.lang3.StringUtils;

/**
 * The type Native query table.
 *
 * @param <T> the type parameter
 */
public class NativeQueryTable<T extends Table> implements NativeQueryFrom<NativeQueryTable<T>, T> {

  private String name;
  private String alias;
  private Class<T> type;
  private NativeSubQuery<T, ?> subQuery;
  private final Map<String, NativeQueryColumn<T, ?>> bindColumns = new HashMap<>();
  /** The Entity. */
  public T entity;
  /** The Is sub query. */
  boolean isSubQuery = false;

  private NativeQueryTableJoin<?> innerJoinList = null;

  /** Instantiates a new Native query table. */
  protected NativeQueryTable() {}

  /**
   * Instantiates a new Native query table.
   *
   * @param className the class name
   */
  public NativeQueryTable(T className) {
    this.entity = className;
    bindEntityDefinedColumns(this.entity.getColumns(), this.bindColumns.isEmpty());
    this.name = this.entity.getTableName();
  }

  /**
   * Instantiates a new Native query table.
   *
   * @param className the class name
   */
  public NativeQueryTable(Class<T> className) {
    initializeEntity(className);
    bindEntityDefinedColumns(this.entity.getColumns(), this.bindColumns.isEmpty());
    this.name = this.entity.getTableName();
  }

  /**
   * Instantiates a new Native query table.
   *
   * @param className the class name
   * @param alias the alias
   */
  public NativeQueryTable(Class<T> className, String alias) {
    initializeEntity(className);
    bindEntityDefinedColumns(this.entity.getColumns(), this.bindColumns.isEmpty());
    this.name = this.entity.getTableName();
    this.alias = alias;
  }

  /**
   * Instantiates a new Native query table.
   *
   * @param table the table
   */
  public NativeQueryTable(NativeQueryTable<T> table) {
    this.name = table.name;
    this.alias = table.alias;
    this.type = table.type;
    this.subQuery = table.subQuery;
    this.entity = table.entity;
    this.innerJoinList = table.innerJoinList;
    this.isSubQuery = table.isSubQuery;
    this.bindEntityDefinedColumns(new ArrayList<>(table.bindColumns.values()), true);
  }

  /**
   * Instantiates a new Native query table.
   *
   * @param subQuery the sub query
   * @param alias the alias
   */
  @SneakyThrows
  public <C extends NativeQuerySelect<T, ?, ?>> NativeQueryTable(
      NativeSubQuery<T, C> subQuery, String alias) {
    this.subQuery = subQuery;
    this.alias = alias;
    initializeEntity(this.getSubQuery().getEntity());
    bindSelection(this.getSubQuery().getSelection());
    bindEntityDefinedColumns(this.entity.getColumns(), this.bindColumns.isEmpty());
    this.isSubQuery = true;
  }

  @SneakyThrows
  @SuppressWarnings("unchecked")
  private void initializeEntity(Class<T> className) {
    if (className.equals(Table.class)) {
      this.entity =
          (T)
              new Table() {

                @Override
                public String getTableName() {
                  return getAs();
                }

                @Override
                public List<NativeQueryColumn<?, ?>> getColumns() {
                  return new ArrayList<>(bindColumns.values());
                }
              };
    } else {
      Constructor<T> constructor = className.getDeclaredConstructor();
      if (Modifier.isPrivate(constructor.getModifiers())) constructor.setAccessible(true);
      this.entity = constructor.newInstance();
    }
  }

  @Override
  public void bindSelection(Set<NativeQuerySelect<?, ?, ?>> selection) {
    for (NativeQuerySelect<?, ?, ?> select : selection) {
      if (select instanceof NativeQueryColumn) {
        NativeQueryColumn<?, ?> column =
            NativeQueryColumn.build(
                ((NativeQueryColumn<?, ?>) select).getColumnName(),
                select.getResultType(),
                select.isPrimitive(),
                select.isListCollection());
        this.bindSelect(column.as(select.getAs()));
      } else {
        this.bindSelectRaw(select);
      }
    }
  }

  /**
   * Bind entity defined columns.
   *
   * @param columns the columns
   * @param isInitialEmpty the is initial empty
   */
  public void bindEntityDefinedColumns(
      List<NativeQueryColumn<?, ?>> columns, boolean isInitialEmpty) {
    for (NativeQueryColumn<?, ?> column : columns) {
      if (exist(column)) {
        NativeQueryColumn<T, ?> nativeQueryColumn = this.bindColumns.get(column.getColumnName());
        column.updateName(nativeQueryColumn.getName());
        this.bindSelect(column);
      } else if (isInitialEmpty) {
        this.bindSelect(column);
        column.updateName(column.getName());
      }
    }
  }

  @SuppressWarnings({"unchecked case"})
  private <C> void bindSelect(NativeQueryColumn<?, ?> select) {
    NativeQueryColumn<T, C> column = (NativeQueryColumn<T, C>) select;
    if (Objects.isNull(column)) return;
    column.bindTable(this);
    this.bindColumns.put(column.getColumnName(), column);
    this.bindColumns.put(column.getName(), column);
  }

  @SuppressWarnings({"unchecked case"})
  private <C> void bindSelectRaw(NativeQuerySelect<?, ?, ?> select) {
    NativeQueryColumn<T, C> column =
        (NativeQueryColumn<T, C>)
            NativeQueryColumn.build(
                select.getName(),
                select.getResultType(),
                select.isPrimitive(),
                select.isListCollection());
    column.bindTable(this);
    this.bindColumns.put(column.getColumnName(), column);
  }

  @Override
  public void prepareAlias(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    if (StringUtils.isBlank(this.getAs())) {
      if (this.isSubQuery) {
        this.getSubQuery().prepareAlias(nativeQueryGeneratorContext);
        this.as(this.getSubQuery().getAs());
      } else this.as(nativeQueryGeneratorContext.generateAlias(this.name.toLowerCase()));
    }
    prepareAliasesForJoins(getJoin(), nativeQueryGeneratorContext);
  }

  private void prepareAliasesForJoins(
      NativeQueryTableJoin<?> tableJoin, NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    if (tableJoin == null) return;
    tableJoin.prepareAlias(nativeQueryGeneratorContext);
    prepareAliasesForJoins(tableJoin.getJoin(), nativeQueryGeneratorContext);
  }

  @Override
  public NativeQueryTable<T> as(String alias) {
    this.alias = alias;
    return this;
  }

  @Override
  public <C extends Table> NativeQueryTableJoin<C> join(NativeQueryTable<C> table) {
    NativeQueryTableJoin<C> tableJoin =
        new NativeQueryTableJoin<>(table, NativeQueryTableJoinType.JOIN);
    this.innerJoinList = tableJoin;
    return tableJoin;
  }

  @Override
  public <C extends Table> NativeQueryTableJoin<C> join(
      NativeQueryTable<C> table, NativeQueryTableJoinType joinType) {
    NativeQueryTableJoin<C> tableJoin = new NativeQueryTableJoin<>(table, joinType);
    this.innerJoinList = tableJoin;
    return tableJoin;
  }

  @Override
  public <C extends NativeQuerySelect<T, ?, ?>> NativeQueryTableJoin<T> join(
      NativeSubQuery<T, C> table) {
    NativeQueryTableJoin<T> tableJoin =
        new NativeQueryTableJoin<>(table, NativeQueryTableJoinType.JOIN);
    this.innerJoinList = tableJoin;
    return tableJoin;
  }

  @Override
  public <G extends Table, C extends NativeQuerySelect<G, ?, ?>> NativeQueryTableJoin<G> join(
      NativeSubQuery<G, C> table, NativeQueryTableJoinType joinType) {
    NativeQueryTableJoin<G> tableJoin = new NativeQueryTableJoin<>(table, joinType);
    this.innerJoinList = tableJoin;
    return tableJoin;
  }

  @Override
  public <C extends Table> NativeQueryTableJoin<C> join(Class<C> table) {
    NativeQueryTableJoin<C> tableJoin =
        new NativeQueryTableJoin<>(table, NativeQueryTableJoinType.JOIN);
    this.innerJoinList = tableJoin;
    return tableJoin;
  }

  @Override
  public <C extends Table> NativeQueryTableJoin<C> join(
      Class<C> table, NativeQueryTableJoinType joinType) {
    NativeQueryTableJoin<C> tableJoin = new NativeQueryTableJoin<>(table, joinType);
    this.innerJoinList = tableJoin;
    return tableJoin;
  }

  @Override
  public String render(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    List<String> tokens = new ArrayList<>();
    tokens.add(renderWithoutJoin(nativeQueryGeneratorContext));
    if (shouldRender(this.innerJoinList))
      tokens.add(this.innerJoinList.render(nativeQueryGeneratorContext));
    return renderQuery(tokens);
  }

  /**
   * Render without join string.
   *
   * @param nativeQueryGeneratorContext the native query generator context
   * @return the string
   */
  public String renderWithoutJoin(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    List<String> tokens = new ArrayList<>();
    if (this.isSubQuery) tokens.add(this.getSubQuery().render(nativeQueryGeneratorContext));
    else tokens.add(this.name);
    if (nativeQueryGeneratorContext.isShouldRenderAlias(this.getAs())) tokens.add(this.getAs());
    return renderQuery(tokens);
  }

  @Override
  public String getAs() {
    return this.alias;
  }

  @Override
  public String getFrom() {
    return this.name;
  }

  @Override
  public boolean exist(NativeQueryColumn<?, ?> column) {
    return this.bindColumns.containsKey(column.getName())
        || this.bindColumns.containsKey(column.getColumnName());
  }

  /**
   * Sets table name.
   *
   * @param tableName the table name
   */
  public void setTableName(String tableName) {
    this.name = tableName;
  }

  /**
   * Sets sub query.
   *
   * @param <C> the type parameter
   * @param subQuery the sub query
   */
  public <C extends NativeQuerySelect<T, ?, ?>> void setSubQuery(NativeSubQuery<T, C> subQuery) {
    this.subQuery = subQuery;
  }

  /**
   * Gets string.
   *
   * @param name the name
   * @return the string
   */
  public StringNativeColumn<T> getString(String name) {
    return (StringNativeColumn<T>) get(name, String.class);
  }

  /**
   * Gets string.
   *
   * @param column the column
   * @return the string
   */
  public StringNativeColumn<T> getString(NativeQuerySelect<T, ?, String> column) {
    return (StringNativeColumn<T>) get(column.getName(), String.class);
  }

  /**
   * Gets number.
   *
   * @param <C> the type parameter
   * @param name the name
   * @return the number
   */
  @SuppressWarnings({"unchecked"})
  public <C extends Number> NumberNativeColumn<T, C> getNumber(String name) {
    return (NumberNativeColumn<T, C>) get(name, Number.class);
  }

  /**
   * Gets number.
   *
   * @param <C> the type parameter
   * @param column the column
   * @return the number
   */
  @SuppressWarnings({"unchecked"})
  public <C extends Number> NumberNativeColumn<T, C> getNumber(NativeQuerySelect<T, ?, C> column) {
    return (NumberNativeColumn<T, C>) get(column.getName(), Number.class);
  }

  /**
   * Gets boolean.
   *
   * @param name the name
   * @return the boolean
   */
  public BooleanNativeColumn<T> getBoolean(String name) {
    return (BooleanNativeColumn<T>) get(name, Boolean.class);
  }

  /**
   * Gets boolean.
   *
   * @param column the column
   * @return the boolean
   */
  public BooleanNativeColumn<T> getBoolean(NativeQuerySelect<T, ?, Boolean> column) {
    return (BooleanNativeColumn<T>) get(column.getName(), Boolean.class);
  }

  /**
   * Gets list.
   *
   * @param <C> the type parameter
   * @param name the name
   * @param javaType the java type
   * @return the list
   */
  @SuppressWarnings({"unchecked"})
  public <C> ListNativeColumn<T, C> getList(String name, Class<C> javaType) {
    return (ListNativeColumn<T, C>) get(name, (Class<List<C>>) javaType);
  }

  /**
   * Gets list.
   *
   * @param <C> the type parameter
   * @param column the column
   * @return the list
   */
  @SuppressWarnings({"unchecked"})
  public <C> ListNativeColumn<T, C> getList(NativeQuerySelect<T, ?, C> column) {
    return (ListNativeColumn<T, C>) get(column.getName(), (Class<List<C>>) column.getResultType());
  }

  /**
   * Get native query column.
   *
   * @param <C> the type parameter
   * @param name the name
   * @param type the type
   * @return the native query column
   */
  @SuppressWarnings({"unchecked"})
  public <C> NativeQueryColumn<T, C> get(String name, Class<C> type) {
    if (!this.bindColumns.containsKey(name))
      throw new RuntimeException(
          "Cannot find property `" + name + "` in Table `" + this.getAs() + "`.");
    return (NativeQueryColumn<T, C>) this.bindColumns.get(name);
  }

  @Override
  public NativeQueryTableJoin<?> getJoin() {
    return this.innerJoinList;
  }

  /**
   * Gets possible columns.
   *
   * @return the possible columns
   */
  public Map<String, NativeQueryColumn<T, ?>> getPossibleColumns() {
    return this.bindColumns;
  }

  /**
   * Gets sub query.
   *
   * @return the sub query
   */
  public NativeSubQuery<T, ?> getSubQuery() {
    return subQuery;
  }
}
