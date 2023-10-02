package open.source.typednativequery.operator;

import java.util.ArrayList;
import java.util.List;
import open.source.typednativequery.condition.Condition;
import open.source.typednativequery.core.NativeQueryGeneratorContext;
import open.source.typednativequery.core.NativeQuerySelect;
import open.source.typednativequery.metaentity.Table;

/**
 * The type Null operator.
 *
 * @param <T> the type parameter
 */
public class NullOperator<T extends Table> implements Condition<NullOperator<T>> {
  private final NativeQuerySelect<T, ?, ?> select;
  private String clause = "IS NULL";

  /**
   * Instantiates a new Null operator.
   *
   * @param select the select
   */
  public <C, R> NullOperator(NativeQuerySelect<T, C, R> select) {
    this.select = select;
  }

  public NullOperator<T> not() {
    this.clause = "IS NOT NULL";
    return this;
  }

  @Override
  public String render(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    List<String> tokens = new ArrayList<>();
    tokens.add(this.select.render(nativeQueryGeneratorContext));
    tokens.add(this.clause);
    return renderQuery(tokens);
  }
}
