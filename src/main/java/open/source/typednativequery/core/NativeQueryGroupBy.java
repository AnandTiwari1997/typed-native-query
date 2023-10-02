package open.source.typednativequery.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/** The type Native query group by. */
public class NativeQueryGroupBy implements IQuery {

  private final List<IQuery> query = new ArrayList<>();
  private boolean useRollup = false;

  /**
   * Instantiates a new Native query group by.
   *
   * @param query the query
   */
  public NativeQueryGroupBy(IQuery... query) {
    this.query.addAll(Arrays.asList(query));
  }

  /**
   * Roll up native query group by.
   *
   * @return the native query group by
   */
  public NativeQueryGroupBy rollUp() {
    this.useRollup = true;
    return this;
  }

  /**
   * Group by native query group by.
   *
   * @param query the query
   * @return the native query group by
   */
  public NativeQueryGroupBy groupBy(IQuery query) {
    this.query.add(query);
    return this;
  }

  @Override
  public String render(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    List<String> groupBy =
        this.query.stream()
            .map(query1 -> query1.render(nativeQueryGeneratorContext))
            .collect(Collectors.toList());
    if (this.useRollup) {
      List<String> tokens = new ArrayList<>();
      tokens.add("ROLLUP");
      tokens.add(wrap(renderQuery(groupBy, ", ")));
      return renderQuery(tokens);
    }
    return renderQuery(groupBy, ", ");
  }
}
