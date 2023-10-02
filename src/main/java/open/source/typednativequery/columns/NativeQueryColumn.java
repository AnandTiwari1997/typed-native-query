package open.source.typednativequery.columns;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.SneakyThrows;
import open.source.typednativequery.condition.Condition;
import open.source.typednativequery.operator.InOperator;
import open.source.typednativequery.core.*;
import open.source.typednativequery.expressions.CommonOperatorExpression;
import open.source.typednativequery.function.CaseOperator;
import open.source.typednativequery.metaentity.Table;
import open.source.typednativequery.operator.EqualsOperator;
import open.source.typednativequery.operator.NullOperator;
import org.apache.commons.lang3.StringUtils;

/**
 * The type Native query column.
 *
 * @param <T> the type parameter
 * @param <C> the type parameter
 */
public class NativeQueryColumn<T extends Table, C>
    implements NativeQuerySelect<T, NativeQueryColumn<T, C>, C>,
        CommonOperatorExpression<T, C>,
        IQuery {

  private String name;
  private final Class<C> javaType;
  private String alias;
  private NativeQueryTable<T> bindTable;
  private boolean isPrimitive = false;
  private boolean isListCollection = false;

  /**
   * Instantiates a new Native query column.
   *
   * @param name the name
   * @param javaType the java type
   */
  protected NativeQueryColumn(String name, Class<C> javaType) {
    this.name = name;
    this.javaType = javaType;
    this.isPrimitive = true;
    this.isListCollection = false;
  }

  /**
   * Instantiates a new Native query column.
   *
   * @param name the name
   * @param javaType the java type
   * @param isListCollection the is list collection
   */
  protected NativeQueryColumn(String name, Class<C> javaType, boolean isListCollection) {
    this.name = name;
    this.javaType = javaType;
    this.isListCollection = isListCollection;
  }

  @Override
  public String render(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    List<String> tokens = new ArrayList<>();
    String columnQuery = String.format("%s.%s", this.getBindTable().getAs(), this.getColumnName());
    tokens.add(columnQuery);
    if (nativeQueryGeneratorContext.isShouldRenderAlias(this.getAs())) {
      tokens.add("AS");
      tokens.add(this.getAs());
    }
    return renderQuery(tokens);
  }

  @Override
  public NativeQueryColumn<T, C> as(String alias) {
    this.alias = alias;
    return this;
  }

  @Override
  public String getAs() {
    return this.alias;
  }

  @Override
  public void prepareAlias(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    // Nothing to do here
  }

  @Override
  public String getName() {
    if (StringUtils.isNotBlank(this.getAs())) return this.getAs();
    return this.name;
  }

  @Override
  public Class<C> getResultType() {
    return this.javaType;
  }

  @Override
  public NativeQueryColumn<T, C> get() {
    return this;
  }

  @Override
  public boolean isPrimitive() {
    return this.isPrimitive;
  }

  @Override
  public boolean isListCollection() {
    return this.isListCollection;
  }

  /**
   * Gets bind table.
   *
   * @return the bind table
   */
  public NativeQueryTable<T> getBindTable() {
    return this.bindTable;
  }

  /**
   * Gets column name.
   *
   * @return the column name
   */
  public String getColumnName() {
    return this.name;
  }

  /**
   * Update name.
   *
   * @param name the name
   */
  public void updateName(String name) {
    this.name = name;
  }

  /**
   * Bind table.
   *
   * @param table the table
   */
  public void bindTable(NativeQueryTable<T> table) {
    this.bindTable = table;
  }

  /**
   * Build native query column.
   *
   * @param <T> the type parameter
   * @param <C> the type parameter
   * @param name the name
   * @param javaType the java type
   * @param isPrimitive the is primitive
   * @param isList the is list
   * @return the native query column
   */
  @SneakyThrows
  @SuppressWarnings({"unchecked"})
  public static <T extends Table, C> NativeQueryColumn<T, C> build(
      String name, Class<C> javaType, boolean isPrimitive, boolean isList) {
    if (isPrimitive) {
      if (String.class.equals(javaType)) {
        return (NativeQueryColumn<T, C>) new StringNativeColumn<T>(name, String.class);
      } else if (Boolean.class.equals(javaType)) {
        return (NativeQueryColumn<T, C>) new BooleanNativeColumn<T>(name, Boolean.class);
      } else if (Number.class.equals(javaType.getSuperclass()) || Number.class.equals(javaType)) {
        return (NativeQueryColumn<T, C>) new NumberNativeColumn<T, Number>(name, Number.class);
      }
    } else if (isList) {
      return (NativeQueryColumn<T, C>) new ListNativeColumn<T, C>(name, javaType);
    }
    return new NativeQueryColumn<>(name, javaType);
  }

  @Override
  public <U extends Table> Condition<EqualsOperator> equal(NativeQueryColumn<U, C> rightOperand) {
    return new EqualsOperator(this, rightOperand);
  }

  @Override
  public <R extends Table, E extends NativeQuerySelect<R, ?, C>> Condition<EqualsOperator> equal(
      NativeSubQuery<R, E> rightOperand) {
    return new EqualsOperator(this, rightOperand);
  }

  @Override
  public Condition<EqualsOperator> equal(C value) {
    return new EqualsOperator(this, new CustomQueryParameter<>(value));
  }

  @Override
  public Condition<EqualsOperator> equal(CustomQueryParameter<C> value) {
    return new EqualsOperator(this, value);
  }

  @Override
  public Condition<EqualsOperator> equal(NamedQueryParameter value) {
    return new EqualsOperator(this, value);
  }

  @Override
  public NativeQueryOrderBy asc() {
    return new NativeQueryOrderBy(this, true);
  }

  @Override
  public NativeQueryOrderBy desc() {
    return new NativeQueryOrderBy(this, false);
  }

  @Override
  public Condition<NullOperator<T>> isNotNull() {
    return new NullOperator<>(this).not();
  }

  @Override
  public Condition<NullOperator<T>> isNull() {
    return new NullOperator<>(this);
  }

  @Override
  public <U extends Table> InOperator<T, U, C> in(
      NativeSubQuery<U, NativeQueryColumn<U, C>> value) {
    return new InOperator<>(this, value);
  }

  @Override
  public InOperator<T, T, C> in(Collection<C> value) {
    return new InOperator<>(this, value);
  }

  @Override
  public <U extends Table> InOperator<T, U, C> notIn(
      NativeSubQuery<U, NativeQueryColumn<U, C>> value) {
    return new InOperator<>(this, value).not();
  }

  @Override
  public InOperator<T, T, C> notIn(Collection<C> value) {
    return new InOperator<T, T, C>(this, value).not();
  }

  @Override
  public <R> CaseOperator<T, R> sqlCase(Condition.Conditions conditions, C value, R thenValue) {
    return new CaseOperator<T, R>().when(conditions.getOperator(this, value)).thenValue(thenValue);
  }
}
