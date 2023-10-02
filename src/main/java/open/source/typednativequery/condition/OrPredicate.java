package open.source.typednativequery.condition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import open.source.typednativequery.core.NativeQueryGeneratorContext;

/** The type Or operator. */
public class OrPredicate implements Predicate {

  private final List<Predicate> conditions = new ArrayList<>();

  /**
   * Instantiates a new Or operator.
   *
   * @param conditions the conditions
   */
  public OrPredicate(List<Predicate> conditions) {
    this.conditions.addAll(conditions);
  }

  /**
   * Or or operator.
   *
   * @param conditions the conditions
   * @return the or operator
   */
  public final OrPredicate or(Predicate... conditions) {
    this.conditions.addAll(Arrays.asList(conditions));
    return this;
  }

  @Override
  public String render(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    List<String> orQuery =
        this.conditions.stream()
            .map(predicateOperator -> predicateOperator.render(nativeQueryGeneratorContext))
            .collect(Collectors.toList());
    return addSpaces(wrap(renderQuery(orQuery, addSpaces("OR"))));
  }
}
