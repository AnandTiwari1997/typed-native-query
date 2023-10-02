package open.source.typednativequery.expressions;

import open.source.typednativequery.condition.Condition;
import open.source.typednativequery.operator.UnaryOperator;

/** The interface Boolean ops. */
public interface BooleanOps {

  /**
   * Is true condition.
   *
   * @return the condition
   */
  Condition<UnaryOperator<Boolean>> isTrue();

  /**
   * Is false condition.
   *
   * @return the condition
   */
  Condition<UnaryOperator<Boolean>> isFalse();
}
