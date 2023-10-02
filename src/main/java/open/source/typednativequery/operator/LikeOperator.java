package open.source.typednativequery.operator;

import java.util.ArrayList;
import java.util.List;
import open.source.typednativequery.condition.Condition;
import open.source.typednativequery.core.IQuery;
import open.source.typednativequery.core.NativeQueryGeneratorContext;
import open.source.typednativequery.expressions.StringFunctionExpression;

/** The type Like operator. */
public class LikeOperator implements Condition<LikeOperator> {

  private final IQuery leftOperand;
  private final IQuery rightOperand;
  private String clause = "LIKE";

  /**
   * Instantiates a new Like operator.
   *
   * @param leftOperand the left operand
   * @param rightOperand the right operand
   */
  public LikeOperator(StringFunctionExpression<?> leftOperand, IQuery rightOperand) {
    this.leftOperand = leftOperand;
    this.rightOperand = rightOperand;
  }

  @Override
  public LikeOperator not() {
    this.clause = "NOT LIKE";
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
