package open.source.typednativequery.operator;

import java.util.List;
import open.source.typednativequery.columns.NativeQueryColumn;
import open.source.typednativequery.condition.Condition;
import open.source.typednativequery.core.NativeQueryGeneratorContext;

/**
 * The type Unary operator.
 *
 * @param <C> the type parameter
 */
public class UnaryOperator<C> implements Condition<UnaryOperator<C>> {

  private final NativeQueryColumn<?, C> select;
  /** The Negated. */
  boolean negated;
  /** The Clause. */
  String clause = "";

  /**
   * Instantiates a new Unary operator.
   *
   * @param select the select
   */
  public UnaryOperator(NativeQueryColumn<?, C> select) {
    this.select = select;
  }

  @Override
  public String render(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    List<String> tokens = List.of(this.clause, this.select.render(nativeQueryGeneratorContext));
    return renderQuery(tokens);
  }

  @Override
  public UnaryOperator<C> not() {
    this.clause = "NOT";
    return this;
  }
}
