package open.source.typednativequery.function.window;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import open.source.typednativequery.columns.NativeQueryColumn;
import open.source.typednativequery.core.IQuery;
import open.source.typednativequery.core.NativeQueryGeneratorContext;
import open.source.typednativequery.core.NativeQueryOrderBy;
import open.source.typednativequery.function.arithmatic.AggregateFunction;
import open.source.typednativequery.metaentity.Table;

/**
 * The type Window function.
 *
 * @param <T> the type parameter
 * @param <I> the type parameter
 * @param <O> the type parameter
 */
public abstract class WindowFunction<T extends Table, I, O extends Number>
    implements AggregateFunction<T, I, O>, IQuery {

  /** The Partition by. */
  List<NativeQueryColumn<T, ?>> partitionBy = new ArrayList<>();
  /** The Order by. */
  List<NativeQueryOrderBy> orderBy = new ArrayList<>();

  private boolean applyOver = false;
  private I type;

  /**
   * Over .
   *
   * @return the
   */
  public abstract I over();

  /**
   * Over .
   *
   * @param type the type
   * @return the
   */
  public I over(I type) {
    this.applyOver = true;
    this.type = type;
    return type;
  }

  /**
   * Partition by .
   *
   * @param <U> the type parameter
   * @param columns the columns
   * @return the
   */
  @SafeVarargs
  public final <U> I partitionBy(NativeQueryColumn<T, U>... columns) {
    this.partitionBy.addAll(Arrays.asList(columns));
    return type;
  }

  /**
   * Order by .
   *
   * @param order the order
   * @return the
   */
  public final I orderBy(NativeQueryOrderBy... order) {
    this.orderBy.addAll(Arrays.asList(order));
    return type;
  }

  @Override
  public String render(NativeQueryGeneratorContext nativeQueryGeneratorContext) {
    List<String> upperTokens = new ArrayList<>();
    if (this.applyOver) {
      upperTokens.add("OVER");
      List<String> tokens = new ArrayList<>();
      if (!this.partitionBy.isEmpty()) {
        tokens.add("PARTITION BY");
        List<String> columns =
            this.partitionBy.stream()
                .map(tNativeQueryColumn -> tNativeQueryColumn.render(nativeQueryGeneratorContext))
                .collect(Collectors.toList());
        tokens.add(renderQuery(columns, ", "));
      }
      if (!this.orderBy.isEmpty()) {
        tokens.add("ORDER BY");
        List<String> orders =
            this.orderBy.stream()
                .map(nativeQueryOrderBy -> nativeQueryOrderBy.render(nativeQueryGeneratorContext))
                .collect(Collectors.toList());
        tokens.add(renderQuery(orders, ", "));
      }
      upperTokens.add(wrap(renderQuery(tokens)));
    }
    return renderQuery(upperTokens);
  }
}
