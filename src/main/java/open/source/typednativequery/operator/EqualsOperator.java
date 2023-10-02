package open.source.typednativequery.operator;

import java.util.ArrayList;
import java.util.List;
import open.source.typednativequery.columns.NativeQueryColumn;
import open.source.typednativequery.condition.Condition;
import open.source.typednativequery.core.IQuery;
import open.source.typednativequery.core.NativeQueryGeneratorContext;

/** The type Equals operator. */
public class EqualsOperator implements Condition<EqualsOperator> {

  private final NativeQueryColumn<?, ?> leftOperand;
  private final IQuery rightOperand;
  private String clause = "=";

  /**
   * Instantiates a new Equals operator.
   *
   * @param leftOperand the left operand
   * @param rightOperand the right operand
   */
  public EqualsOperator(NativeQueryColumn<?, ?> leftOperand, IQuery rightOperand) {
    this.leftOperand = leftOperand;
    this.rightOperand = rightOperand;
  }

  @Override
  public EqualsOperator not() {
    this.clause = "!=";
    return this;
  }

  @Override
  public String render(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    List<String> tokens = new ArrayList<>();
    tokens.add(this.leftOperand.render(nativeQueryGeneratorContext));
    tokens.add(this.clause);
    tokens.add(this.rightOperand.render(nativeQueryGeneratorContext));
    return renderQuery(tokens);
  }
}
