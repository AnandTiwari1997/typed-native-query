package open.source.typednativequery.function;

import java.util.*;
import java.util.stream.Collectors;
import open.source.typednativequery.condition.Predicate;
import open.source.typednativequery.core.IQuery;
import open.source.typednativequery.core.NativeQueryGeneratorContext;
import open.source.typednativequery.core.NativeQuerySelect;
import open.source.typednativequery.metaentity.Table;

/**
 * The type Case operator.
 *
 * @param <T> the type parameter
 * @param <R> the type parameter
 */
public class CaseOperator<T extends Table, R>
    implements NativeQuerySelect<T, CaseOperator<T, R>, R>, IQuery {
  /** The When list. */
  LinkedList<When<R>> whenList = new LinkedList<>();
  /** The Alias. */
  String alias;

  /** Instantiates a new Case operator. */
  public CaseOperator() {}

  /**
   * When case operator.
   *
   * @param condition the condition
   * @return the case operator
   */
  public CaseOperator<T, R> when(Predicate... condition) {
    When<R> when = new When<>(condition);
    this.whenList.add(when);
    return this;
  }

  /**
   * Then value case operator.
   *
   * @param value the value
   * @return the case operator
   */
  public CaseOperator<T, R> thenValue(R value) {
    When<R> whenThenElse = this.whenList.get(this.whenList.size() - 1);
    whenThenElse.thenValue(value);
    return this;
  }

  /**
   * Else value case operator.
   *
   * @param value the value
   * @return the case operator
   */
  public CaseOperator<T, R> elseValue(R value) {
    When<R> whenThenElse = this.whenList.get(this.whenList.size() - 1);
    whenThenElse.elseValue(value);
    return this;
  }

  @Override
  public String render(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    if (this.whenList.isEmpty()) return "";
    List<String> tokens = new ArrayList<>();
    tokens.add("CASE");
    for (When<R> when : this.whenList) {
      List<String> whenTokens = new ArrayList<>();
      whenTokens.add("WHEN");
      whenTokens.add(
          when.conditions.stream()
              .map(predicateOperator -> predicateOperator.render(nativeQueryGeneratorContext))
              .collect(Collectors.joining(" AND ")));
      whenTokens.add("THEN");
      whenTokens.add(transform(when.thenValue, nativeQueryGeneratorContext));
      if (shouldRender(when.elseValue)) {
        whenTokens.add("ELSE");
        whenTokens.add(transform(when.elseValue, nativeQueryGeneratorContext));
      }
      tokens.add(renderQuery(whenTokens));
    }
    tokens.add("END");
    if (nativeQueryGeneratorContext.isShouldRenderAlias(this.getAs())) {
      tokens.add("AS");
      tokens.add(this.getAs());
    }
    return renderQuery(tokens);
  }

  @Override
  public CaseOperator<T, R> as(String alias) {
    this.alias = alias;
    return this;
  }

  @Override
  public String getAs() {
    return this.alias;
  }

  @Override
  public void prepareAlias(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    if (!nativeQueryGeneratorContext.isRenderAble(this.getAs())) {
      this.as(String.format(nativeQueryGeneratorContext.generateAlias("case")));
    }
  }

  /**
   * The type When.
   *
   * @param <R> the type parameter
   */
  public static class When<R> {
    /** The Conditions. */
    List<Predicate> conditions;
    /** The Then value. */
    R thenValue;
    /** The Else value. */
    R elseValue;

    /**
     * Instantiates a new When.
     *
     * @param condition the condition
     */
    public When(Predicate... condition) {
      this.conditions = new ArrayList<>(Arrays.asList(condition));
    }

    /**
     * Then value.
     *
     * @param value the value
     */
    public void thenValue(R value) {
      this.thenValue = value;
    }

    /**
     * Else value.
     *
     * @param value the value
     */
    public void elseValue(R value) {
      this.elseValue = value;
    }
  }
}
