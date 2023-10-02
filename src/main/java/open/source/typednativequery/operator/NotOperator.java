package open.source.typednativequery.operator;

import java.util.List;
import open.source.typednativequery.condition.Condition;
import open.source.typednativequery.core.IQuery;
import open.source.typednativequery.core.NativeQueryGeneratorContext;
import open.source.typednativequery.function.Function;

/** The type Not operator. */
public class NotOperator implements Condition<NotOperator> {

  private final IQuery query;

  /**
   * Instantiates a new Not operator.
   *
   * @param query the query
   */
  public NotOperator(IQuery query) {
    this.query = query;
  }

  @Override
  public String render(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    if (this.query instanceof Function) {
      throw new RuntimeException("Cannot applied not operator on functions.");
    }
    if (this.query instanceof Condition) {
      return ((IQuery) ((Condition<?>) this.query).not()).render(nativeQueryGeneratorContext);
    }
    List<String> tokens = List.of("NOT", this.query.render(nativeQueryGeneratorContext));
    return renderQuery(tokens);
  }

  @Override
  public NotOperator not() {
    return this;
  }
}
