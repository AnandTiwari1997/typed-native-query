package open.source.typednativequery.core;

import java.util.*;
import lombok.ToString;
import open.source.typednativequery.condition.Predicate;
import open.source.typednativequery.metaentity.Table;

/**
 * The type Native query table join.
 *
 * @param <T> the type parameter
 */
@ToString
public class NativeQueryTableJoin<T extends Table> extends NativeQueryTable<T> implements IQuery {
  private final Set<Predicate> conditions = new LinkedHashSet<>();
  private final NativeQueryTableJoinType joinType;

  /**
   * Instantiates a new Native query table join.
   *
   * @param from the from
   * @param joinType the join type
   */
  public NativeQueryTableJoin(NativeQueryTable<T> from, NativeQueryTableJoinType joinType) {
    super(from);
    this.joinType = joinType;
  }

  /**
   * Instantiates a new Native query table join.
   *
   * @param from the from
   * @param joinType the join type
   */
  public <C extends NativeQuerySelect<T, ?, ?>> NativeQueryTableJoin(
      NativeSubQuery<T, C> from, NativeQueryTableJoinType joinType) {
    super(from, null);
    this.joinType = joinType;
  }

  /**
   * Instantiates a new Native query table join.
   *
   * @param className the class name
   * @param joinType the join type
   */
  public NativeQueryTableJoin(Class<T> className, NativeQueryTableJoinType joinType) {
    super(className);
    this.joinType = joinType;
  }

  @Override
  public NativeQueryTableJoin<T> as(String alias) {
    super.as(alias);
    return this;
  }

  /**
   * On native query table join.
   *
   * @param condition the condition
   * @return the native query table join
   */
  public NativeQueryTableJoin<T> on(Predicate... condition) {
    this.conditions.addAll(Arrays.asList(condition));
    return this;
  }

  public String render(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    List<String> tokens = new ArrayList<>();
    tokens.add(this.joinType.getJoinName());
    tokens.add(renderWithoutJoin(nativeQueryGeneratorContext));
    tokens.add(nativeQueryGeneratorContext.renderOn(this.conditions));
    if (shouldRender(getJoin())) tokens.add(getJoin().render(nativeQueryGeneratorContext));
    return renderQuery(tokens);
  }
}
