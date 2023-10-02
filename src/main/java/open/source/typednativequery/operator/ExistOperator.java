package open.source.typednativequery.operator;

import java.util.List;
import open.source.typednativequery.condition.Condition;
import open.source.typednativequery.core.IQuery;
import open.source.typednativequery.core.NativeQueryGeneratorContext;

/** The type Exist operator. */
public class ExistOperator implements Condition<ExistOperator> {

  private final IQuery query;
  private String clause = "EXISTS";

  /**
   * Instantiates a new Exist operator.
   *
   * @param query the query
   */
  public ExistOperator(IQuery query) {
    this.query = query;
  }

  @Override
  public String render(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    List<String> tokens = List.of(this.clause, this.query.render(nativeQueryGeneratorContext));
    return renderQuery(tokens);
  }

  @Override
  public ExistOperator not() {
    this.clause = "NOT EXISTS";
    return this;
  }
}
