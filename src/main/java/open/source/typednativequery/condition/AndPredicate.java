/*
 * This Source Code is generated as part of Open Source Project.
 */

package open.source.typednativequery.condition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import open.source.typednativequery.core.NativeQueryGeneratorContext;

/** The type And operator. */
public class AndPredicate implements Predicate {

  private final List<Predicate> conditions = new ArrayList<>();

  /**
   * Instantiates a new And operator.
   *
   * @param conditions the conditions
   */
  public AndPredicate(List<Predicate> conditions) {
    super();
    this.conditions.addAll(conditions);
  }

  /**
   * @param nativeQueryGeneratorContext NativeQueryGeneratorContext
   * @return String
   */
  @Override
  public String render(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    List<String> andQuery =
        this.conditions.stream()
            .map(predicateOperator -> predicateOperator.render(nativeQueryGeneratorContext))
            .collect(Collectors.toList());
    return addSpaces(wrap(renderQuery(andQuery, addSpaces("AND"))));
  }

  /**
   * And operator.
   *
   * @param conditions the conditions
   * @return the and operator
   */
  public AndPredicate and(Predicate... conditions) {
    this.conditions.addAll(Arrays.asList(conditions));
    return this;
  }
}
