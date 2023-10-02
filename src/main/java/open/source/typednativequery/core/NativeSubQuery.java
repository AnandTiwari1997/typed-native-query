package open.source.typednativequery.core;

import open.source.typednativequery.metaentity.Table;

/**
 * The type Native sub query.
 *
 * @param <T> the type parameter
 * @param <E> the type parameter
 */
public class NativeSubQuery<T extends Table, E extends NativeQuerySelect<T, ?, ?>>
    extends NativeQuery<T, E> implements QueryAlias<NativeSubQuery<T, E>> {

  /** The Alias. */
  String alias;
  /** The Entity. */
  Class<T> entity;

  /**
   * Instantiates a new Native sub query.
   *
   * @param className the class name
   */
  NativeSubQuery(Class<T> className) {
    this.entity = className;
  }

  /**
   * Instantiates a new Native sub query.
   *
   * @param customQuery the custom query
   */
  NativeSubQuery(NativeQuery<?, ?> customQuery) {
    super(
        customQuery.isDistinct(),
        customQuery.getOffset(),
        customQuery.getLimit(),
        customQuery.getTables(),
        customQuery.getExpressions(),
        customQuery.getSelection(),
        customQuery.getOrderBy(),
        customQuery.getGroupBy(),
        customQuery.getWith(),
        customQuery.getNativeQueryGeneratorContext());
  }

  @Override
  public NativeSubQuery<T, E> as(String alias) {
    this.alias = alias;
    return this;
  }

  @Override
  public String render(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    return String.format("( %s )", super.render(nativeQueryGeneratorContext));
  }

  @Override
  public void prepareAlias(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    if (!nativeQueryGeneratorContext.isRenderAble(this.getAs())) {
      as(nativeQueryGeneratorContext.generateAlias("custom_sub_query"));
    }
  }

  @Override
  public String getAs() {
    return this.alias;
  }

  /**
   * Gets entity.
   *
   * @return the entity
   */
  public Class<T> getEntity() {
    return this.entity;
  }
}
