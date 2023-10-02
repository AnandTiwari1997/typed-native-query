/*
 * This Source Code is generated as part of Open Source Project.
 */

package open.source.typednativequery.operator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import open.source.typednativequery.columns.NativeQueryColumn;
import open.source.typednativequery.condition.Condition;
import open.source.typednativequery.core.NativeQueryGeneratorContext;
import open.source.typednativequery.core.NativeQuerySelect;
import open.source.typednativequery.core.NativeSubQuery;
import open.source.typednativequery.metaentity.Table;

/**
 * The type In operator.
 *
 * @param <T> the type parameter
 * @param <U> the type parameter
 * @param <R> the type parameter
 */
public class InOperator<T extends Table, U extends Table, R>
    implements Condition<InOperator<T, U, R>> {

  private final NativeQuerySelect<T, ?, R> select;
  private NativeSubQuery<U, NativeQueryColumn<U, R>> value;
  private Collection<R> collection;
  private String clause = "IN";

  /**
   * Instantiates a new In operator.
   *
   * @param select the select
   */
  public <C> InOperator(NativeQuerySelect<T, C, R> select) {
    this.select = select;
  }

  /**
   * Instantiates a new In operator.
   *
   * @param select the select
   * @param value the value
   */
  public <C> InOperator(
      NativeQuerySelect<T, C, R> select, NativeSubQuery<U, NativeQueryColumn<U, R>> value) {
    this.select = select;
    this.value = value;
  }

  /**
   * Instantiates a new In operator.
   *
   * @param select the select
   * @param collection the collection
   */
  public <C> InOperator(NativeQuerySelect<T, C, R> select, Collection<R> collection) {
    this.select = select;
    this.collection = collection;
  }

  /**
   * Value in operator.
   *
   * @param value the value
   * @return the in operator
   */
  public InOperator<T, U, R> value(NativeSubQuery<U, NativeQueryColumn<U, R>> value) {
    this.value = value;
    return this;
  }

  /**
   * Value in operator.
   *
   * @param collection the collection
   * @return the in operator
   */
  public InOperator<T, U, R> value(Collection<R> collection) {
    this.collection = collection;
    return this;
  }

  public InOperator<T, U, R> not() {
    this.clause = "NOT IN";
    return this;
  }

  @Override
  public String render(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    List<String> tokens = new ArrayList<>();
    tokens.add(this.select.render(nativeQueryGeneratorContext));
    tokens.add(this.clause);
    if (shouldRender(this.value)) {
      tokens.add(wrap(String.valueOf(this.value.render(nativeQueryGeneratorContext))));
    } else if (shouldRender(this.collection)) {
      tokens.add(
          wrap(
              renderQuery(
                  this.collection.stream().map(String::valueOf).collect(Collectors.toList()),
                  ", ")));
    }
    return renderQuery(tokens);
  }
}
